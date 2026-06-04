# 运行手册

## Android Studio 打开方式

1. 打开 Android Studio。
2. 选择 `Open`。
3. 选择项目目录：

```text
D:\android-studio-projects\MyApplication
```

4. 等待 Gradle Sync 完成。
5. 如果首次打开下载依赖较慢，请保持网络可用。

## 编译方式

在项目根目录执行：

```powershell
.\gradlew.bat assembleDebug
```

运行单元测试：

```powershell
.\gradlew.bat testDebugUnitTest
```

输出 APK 位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 运行方式

### Android Studio 运行

1. 启动 Android 模拟器或连接真机。
2. 选择 `app` 运行配置。
3. 点击 Run。
4. App 启动后默认进入 FeedScreen。

### 命令行安装

```powershell
.\gradlew.bat installDebug
```

## 模拟器注意事项

- 建议使用 Android 8.0 及以上模拟器。
- 视频播放需要模拟器可以访问网络。
- 如果视频黑屏或无法播放，先确认模拟器浏览器能访问外网。
- 如果模拟器性能较低，视频起播可能较慢。
- 建议录屏前先滑动几次 Feed，让图片和视频资源完成首次加载。

## 常见问题排查

### 1. Gradle Sync 慢或失败

检查网络连接，然后重新 Sync。也可以执行：

```powershell
.\gradlew.bat --refresh-dependencies
```

### 2. 编译时看到 AGP warning

当前项目为了适配 Room + KSP，在 `gradle.properties` 中设置了：

```properties
android.builtInKotlin=false
android.newDsl=false
```

因此可能出现 AGP 9 的弃用 warning。只要 `assembleDebug` 成功即可。

### 3. 页面没有搜索历史

搜索历史保存在 Room 中。需要先进入 SearchScreen，输入关键词并搜索一次，才会出现历史记录。

### 4. 搜索结果为空

Fake 数据只覆盖部分关键词。可以尝试：

```text
城市
旅行
美食
科技
电影
音乐
运动
学习
摄影
新闻
```

### 5. 点击搜索结果没有跳到视频

确认搜索结果项是视频结果。点击后路由应跳转到：

```text
feed?targetId={videoId}
```

FeedScreen 会加载分页并定位到对应视频。

## 视频无法播放时如何检查 URL

1. 打开 `FakeFeedRepository.kt`。
2. 找到 `testVideoUrls` 列表。
3. 复制其中一个 mp4 URL 到浏览器中访问。
4. 如果浏览器无法打开，说明当前网络无法访问该视频资源。
5. 可以临时替换为其他可访问的 mp4 测试地址。

排查顺序：

- 模拟器是否联网。
- App 是否有 `INTERNET` 权限。
- 视频 URL 是否可访问。
- 是否正在当前 Feed 视频页。
- 是否被切到搜索页或图文页后暂停。

## Room 数据如何清理

### 方式 1：卸载 App

卸载 App 会清除 Room 数据库：

```powershell
adb uninstall com.example.myapplication
```

然后重新安装运行。

### 方式 2：系统设置清除数据

在模拟器或手机中：

1. 打开系统设置。
2. 找到应用 `MyApplication`。
3. 进入存储。
4. 点击清除数据。

### 方式 3：Android Studio App Inspection

1. 运行 App。
2. 打开 Android Studio 的 App Inspection。
3. 选择 Database Inspector。
4. 查看或清理 `toutiao_video_client.db`。

当前数据库包含：

- `search_history`
- `feed_cache`
