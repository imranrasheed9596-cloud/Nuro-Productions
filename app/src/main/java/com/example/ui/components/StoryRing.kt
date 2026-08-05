package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun StoryRing(
    name: String,
    avatarUrl: String,
    isSeen: Boolean = false,
    isCloseFriends: Boolean = false,
    isAddStory: Boolean = false,
    onClick: () -> Unit
) {
    val activeColor = MaterialTheme.colorScheme.onSurface
    val dimColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    val ringBorderModifier = when {
        isAddStory -> Modifier.border(2.dp, Color.Transparent, CircleShape)
        isCloseFriends -> Modifier.border(2.5.dp, Brush.linearGradient(listOf(activeColor, Color(0xFFCBD5E1))), CircleShape)
        !isSeen -> Modifier.border(2.5.dp, Brush.linearGradient(listOf(activeColor, activeColor.copy(alpha = 0.5f))), CircleShape)
        else -> Modifier.border(1.5.dp, dimColor, CircleShape)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 6.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .then(ringBorderModifier)
                    .padding(4.dp)
                    .clip(CircleShape)
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isAddStory) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Story",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = name,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(68.dp)
        )
    }
}
