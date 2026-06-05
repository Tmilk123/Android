package com.example.myapplication.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val images = item.allImageUrls
    val pagerState = rememberPagerState(pageCount = { images.size.coerceAtLeast(1) })

    Box(modifier = modifier.background(Color.Black)) {
        if (images.size > 1) {
            // ── Multiple images: horizontal swipe ──
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "${item.title} (${page + 1}/${images.size})",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,  // 不裁剪，黑边
                )
            }

            // Page indicator dots
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                images.indices.forEach { index ->
                    val isActive = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .size(if (isActive) 7.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) Color.White
                                else Color.White.copy(alpha = 0.45f)
                            ),
                    )
                }
            }

            // Image count badge
            Text(
                text = "${pagerState.currentPage + 1}/${images.size}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 12.dp, start = 12.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            )
        } else {
            // ── Single image ──
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,  // 不裁剪，黑边
            )
        }

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.30f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.82f),
                        )
                    )
                )
        )

        // "图文" badge
        Text(
            text = if (images.size > 1) "图文 · ${images.size}图" else "图文",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = if (images.size > 1) 48.dp else 16.dp, start = 16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 8.dp, vertical = 3.dp),
        )
    }
}
