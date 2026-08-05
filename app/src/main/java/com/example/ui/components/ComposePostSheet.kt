package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposePostSheet(
    onDismiss: () -> Unit,
    onPublish: (caption: String, mediaUrl: String, audience: String, location: String) -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Wimbledon, London") }
    var audience by remember { mutableStateOf("Public") }
    var taggedUsers by remember { mutableStateOf("@sunaraspires") }
    var isGeneratingAiCaptions by remember { mutableStateOf(false) }
    var aiSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    var shareToFacebook by remember { mutableStateOf(true) }
    var shareToTwitter by remember { mutableStateOf(false) }
    var shareToTumblr by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val mediaOptions = listOf(
        "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800",
        "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=800",
        "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=800"
    )
    var selectedMediaUrl by remember { mutableStateOf(mediaOptions.first()) }

    val filterList = listOf("Normal", "Clarendon", "Ludwig", "Moon", "Lark", "Gingham")
    var selectedFilter by remember { mutableStateOf("Normal") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(0.92f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Text(
                    text = "New Post",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = {
                        if (caption.isNotBlank()) {
                            onPublish(caption, selectedMediaUrl, audience, location)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface),
                    enabled = caption.isNotBlank(),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Share", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Caption Field
            OutlinedTextField(
                value = caption,
                onValueChange = { caption = it },
                placeholder = { Text("Write a caption, thoughts, or hashtags...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // AI Caption Assistance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        isGeneratingAiCaptions = true
                        coroutineScope.launch {
                            val options = GeminiService.generateCaption("Islamic Architecture & Creative Design", "Inspiration")
                            aiSuggestions = options
                            isGeneratingAiCaptions = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "AI Caption",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGeneratingAiCaptions) "Generating AI Captions..." else "AI Caption Assistant",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // AI Suggestions List
            AnimatedVisibility(visible = aiSuggestions.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Tap to insert suggestion:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    aiSuggestions.forEach { suggestion ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { caption = suggestion },
                            shape = RoundedCornerShape(12.dp),
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = suggestion,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photo Filter Selector
            Text("Filters", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterList) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tag People & Geotag Location
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add Location (Geotag)") },
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = taggedUsers,
                onValueChange = { taggedUsers = it },
                label = { Text("Tag People (@username)") },
                leadingIcon = { Icon(Icons.Filled.PersonAdd, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Cross-Posting Toggles (Facebook, Twitter, Tumblr - Nura Guide Page 7)
            Text("Cross-Post Simultaneously", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Facebook", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = shareToFacebook, onCheckedChange = { shareToFacebook = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Twitter / X", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = shareToTwitter, onCheckedChange = { shareToTwitter = it })
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tumblr", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Switch(checked = shareToTumblr, onCheckedChange = { shareToTumblr = it })
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
