# ToutiaoVideoClient — 仿今日头条视频播放流 + 搜索模块

> 一个完整的 Android 短视频 Feed 客户端 Demo，支持视频播放流、图文混排、搜索、预加载优化、清晰度切换、横屏播放，以及 Kotlin Multiplatform 跨平台搜索模块。

## 项目简介

ToutiaoVideoClient 是一个仿今日头条视频播放流 + 搜索模块的 Android 客户端。使用本地 Fake 数据模拟服务端接口，完成视频流、图文混排、搜索、搜索结果、视频播放、Room 本地缓存等核心功能，并实现了起播性能优化与指标量化。

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 语言 | Kotlin | 2.2.21 |
| UI | Jetpack Compose + Material3 | BOM 2025.12 |
| 导航 | Navigation Compose | 2.9.8 |
| 播放器 | Media3 ExoPlayer | 1.8.0 |
| 数据库 | Room (KSP) | 2.8.4 |
| 图片加载 | Coil Compose | 2.7.0 |
| 跨平台 | Kotlin Multiplatform (KMP) | 2.2.21 |
| 架构 | MVVM + Repository | — |
| 最低 SDK | API 24 (Android 7.0) | — |
| 目标 SDK | API 36 | — |

## 功能清单

### 视频功能
- ✅ 全屏播放页，黑色沉浸式背景
- ✅ 上下滑切换视频（`VerticalPager`）
- ✅ 数据分页拉取（每页 5 条）
- ✅ 播放 / 暂停切换
- ✅ 进度条展示 + 拖动 Seek
- ✅ 头像、点赞、评论、收藏、分享（展示，不响应点击）
- ✅ 作者名称、标题、描述

### 搜索功能
- ✅ 搜索框（视频流顶部半透明 pill）
- ✅ 推荐词入口（底部横向滚动词条，AI 生成 + 多源融合）
- ✅ 搜索中间页（输入框自动聚焦 + 键盘拉起）
- ✅ 搜索历史（Room 持久化，支持删除/清空）
- ✅ 搜索结果页（视频类结果，显示匹配词）
- ✅ 点击搜索结果跳转到视频流指定视频

### 进阶功能
- ✅ **预加载优化**: 独立 ExoPlayer 实例提前 `prepare()` 下一个视频
- ✅ **起播指标量化**: 冷启动 vs 预加载耗时对比，持久化到 Room
- ✅ **指标面板**: Feed 页实时显示 + 独立 MetricsDashboard 聚合统计
- ✅ **清晰度切换**: 360P / 720P / 1080P 三级切换
- ✅ **横屏播放**: 进入/退出全屏，隐藏系统栏
- ✅ **图文卡混排**: 视频流中穿插图片卡片（6 张图文 + 10 个视频）
- ✅ **网络韧性**: 自动重试（最多 3 次，递增延迟）+ 手动重试按钮
- ✅ **跨平台搜索模块**: KMP shared 模块，SearchRanker + RecommendWordEngine 可复用于 iOS

## 起播性能指标

| 场景 | 冷启动 | 预加载 | 优化幅度 |
|------|--------|--------|---------|
| 本地缓存 | 185ms | 8ms | ↓95.7% |
| CDN 1080P | 450ms | 5ms | ↓98.9% |
| CDN 720P | 320ms | 5ms | ↓98.4% |
| 弱网 3G | 3200ms | 6ms | ↓99.8% |
| **平均** | **879ms** | **5.6ms** | **↓98.2%** |

## 页面结构

```
┌─────────────┐    ┌─────────────┐    ┌─────────────────┐
│  FeedScreen │───→│ SearchScreen│───→│SearchResultScreen│
│  (首页视频流)│    │ (搜索中间页) │    │   (搜索结果页)    │
│             │←───│             │    │         │        │
│  ↑↓ 滑动    │    │ • 搜索历史  │    │ 点击结果  │        │
│  视频+图文  │    │ • 键盘输入  │    │ 跳回Feed  │        │
└──────┬──────┘    └─────────────┘    └─────────┼────────┘
       │                                        │
       │  ┌──────────────────────┐              │
       └─→│ MetricsDashboard     │              │
          │ (起播指标聚合统计)    │              │
          └──────────────────────┘              │
                                                │
┌───────────────────────────────────────────────┘
│  推荐词点击 → 直接跳转 SearchResult
└───────────────────────────────────────────────
```

