package com.example.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectMessageScreen(
    chatViewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messageInput by chatViewModel.messageInput.collectAsState()
    var isVoiceRecording by remember { mutableStateOf(false) }
    var showDisappearingMediaMenu by remember { mutableStateOf(false) }
    var selectedMediaMode by remember { mutableStateOf("View Once") } // "View Once", "Allow Replay", "Keep in Chat"

    val activeCall by chatViewModel.activeCallState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                        contentDescription = "Ustadh Yusuf Omar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Ustadh Yusuf Omar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Online • Verified Scholar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
            },
            actions = {
                IconButton(onClick = { chatViewModel.startCall("Ustadh Yusuf Omar", "@yusuf.scholar", isVideo = false) }) {
                    Icon(imageVector = Icons.Filled.Call, contentDescription = "Voice Call", tint = MaterialTheme.colorScheme.onSurface)
                }
                IconButton(onClick = { chatViewModel.startCall("Ustadh Yusuf Omar", "@yusuf.scholar", isVideo = true) }) {
                    Icon(imageVector = Icons.Filled.Videocam, contentDescription = "Video Call", tint = MaterialTheme.colorScheme.onSurface)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
        )

        // Message List Simulation
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                MessageBubble(
                    text = "Assalamu Alaikum! Did you review the Tafsir notes for Surah Al-Kahf?",
                    isMe = false,
                    time = "10:30 AM"
                )
            }
            item {
                MessageBubble(
                    text = "Wa Alaikum Assalam Ustadh! Yes, the citations were very insightful.",
                    isMe = true,
                    time = "10:32 AM"
                )
            }
            item {
                MessageBubble(
                    text = "📷 Photo attached ($selectedMediaMode)",
                    isMe = true,
                    time = "10:34 AM",
                    isMedia = true
                )
            }
            item {
                MessageBubble(
                    text = "🎤 Voice message (14s)",
                    isMe = false,
                    time = "10:35 AM",
                    isVoice = true
                )
            }
        }

        // Voice Recording Status Banner
        if (isVoiceRecording) {
            GlassCard(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.GraphicEq, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Recording Voice Note... (0:05)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    TextButton(onClick = {
                        isVoiceRecording = false
                        chatViewModel.sendVoiceNote("conv_yusuf", "@current_user", "You", 5)
                    }) {
                        Text("Send", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Disappearing Media Selector Menu (Nura Guide Page 4)
        AnimatedVisibility(visible = showDisappearingMediaMenu) {
            GlassCard(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Disappearing Media Options:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("View Once", "Allow Replay", "Keep in Chat").forEach { mode ->
                            FilterChip(
                                selected = selectedMediaMode == mode,
                                onClick = {
                                    selectedMediaMode = mode
                                    showDisappearingMediaMenu = false
                                },
                                label = { Text(mode, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Composer Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showDisappearingMediaMenu = !showDisappearingMediaMenu },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = "Send Disappearing Media",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = messageInput,
                onValueChange = { chatViewModel.setMessageInput(it) },
                placeholder = { Text("Message...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { isVoiceRecording = !isVoiceRecording },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector = Icons.Outlined.Mic,
                    contentDescription = "Voice Note",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            IconButton(
                onClick = {
                    if (messageInput.isNotBlank()) {
                        chatViewModel.sendMessage("conv_yusuf", "@current_user", "You")
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // In-Call Dialog Sheet
        activeCall?.let { call ->
            CallOverlaySheet(
                callState = call,
                onMuteToggle = { chatViewModel.toggleMuteCall() },
                onCameraToggle = { chatViewModel.toggleCameraCall() },
                onEndCall = { chatViewModel.endCall() }
            )
        }
    }
}

@Composable
private fun MessageBubble(
    text: String,
    isMe: Boolean,
    time: String,
    isVoice: Boolean = false,
    isMedia: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        GlassCard(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isMe) 18.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 18.dp
            ),
            backgroundColor = if (isMe) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (isVoice) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null, tint = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = text, fontSize = 13.sp, color = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface)
                    }
                } else if (isMedia) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = text, fontSize = 13.sp, color = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = text,
                        fontSize = 13.sp,
                        color = if (isMe) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = if (isMe) MaterialTheme.colorScheme.surface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CallOverlaySheet(
    callState: ChatViewModel.CallState,
    onMuteToggle: () -> Unit,
    onCameraToggle: () -> Unit,
    onEndCall: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onEndCall,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                contentDescription = callState.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = callState.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = if (callState.isVideo) "Video Call • 00:42" else "Voice Call • 00:42", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (callState.isMuted) Color.Red else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                        contentDescription = "Mute",
                        tint = if (callState.isMuted) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                if (callState.isVideo) {
                    IconButton(
                        onClick = onCameraToggle,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (callState.isCameraOff) Color.Red else MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = if (callState.isCameraOff) Icons.Filled.VideocamOff else Icons.Filled.Videocam,
                            contentDescription = "Camera",
                            tint = if (callState.isCameraOff) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
