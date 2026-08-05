package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.NoteEntity
import com.example.data.db.PostEntity
import com.example.ui.components.*
import com.example.ui.viewmodel.HomeViewModel
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    onOpenMessages: () -> Unit,
    onOpenPrayerTimes: () -> Unit
) {
    val posts by homeViewModel.posts.collectAsState()
    val stories by homeViewModel.stories.collectAsState()
    val notes by homeViewModel.notes.collectAsState()

    var activeSharePost by remember { mutableStateOf<PostEntity?>(null) }
    var isCommentsOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = com.example.R.drawable.nura_official_logo_as_uploaded_1785893585667,
                            contentDescription = "Nura Official Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Nura",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Design Choose Options
                    IconButton(onClick = { mainViewModel.setDesignChooserOpen(true) }) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Choose App Design",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Notifications
                    IconButton(onClick = { mainViewModel.setNotificationsOpen(true) }) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    // Direct Messages
                    IconButton(onClick = onOpenMessages) {
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Direct Messages",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurface)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Story Tray
                item {
                    LazyRow(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        item {
                            StoryRing(
                                name = "Your Story",
                                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                                isAddStory = true,
                                onClick = { mainViewModel.setComposeOpen(true) }
                            )
                        }
                        items(stories) { story ->
                            StoryRing(
                                name = story.authorName.split(" ").first(),
                                avatarUrl = story.avatarUrl,
                                isSeen = story.isSeen,
                                isCloseFriends = story.isCloseFriends,
                                onClick = {}
                            )
                        }
                    }
                }

                // Notes Bubble Row
                item {
                    if (notes.isNotEmpty()) {
                        NotesRow(notes = notes)
                    }
                }

                // Islamic Quick Access Strip
                item {
                    IslamicQuickAccessStrip(
                        nextPrayerName = "Asr",
                        nextPrayerTime = "04:20 PM",
                        countdownMinutes = 42,
                        onClick = onOpenPrayerTimes
                    )
                }

                // Feed Posts
                items(posts) { post ->
                    FeedPostCard(
                        post = post,
                        onLikeToggle = { homeViewModel.toggleLike(post) },
                        onSaveToggle = { homeViewModel.toggleSave(post) },
                        onCommentClick = { isCommentsOpen = true },
                        onShareClick = { activeSharePost = post }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Share Sheet Dialog
        activeSharePost?.let { post ->
            ShareSheet(
                postCaption = post.caption,
                onDismiss = { activeSharePost = null },
                onActionSelected = {}
            )
        }

        // Comment Sheet
        if (isCommentsOpen) {
            CommentSheet(onDismiss = { isCommentsOpen = false })
        }
    }
}

@Composable
private fun NotesRow(notes: List<NoteEntity>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notes) { note ->
            GlassCard(
                modifier = Modifier,
                shape = RoundedCornerShape(18.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = note.emoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = note.authorName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = note.content,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedPostCard(
    post: PostEntity,
    onLikeToggle: () -> Unit,
    onSaveToggle: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit
) {
    var showHeartBurst by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Author Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = post.authorAvatarUrl,
                        contentDescription = post.authorName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (post.isVerified || post.isScholar) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Verified",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        if (post.location.isNotBlank()) {
                            Text(
                                text = post.location,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Options", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Post Media Image with double-tap like gesture
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                showHeartBurst = true
                                if (!post.isLiked) onLikeToggle()
                            }
                        )
                    }
            ) {
                AsyncImage(
                    model = post.mediaUrl,
                    contentDescription = "Post Media",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (showHeartBurst) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier
                            .size(90.dp)
                            .scale(1.2f)
                    )
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(600)
                        showHeartBurst = false
                    }
                }
            }

            // Actions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLikeToggle) {
                        Icon(
                            imageVector = if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (post.isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onCommentClick) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comment",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(onClick = onSaveToggle) {
                    Icon(
                        imageVector = if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Likes count & Caption
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                Text(
                    text = "${post.likesCount} likes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row {
                    Text(
                        text = post.authorHandle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = post.caption,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isExpanded) 10 else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { isExpanded = !isExpanded }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "View all ${post.commentsCount} comments",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onCommentClick() }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