## 项目结构

```text
MyApplication/
├── app/                              # Android 应用模块
│   └── src/main/java/com/example/myapplication/
│       ├── data/                     # 数据层
│       │   ├── FakeFeedRepository.kt    # 本地假数据（16条）
│       │   ├── FeedRepository.kt        # Room 缓存层
│       │   ├── MetricsRepository.kt     # 起播指标持久化
│       │   ├── RecommendWordEngine.kt   # 推荐词引擎（→KMP delegate）
│       │   ├── SearchHistoryRepository.kt
│       │   └── SearchRanker.kt          # 搜索排名（→KMP delegate）
│       ├── database/                 # Room 数据库
│       │   ├── AppDatabase.kt          # v4，含指标表
│       │   ├── dao/                    # FeedDao, SearchHistoryDao, PlaybackMetricsDao
│       │   └── entity/                # FeedEntity, SearchHistoryEntity, PlaybackMetricsEntity
│       ├── model/                    # 数据模型
│       │   ├── FeedItem.kt, VideoItem.kt, ImageTextItem.kt
│       │   ├── RecommendWord.kt, SearchHistoryItem.kt, VideoQuality.kt
│       ├── navigation/               # 路由导航
│       │   ├── Routes.kt               # 5 条路由（含 Metrics）
│       │   └── AppNavHost.kt
│       ├── player/                   # 视频播放器
│       │   ├── PlayerManager.kt        # ExoPlayer + 预加载 + 重试 + 指标
│       │   ├── VideoPlayerState.kt     # 播放状态
│       │   ├── PlaybackMetrics.kt      # 起播指标数据类
│       │   └── FullscreenController.kt # 横屏/竖屏切换
│       └── ui/                       # UI 层
│           ├── feed/                   # FeedScreen + ViewModel + Cards
│           ├── search/                 # SearchScreen + SearchResultScreen
│           └── metrics/               # MetricsDashboardScreen
├── shared/                           # KMP 跨平台共享模块
│   └── src/
│       ├── commonMain/                 # Android + iOS 共享代码
│       │   └── kotlin/.../shared/
│       │       ├── model/              # VideoItem, FeedItem, RecommendWord 等
│       │       └── search/             # SearchRanker, RecommendWordEngine
│       ├── androidMain/               # Android 平台特定代码
│       └── iosMain/                   # iOS 平台特定代码
├── ASSESSMENT.md                     # 需求评估报告
├── PERFORMANCE_REPORT.md             # 性能优化报告
├── metrics.json                      # 量化指标数据（JSON 格式）
└── README.md
```

## 运行方式

```bash
# 编译 Debug APK
./gradlew.bat assembleDebug

# 运行单元测试
./gradlew.bat testDebugUnitTest

# 编译 KMP shared 模块
./gradlew :shared:build
```

1. 使用 Android Studio 打开项目根目录
2. 等待 Gradle Sync 完成
3. 选择 Android 模拟器或真机（API 24+）
4. 点击 Run

## 需求完成度

| 类别 | 完成 | 总计 | 完成率 |
|------|------|------|--------|
| 视频功能 | 8 | 8 | 100% |
| 搜索功能 | 6 | 6 | 100% |
| 进阶要求 | 5 | 5 | 100% |
| 补充要求 | 4 | 4 | 100% |
| **总计** | **23** | **23** | **100%** |

详见 [ASSESSMENT.md](ASSESSMENT.md) 和 [PERFORMANCE_REPORT.md](PERFORMANCE_REPORT.md)。

## 优化成果总结

1. **起播速度**: 预加载机制使起播耗时从平均 879ms 降至 5.6ms，优化 **98.2%**
2. **代码体积**: 清理 11 个模板残留文件，源文件减少 **19.6%**
3. **跨平台复用**: KMP shared 模块含 ~300 行可复用搜索逻辑，支持 Android + iOS
4. **网络韧性**: 自动重试 3 次 + 手动重试，覆盖弱网场景
5. **指标可观测**: 完整的起播指标采集→持久化→聚合→可视化链路

## AI 辅助说明

本项目由开发者在 AI 辅助下分阶段完成。AI 参与了需求拆分、代码生成、ExoPlayer 和 Room 接入、KMP 共享模块搭建、性能优化、测试验证、文档整理等工作。
