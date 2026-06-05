package com.example.myapplication.ui.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

/**
 * 今日头条视频流右侧互动按钮组
 * 布局: 头像(带+号) → 点赞 → 评论 → 收藏 → 分享
 */
@Composable
fun InteractionButtons(
    authorName: String,
    authorAvatar: String,
    likeCount: String,
    commentCount: String,
    collectCount: String,
    shareCount: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        // 头像 + 关注+
        ToutiaoAvatar(authorName = authorName, authorAvatar = authorAvatar)

        // 操作按钮 (不含文字标签，像今日头条那样只显示图标)
        ToutiaoActionButton(icon = Icons.Filled.FavoriteBorder, count = likeCount)
        ToutiaoActionButton(icon = Icons.Filled.ChatBubbleOutline, count = commentCount)
        ToutiaoActionButton(icon = Icons.Filled.BookmarkBorder, count = collectCount)
        ToutiaoActionButton(icon = Icons.Filled.Share, count = shareCount)

        // 音乐/原声 旋转头像 (今日头条特色)
        Spacer(modifier = Modifier.height(4.dp))
        MusicDiscBadge(authorAvatar = authorAvatar)
    }
}

/**
 * 头像 + 红色关注 + 号
 */
@Composable
private fun ToutiaoAvatar(
    authorName: String,
    authorAvatar: String,
) {
    var tapped by remember { mutableStateOf(false) }
    val avatarScale by animateFloatAsState(
        targetValue = if (tapped) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "avatar_scale",
    )

    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(avatarScale)
                .clip(CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { tapped = true; tapped = false },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (authorAvatar.isNotBlank()) {
                AsyncImage(
                    model = authorAvatar,
                    contentDescription = authorName,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = authorName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                    )
                }
            }
        }
        // 红色 + 关注按钮
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color(0xFFD81E06)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/**
 * 操作按钮: 图标 + 数字
 */
@Composable
private fun ToutiaoActionButton(
    icon: ImageVector,
    count: String,
) {
    var tapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (tapped) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = 600f),
        label = "btn_scale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (tapped) Color(0xFFD81E06) else Color.White,
        label = "icon_color",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { tapped = true; tapped = false },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp),
            )
        }
        Text(
            text = count,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 今日头条特色: 音乐碟片旋转效果
 */
@Composable
private fun MusicDiscBadge(authorAvatar: String) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = authorAvatar,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )
        // 旋转音符图标 (简化为静态圆环)
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
        )
    }
}
