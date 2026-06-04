package com.example.myapplication.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.example.myapplication.model.ImageTextItem

@Preview(showBackground = true)
@Composable
fun ImageTextFeedCardPreview() {
    ImageTextFeedCard(
        item = ImageTextItem(
            id = "1",
            title = "示例图文内容",
            description = "这是一段图文描述",
            authorName = "测试作者",
            authorAvatar = "",
            imageUrl = "https://picsum.photos/800/1200",
            likeCount = "1000",
            commentCount = "200",
            collectCount = "300",
            shareCount = "400",
            tags = emptyList(),
            recommendWords = emptyList(),
        )
    )
}

@Composable
fun ImageTextFeedCard(
    item: ImageTextItem,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.background(Color.Black)) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.24f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.78f),
                        )
                    )
                )
        )
    }
}
