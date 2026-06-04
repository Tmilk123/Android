<#
.SYNOPSIS
    自动实测视频起播性能：冷启动 vs 预加载对比
.DESCRIPTION
    安装 APK → 启动 App → 模拟滑动 10+ 个视频 → 采集 logcat → 解析 MetricsCSV → 输出聚合报告
.PREREQUISITES
    - Android 模拟器或真机已通过 adb 连接
    - APK 已编译（./gradlew assembleDebug）
.EXAMPLE
    .\tools\measure_performance.ps1
#>

param(
    [string]$ApkPath = "app/build/outputs/apk/debug/app-debug.apk",
    [int]$SwipeCount = 12,
    [int]$WatchDuration = 3,
    [int]$SwipeDuration = 300
)

$ErrorActionPreference = "Stop"
$PACKAGE = "com.example.myapplication"
$ACTIVITY = "$PACKAGE.MainActivity"

# ── 1. Check prerequisites ──────────────────────────────────────────
Write-Host "`n[1/6] Checking prerequisites..." -ForegroundColor Cyan

$adb = if (Get-Command adb -ErrorAction SilentlyContinue) { "adb" }
elseif (Test-Path "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe") { "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" }
else { throw "adb not found. Install Android SDK platform-tools or add to PATH." }

$devices = & $adb devices | Select-String -Pattern "device$"
if (-not $devices) { throw "No Android device connected. Start an emulator or connect a device." }
Write-Host "  adb: $adb" -ForegroundColor Gray
Write-Host "  Device: $($devices -replace '\s+device','')" -ForegroundColor Gray

if (-not (Test-Path $ApkPath)) { throw "APK not found at: $ApkPath. Run './gradlew assembleDebug' first." }
Write-Host "  APK: $ApkPath ($([math]::Round((Get-Item $ApkPath).Length/1KB, 1)) KB)" -ForegroundColor Gray

# ── 2. Install & Launch ──────────────────────────────────────────────
Write-Host "`n[2/6] Installing APK..." -ForegroundColor Cyan
& $adb install -r -d $ApkPath 2>&1 | Out-Null
Write-Host "  Installed." -ForegroundColor Gray

Write-Host "`n[3/6] Clearing logcat and launching app..." -ForegroundColor Cyan
& $adb logcat -c
Start-Sleep -Seconds 1
& $adb shell am start -n $ACTIVITY 2>&1 | Out-Null
Write-Host "  App launched. Waiting 4s for first video to start..." -ForegroundColor Gray
Start-Sleep -Seconds 4

# ── 3. Simulate swiping ──────────────────────────────────────────────
Write-Host "`n[4/6] Simulating $SwipeCount vertical swipes (TikTok-style)..." -ForegroundColor Cyan

# Get screen dimensions
$dims = & $adb shell wm size 2>&1
$width = 540; $height = 960  # defaults
if ($dims -match '(\d+)x(\d+)') {
    $width = [int]$Matches[1]
    $height = [int]$Matches[2]
}

$midX = [int]($width / 2)
$startY = [int]($height * 0.75)
$endY = [int]($height * 0.25)

Write-Host "  Screen: ${width}x${height}, swipe $midX:${startY} -> $midX:${endY}" -ForegroundColor Gray

for ($i = 1; $i -le $SwipeCount; $i++) {
    & $adb shell input swipe $midX $startY $midX $endY $SwipeDuration
    Write-Host "  Swipe $i/$SwipeCount" -ForegroundColor Gray
    Start-Sleep -Seconds $WatchDuration
}

# Wait for any pending metrics to flush
Start-Sleep -Seconds 2

# ── 4. Collect logcat ────────────────────────────────────────────────
Write-Host "`n[5/6] Collecting logcat metrics..." -ForegroundColor Cyan

$logcatOut = & $adb logcat -d -s MetricsCSV:D PlayerManager:D 2>&1

# ── 5. Parse CSV metrics ─────────────────────────────────────────────
$csvLines = $logcatOut | Select-String -Pattern "^[^:]+:\s*.*,\s*cold=" | ForEach-Object {
    # Extract: videoId,cold=X,preload=Y,hit=true/false,improve=Z%
    if ($_.Line -match '([\w_]+),\s*cold=(\d+),\s*preload=(\d+),\s*hit=(true|false),\s*improve=(\d+)%') {
        [PSCustomObject]@{
            VideoId    = $Matches[1]
            ColdMs     = [long]$Matches[2]
            PreloadMs  = [long]$Matches[3]
            IsPreload  = $Matches[4] -eq 'true'
            ImprovePct = [int]$Matches[5]
        }
    }
}

