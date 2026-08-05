package com.example.ui.screens.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.GlassCard
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SoftGold
import com.example.ui.viewmodel.AiViewModel
import com.example.ui.viewmodel.QuranViewModel

enum class DiscoverSegment {
    EXPLORE, ISLAMIC, PEOPLE
}

enum class IslamicFeature {
    PRAYER_TIMES, QIBLA, MOSQUE_FINDER, QURAN_READER, AUDIO_QURAN, TAFSIR, HADITH_SEARCH, DAILY_REMINDERS, ZAKAT_CALCULATOR, CHARITY_DIRECTORY, EVENTS, LEARNING_PATHS, AI_ASSISTANT, SCHOLARS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    quranViewModel: QuranViewModel,
    aiViewModel: AiViewModel,
    onOpenFeature: (IslamicFeature) -> Unit
) {
    var selectedSegment by remember { mutableStateOf(DiscoverSegment.ISLAMIC) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search accounts, #hashtags, verses, hadith...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = EmeraldPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Segmented Control
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            SegmentButton(
                title = "Islamic Hub",
                isSelected = selectedSegment == DiscoverSegment.ISLAMIC,
                onClick = { selectedSegment = DiscoverSegment.ISLAMIC },
                modifier = Modifier.weight(1f)
            )
            SegmentButton(
                title = "Explore",
                isSelected = selectedSegment == DiscoverSegment.EXPLORE,
                onClick = { selectedSegment = DiscoverSegment.EXPLORE },
                modifier = Modifier.weight(1f)
            )
            SegmentButton(
                title = "People",
                isSelected = selectedSegment == DiscoverSegment.PEOPLE,
                onClick = { selectedSegment = DiscoverSegment.PEOPLE },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Segment Content
        when (selectedSegment) {
            DiscoverSegment.ISLAMIC -> IslamicHubGrid(onOpenFeature = onOpenFeature)
            DiscoverSegment.EXPLORE -> ExploreGrid()
            DiscoverSegment.PEOPLE -> PeopleList()
        }
    }
}

@Composable
private fun SegmentButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) EmeraldPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun IslamicHubGrid(
    onOpenFeature: (IslamicFeature) -> Unit
) {
    val launcherItems = listOf(
        LauncherItem(IslamicFeature.PRAYER_TIMES, "Prayer Times", "Location-based adhan", Icons.Filled.AccessTime, SoftGold),
        LauncherItem(IslamicFeature.QIBLA, "Qibla Compass", "Compass to Kaaba", Icons.Filled.Explore, EmeraldPrimary),
        LauncherItem(IslamicFeature.QURAN_READER, "Qur'an Reader", "114 Surahs Uthmani", Icons.Filled.MenuBook, EmeraldPrimary),
        LauncherItem(IslamicFeature.AUDIO_QURAN, "Audio Qur'an", "Qari recitations", Icons.Filled.Headphones, SoftGold),
        LauncherItem(IslamicFeature.TAFSIR, "Tafsir", "Multi-source commentary", Icons.Filled.AutoStories, EmeraldPrimary),
        LauncherItem(IslamicFeature.HADITH_SEARCH, "Hadith Search", "Authentic collections", Icons.Filled.FormatQuote, SoftGold),
        LauncherItem(IslamicFeature.ZAKAT_CALCULATOR, "Zakat Calculator", "Guided asset calculation", Icons.Filled.Calculate, EmeraldPrimary),
        LauncherItem(IslamicFeature.MOSQUE_FINDER, "Mosque Finder", "Nearby mosques & Jummah", Icons.Filled.Mosque, SoftGold),
        LauncherItem(IslamicFeature.AI_ASSISTANT, "AI Islamic Assistant", "Sourced citations & ikhtilaf", Icons.Filled.Psychology, SoftGold),
        LauncherItem(IslamicFeature.LEARNING_PATHS, "Learning Paths", "Sequential courses", Icons.Filled.School, EmeraldPrimary),
        LauncherItem(IslamicFeature.CHARITY_DIRECTORY, "Charity Directory", "Vetted organizations", Icons.Filled.VolunteerActivism, SoftGold),
        LauncherItem(IslamicFeature.SCHOLARS, "Verified Scholars", "Credentialed teachers", Icons.Filled.Verified, EmeraldPrimary)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(launcherItems) { item ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenFeature(item.feature) }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(item.color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ExploreGrid() {
    val sampleImages = listOf(
        "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=400",
        "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=400",
        "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=400",
        "https://images.unsplash.com/photo-1564769625905-50e93615e769?w=400",
        "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?w=400",
        "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=400"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        items(sampleImages) { url ->
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = "Explore",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun PeopleList() {
    val people = listOf(
        Pair("Ustadh Yusuf Omar", "@yusuf.scholar"),
        Pair("Dr. Miriam Hassan", "@dr.miriam"),
        Pair("Qari Bilal Hassan", "@bilal.recitation"),
        Pair("Fatima Skincare", "@fatima.halalbeauty"),
        Pair("Sarah Revert", "@sarah.revert")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(people) { (name, handle) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                            contentDescription = name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = handle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Follow", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

private data class LauncherItem(
    val feature: IslamicFeature,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)
