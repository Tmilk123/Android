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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
        ToutiaoAvatar(authorName = authorName, authorAvatar = authorAvatar)
        ToutiaoActionButton(icon = Icons.Filled.FavoriteBorder, count = likeCount)
        ToutiaoActionButton(icon = Icons.Filled.ChatBubbleOutline, count = commentCount)
        ToutiaoActionButton(icon = Icons.Filled.BookmarkBorder, count = collectCount)
        ToutiaoActionButton(icon = Icons.Filled.Share, count = shareCount)
    }
}

@Composable
private fun ToutiaoAvatar(authorName: String, authorAvatar: String) {
    var tapped by remember { mutableStateOf(false) }
    val avatarScale by animateFloatAsState(
        targetValue = if (tapped) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "avatar_scale",
    )
    Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.fillMaxSize().scale(avatarScale).clip(CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    tapped = true; tapped = false
                },
            contentAlignment = Alignment.Center,
        ) {
            if (authorAvatar.isNotBlank()) {
                AsyncImage(model = authorAvatar, contentDescription = authorName,
                    modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text(authorName.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }
        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter).offset(y = 4.dp).size(20.dp)
                .clip(CircleShape).background(Color(0xFFD81E06)),
            contentAlignment = Alignment.Center,
        ) {
            Text("+", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ToutiaoActionButton(icon: ImageVector, count: String) {
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(46.dp).scale(scale).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.28f))
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    tapped = true; tapped = false
                },
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(28.dp))
        }
        Text(count, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp,
            textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