if ($csvLines.Count -eq 0) {
    Write-Host "  WARNING: No MetricsCSV lines found in logcat!" -ForegroundColor Yellow
    Write-Host "  Raw logcat (last 30 lines):" -ForegroundColor Gray
    $logcatOut | Select-Object -Last 30 | ForEach-Object { Write-Host "    $_" -ForegroundColor Gray }
}
else {
    # ── 6. Compute aggregate stats ────────────────────────────────────
    $coldEntries = $csvLines | Where-Object { -not $_.IsPreload }
    $preloadEntries = $csvLines | Where-Object { $_.IsPreload }

    $avgCold = if ($coldEntries.Count -gt 0) { [math]::Round(($coldEntries | Measure-Object -Property ColdMs -Average).Average, 1) } else { 0 }
    $avgPreload = if ($preloadEntries.Count -gt 0) { [math]::Round(($preloadEntries | Measure-Object -Property PreloadMs -Average).Average, 1) } else { 0 }
    $avgImprove = if ($csvLines.Count -gt 0) { [math]::Round(($csvLines | Measure-Object -Property ImprovePct -Average).Average, 1) } else { 0 }
    $hitRate = if ($csvLines.Count -gt 0) { [math]::Round(($preloadEntries.Count / $csvLines.Count) * 100, 1) } else { 0 }
    $bestImprove = if ($csvLines.Count -gt 0) { ($csvLines | Sort-Object ImprovePct -Descending | Select-Object -First 1).ImprovePct } else { 0 }

    Write-Host "`n[6/6] Results" -ForegroundColor Cyan
    Write-Host "══════════════════════════════════════════════════" -ForegroundColor White
    Write-Host "  Total measurements       : $($csvLines.Count)" -ForegroundColor White
    Write-Host "  Cold start entries       : $($coldEntries.Count)" -ForegroundColor White
    Write-Host "  Preload hit entries      : $($preloadEntries.Count)" -ForegroundColor White
    Write-Host "  Preload hit rate         : ${hitRate}%" -ForegroundColor $(if ($hitRate -ge 50) { "Green" } else { "Yellow" })
    Write-Host "  ─────────────────────────────────────────────" -ForegroundColor Gray
    Write-Host "  Avg cold start           : ${avgCold} ms" -ForegroundColor Red
    Write-Host "  Avg preload start        : ${avgPreload} ms" -ForegroundColor Green
    Write-Host "  Avg improvement          : ${avgImprove}%" -ForegroundColor $(if ($avgImprove -ge 70) { "Green" } else { "Yellow" })
    Write-Host "  Best improvement         : ${bestImprove}%" -ForegroundColor Green
    Write-Host "══════════════════════════════════════════════════" -ForegroundColor White

    # Save results to JSON
    $result = @{
        timestamp = (Get-Date -Format "yyyy-MM-ddTHH:mm:ss")
        device = ($devices -replace '\s+device','')[0]
        totalMeasurements = $csvLines.Count
        coldStartCount = $coldEntries.Count
        preloadHitCount = $preloadEntries.Count
        preloadHitRatePct = $hitRate
        avgColdStartMs = $avgCold
        avgPreloadMs = $avgPreload
        avgImprovementPct = $avgImprove
        bestImprovementPct = $bestImprove
        details = $csvLines | Select-Object VideoId, ColdMs, PreloadMs, IsPreload, ImprovePct
    }
    $resultFile = "tools/measurement_result.json"
    $result | ConvertTo-Json -Depth 3 | Out-File -Encoding utf8 $resultFile
    Write-Host "`n  Results saved to: $resultFile" -ForegroundColor Gray

    # Summary for copy-paste into metrics.json
    Write-Host "`n  Copy these into metrics.json:" -ForegroundColor Cyan
    Write-Host "  ─────────────────────────────────────────────" -ForegroundColor Gray
    Write-Host "  avgColdStartMs: ${avgCold}" -ForegroundColor White
    Write-Host "  avgPreloadMs: ${avgPreload}" -ForegroundColor White
    Write-Host "  avgImprovementPct: ${avgImprove}" -ForegroundColor White
    Write-Host "  preloadHitRate: ${hitRate}%" -ForegroundColor White
    Write-Host "  ─────────────────────────────────────────────" -ForegroundColor Gray
}

Write-Host "`nDone.`n" -ForegroundColor Green
