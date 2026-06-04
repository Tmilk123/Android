# 实测工具

## 前置条件

1. Android 模拟器或真机已通过 `adb` 连接
2. APK 已编译
3. （PowerShell 脚本）Windows + PowerShell 5.1+
4. （Python 脚本）Python 3.8+

## 快速实测

### 方式一：全自动（PowerShell）

```powershell
# 1. 编译 APK
.\gradlew.bat assembleDebug

# 2. 运行自动化实测
.\tools\measure_performance.ps1
```

脚本会自动完成：
- 安装 APK → 启动 App → 模拟上下滑动 12 个视频 → 采集 logcat → 解析 MetricsCSV → 输出聚合报告

结果保存在 `tools/measurement_result.json`。

### 方式二：手动采集 + Python 解析

```bash
# 1. 清空旧日志并启动 App
adb logcat -c
adb shell am start -n com.example.myapplication/.MainActivity

# 2. 手动上下滑动浏览 10+ 个视频

# 3. 导出日志
adb logcat -d -s MetricsCSV:D > tools/logcat_raw.txt

# 4. 解析
python tools/parse_metrics.py tools/logcat_raw.txt
```

### 方式三：App 内查看

在 App 中播放 10+ 个视频后：
1. 点击右上角「**起播指标**」→ 查看当前视频的实时起播数据
2. 点击「**📊 更多指标**」→ 进入 MetricsDashboard 查看聚合统计

聚合统计包含：
- 总记录数
- 预加载命中率
- 平均冷启动耗时
- 平均预加载起播耗时
- 平均优化幅度

## 输出数据说明

| 字段 | 说明 |
|------|------|
| `avgColdStartMs` | 冷启动起播平均耗时（ms）：从 `prepare()` 到 `STATE_READY` |
| `avgPreloadMs` | 预加载起播平均耗时（ms）：从提升预加载播放器到可播放 |
| `avgImprovementPct` | 平均优化幅度（%）：`(冷启动 - 预加载) / 冷启动 × 100%` |
| `preloadHitRatePct` | 预加载命中率（%）：走预加载路径的比例 |

## 如何分别测试"无优化"和"有优化"

修改 `app/src/main/java/com/example/myapplication/ui/feed/FeedScreen.kt`：

```kotlin
// 第 82 行附近，注释掉预加载逻辑即可测试无优化版本：
// playerManager.preloadNext(nextItem.item)  // ← 注释掉这行
```

分别构建两个 APK 进行对比测试。
