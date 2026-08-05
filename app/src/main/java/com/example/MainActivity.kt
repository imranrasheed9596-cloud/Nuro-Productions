package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.*
import com.example.ui.screens.chat.DirectMessageScreen
import com.example.ui.screens.community.CommunityScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.discover.IslamicFeature
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.islamic.*
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.reels.ReelsScreen
import com.example.ui.theme.NuraTheme
import com.example.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val appTheme by mainViewModel.appTheme.collectAsState()
            NuraTheme(appTheme = appTheme) {
                NuraApp(mainViewModel = mainViewModel)
            }
        }
    }
}

enum class SubScreen {
    NONE, DIRECT_MESSAGES, PRAYER_TIMES, QURAN_READER, ZAKAT_CALCULATOR, AI_ASSISTANT, MOSQUES_SCHOLARS
}

@Composable
fun NuraApp(mainViewModel: MainViewModel = viewModel()) {
    val repository = mainViewModel.repository

    val homeViewModel = remember { HomeViewModel(repository) }
    val quranViewModel = remember { QuranViewModel(repository) }
    val aiViewModel = remember { AiViewModel() }
    val chatViewModel = remember { ChatViewModel(repository) }

    val selectedTab by mainViewModel.selectedTab.collectAsState()
    val isComposeOpen by mainViewModel.isComposeOpen.collectAsState()
    val isNotificationsOpen by mainViewModel.isNotificationsOpen.collectAsState()
    val isDesignChooserOpen by mainViewModel.isDesignChooserOpen.collectAsState()

    var activeSubScreen by remember { mutableStateOf(SubScreen.NONE) }
    var isEditProfileOpen by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (activeSubScreen == SubScreen.NONE) {
            Scaffold(
                bottomBar = {
                    FloatingBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { mainViewModel.selectTab(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (selectedTab) {
                        MainTab.HOME -> HomeScreen(
                            mainViewModel = mainViewModel,
                            homeViewModel = homeViewModel,
                            onOpenMessages = { activeSubScreen = SubScreen.DIRECT_MESSAGES },
                            onOpenPrayerTimes = { activeSubScreen = SubScreen.PRAYER_TIMES }
                        )
                        MainTab.REELS -> ReelsScreen(homeViewModel = homeViewModel)
                        MainTab.COMMUNITY -> CommunityScreen(homeViewModel = homeViewModel)
                        MainTab.DISCOVER -> DiscoverScreen(
                            quranViewModel = quranViewModel,
                            aiViewModel = aiViewModel,
                            onOpenFeature = { feature ->
                                when (feature) {
                                    IslamicFeature.PRAYER_TIMES, IslamicFeature.QIBLA -> activeSubScreen = SubScreen.PRAYER_TIMES
                                    IslamicFeature.QURAN_READER, IslamicFeature.AUDIO_QURAN, IslamicFeature.TAFSIR -> activeSubScreen = SubScreen.QURAN_READER
                                    IslamicFeature.ZAKAT_CALCULATOR, IslamicFeature.CHARITY_DIRECTORY -> activeSubScreen = SubScreen.ZAKAT_CALCULATOR
                                    IslamicFeature.AI_ASSISTANT -> activeSubScreen = SubScreen.AI_ASSISTANT
                                    IslamicFeature.MOSQUE_FINDER, IslamicFeature.SCHOLARS -> activeSubScreen = SubScreen.MOSQUES_SCHOLARS
                                    else -> activeSubScreen = SubScreen.AI_ASSISTANT
                                }
                            }
                        )
                        MainTab.PROFILE -> ProfileScreen(
                            mainViewModel = mainViewModel,
                            onOpenEditProfile = { isEditProfileOpen = true }
                        )
                    }
                }
            }
        } else {
            // Subscreen navigation
            Box(modifier = Modifier.fillMaxSize()) {
                when (activeSubScreen) {
                    SubScreen.DIRECT_MESSAGES -> DirectMessageScreen(
                        chatViewModel = chatViewModel,
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.PRAYER_TIMES -> PrayerTimesScreen(
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.QURAN_READER -> QuranReaderScreen(
                        quranViewModel = quranViewModel,
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.ZAKAT_CALCULATOR -> ZakatCalculatorScreen(
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.AI_ASSISTANT -> AiAssistantScreen(
                        aiViewModel = aiViewModel,
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.MOSQUES_SCHOLARS -> MosqueAndScholarsScreen(
                        onBack = { activeSubScreen = SubScreen.NONE }
                    )
                    SubScreen.NONE -> {}
                }
            }
        }

        // Global Modals
        if (isComposeOpen) {
            ComposePostSheet(
                onDismiss = { mainViewModel.setComposeOpen(false) },
                onPublish = { caption, mediaUrl, audience, location ->
                    homeViewModel.createPost(
                        authorName = mainViewModel.userProfile.value.displayName,
                        authorHandle = mainViewModel.userProfile.value.username,
                        caption = caption,
                        mediaUrl = mediaUrl,
                        audience = audience,
                        location = location
                    )
                }
            )
        }

        if (isNotificationsOpen) {
            NotificationDrawer(
                onDismiss = { mainViewModel.setNotificationsOpen(false) }
            )
        }

        if (isEditProfileOpen) {
            OnboardingAndSettingsSheet(
                mainViewModel = mainViewModel,
                onDismiss = { isEditProfileOpen = false }
            )
        }

        if (isDesignChooserOpen) {
            DesignChooserSheet(
                mainViewModel = mainViewModel,
                onDismiss = { mainViewModel.setDesignChooserOpen(false) }
            )
        }
    }
}
