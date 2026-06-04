# AI 使用说明

## AI 参与推荐词生成说明

本项目当前不接入真实 AI API，而是在 FakeFeedRepository 中使用静态推荐词模拟 AI 生成结果。这样可以先完成推荐词模型、排序规则和页面交互，后续再替换为真实 AI 或服务端接口。

### 输入给 AI 的视频内容字段

真实项目中，可以把以下字段作为 AI 输入：

- `id`：内容 id，用于回写推荐词。
- `title`：标题，通常是推荐词最重要来源。
- `description`：内容描述，用于补充语义。
- `authorName`：作者名称，可辅助判断账号风格。
- `tags`：人工或算法标签。
- `itemType`：视频、图片、图文等内容类型。

示例输入：

```json
{
  "id": "video_city_06",
  "itemType": "video",
  "title": "城市天台日落",
  "description": "在高处看晚霞掠过楼群，记录一天结束的光。",
  "authorName": "楼顶日记",
  "tags": ["日落", "生活"]
}
```

### AI 输出推荐词格式

AI 输出应使用结构化格式，便于客户端或服务端解析：

```json
[
  {
    "word": "城市日落",
    "source": "ai_generated",
    "score": 95,
    "reason": "标题和描述都围绕城市晚霞场景"
  },
  {
    "word": "天台风景",
    "source": "ai_generated",
    "score": 88,
    "reason": "描述中出现高处视角，适合联想到天台风景"
  }
]
```

字段说明：

- `word`：推荐词文本。
- `source`：来源，可取 `ai_generated`、`tag_based`、`hot_word`、`manual`。
- `score`：推荐词排序分数。
- `reason`：推荐理由，方便审核和调试。

### 人工审核或规则兜底

AI 结果不能直接无条件展示。真实项目通常会增加：

- 敏感词过滤。
- 重复词去重。
- 低质量词过滤。
- 与标题、标签、描述的相关性校验。
- 热门词兜底。
- 人工运营词兜底。

当前项目中的 RecommendWordEngine 就模拟了这一层规则：

- 当前内容已有推荐词优先。
- tags 可补充推荐词。
- title 可生成关键词。
- 全局热门词用于兜底。

### 本项目 Demo 中使用静态推荐词模拟 AI 生成结果

当前 Demo 在 `FakeFeedRepository` 中按内容 id 维护静态推荐词列表，每条内容至少 4 到 8 个推荐词。`RecommendWordEngine` 会把这些静态词视为 `ai_generated`，再结合 tags、title 和全局热门词生成最终展示列表。

这种方式的好处：

- 不依赖网络。
- 不需要真实 AI key。
- 结果稳定，便于录屏和验收。
- 后续可以平滑替换为真实 AI 推荐词接口。
