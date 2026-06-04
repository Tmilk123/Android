package com.example.myapplication.ui.feed

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
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
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Avatar with follow badge
        AvatarBadge(authorName = authorName, authorAvatar = authorAvatar)

        // Action buttons
        ActionIconButton(icon = Icons.Filled.ThumbUp, label = "赞", count = likeCount)
        ActionIconButton(icon = Icons.Filled.ChatBubbleOutline, label = "评", count = commentCount)
        ActionIconButton(icon = Icons.Filled.BookmarkBorder, label = "藏", count = collectCount)
        ActionIconButton(icon = Icons.Filled.Share, label = "享", count = shareCount)
    }
}

@Composable
private fun AvatarBadge(
    authorName: String,
    authorAvatar: String,
) {
    var tapped by remember { mutableStateOf(false) }
    val avatarScale by animateFloatAsState(
        targetValue = if (tapped) 0.85f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "avatar_scale",
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(avatarScale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { tapped = true; tapped = false }
            ),
        contentAlignment = Alignment.Center,
    ) {
        // Avatar image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
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
                Text(
                    text = authorName.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }
        }
        // Follow "+" badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 4.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF2D55)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    icon: ImageVector,
    label: String,
    count: String,
) {
    var tapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (tapped) 1.25f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 500f),
        label = "action_scale",
    )
    val iconColor by animateColorAsState(
        targetValue = if (tapped) Color(0xFFFF2D55) else Color.White,
        label = "action_color",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        tapped = true
                        tapped = false
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(26.dp),
            )
        }
        Text(
            text = count,
            color = Color.White,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
