package com.example.myapplication.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.model.RecommendWord

@Composable
fun FeedOverlay(
    authorName: String,
    authorAvatar: String,
    title: String,
    description: String,
    likeCount: String,
    commentCount: String,
    collectCount: String,
    shareCount: String,
    recommendWords: List<RecommendWord>,
    onSearchClick: () -> Unit,
    onRecommendWordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // ── Top: 搜索栏 ──
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(Color.White.copy(alpha = 0.18f))
                .clickable { onSearchClick() }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "搜索",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp),
            )
            Text(
                text = "  搜索感兴趣的视频",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
            )
        }

        // ── 作者/标题/描述 (进度条上方) ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 80.dp, bottom = 68.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "@$authorName", color = Color.White,
                    fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "关注", color = Color.White,
                    fontSize = 12.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFD81E06)).clickable { }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
            Text(
                text = title, color = Color.White,
                fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
            )
            if (description.isNotBlank()) {
                Text(
                    text = description, color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp, lineHeight = 18.sp,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // ── 相关搜索 (进度条正下方) ──
        if (recommendWords.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 80.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "🔍 相关搜索：",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp,
                )
                recommendWords.take(4).forEach { word ->
                    Text(
                        text = word.word, color = Color(0xFF8CB3D9),
                        fontSize = 13.sp, maxLines = 1,
                        modifier = Modifier.padding(start = 6.dp)
                            .clickable { onRecommendWordClick(word.word) },
                    )
                }
            }
        }

        // ── Bottom Right: 头像 + 互动按钮 (进度条上方) ──
        InteractionButtons(
            authorName = authorName, authorAvatar = authorAvatar,
            likeCount = likeCount, commentCount = commentCount,
            collectCount = collectCount, shareCount = shareCount,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 8.dp, bottom = 68.dp),
        )
    }
}
