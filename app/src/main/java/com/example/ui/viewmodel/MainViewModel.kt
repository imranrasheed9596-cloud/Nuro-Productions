package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.credentials.CustomCredential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.FirebaseAuthService
import com.example.data.db.NuraDatabase
import com.example.data.db.PostEntity
import com.example.data.repository.NuraFirestoreRepository
import com.example.data.repository.NuraRepository
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    HOME, REELS, COMMUNITY, DISCOVER, PROFILE
}

enum class AppDesignTheme(
    val title: String,
    val description: String,
    val isDark: Boolean
) {
    OBSIDIAN_GLASS("Obsidian Liquid Glass", "Modern Apple-style translucent liquid glass dark canvas", true),
    EMERALD_GOLD("Emerald & Gold Grace", "Deep Islamic emerald green with rich warm gold accents", true),
    PEARL_LIGHT("Pearl Porcelain Light", "Clean, high-contrast porcelain white minimalist aesthetic", false),
    ROYAL_SAPPHIRE("Royal Twilight Sapphire", "Midnight sapphire blue with glowing ice-blue highlights", true),
    MIDNIGHT_OLED("Midnight OLED Velvet", "True pitch-black OLED dark mode with indigo glow", true)
}

data class StoryHighlight(
    val id: String,
    val title: String,
    val coverUrl: String
)

data class UserProfileState(
    val displayName: String = "Amina Al-Mansoor",
    val username: String = "@amina.design",
    val bio: String = "Designing digital experiences with beauty, clarity, and intention. 🌿✨ #inspires #creative",
    val country: String = "Turkey",
    val language: String = "English",
    val email: String = "contact@amina.design",
    val phone: String = "+90 555 019 2831",
    val website: String = "https://www.linktr.ee/amina.design",
    val businessAddress: String = "Sisli, Istanbul, Turkey",
    val workingHours: String = "Mon - Fri: 09:00 - 18:00",
    val isPrivate: Boolean = false,
    val isCreator: Boolean = true,
    val isBusiness: Boolean = true,
    val isVerified: Boolean = true,
    val category: String = "Digital Creator & Marketing",
    val followersCount: Int = 1240,
    val followingCount: Int = 380,
    val interests: List<String> = listOf("Technology", "Qur'an Study", "Architecture", "Islamic History"),
    val highlights: List<StoryHighlight> = listOf(
        StoryHighlight("1", "TIPS", "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=200"),
        StoryHighlight("2", "INSPIRES", "https://images.unsplash.com/photo-1584551246679-0daf3d275d0f?w=200"),
        StoryHighlight("3", "PROJECTS", "https://images.unsplash.com/photo-1519817650390-64a93db51149?w=200")
    )
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository: NuraRepository
    private val authService = FirebaseAuthService(application)
    private val firestoreRepo = NuraFirestoreRepository()

    val currentUser: StateFlow<FirebaseUser?> = authService.authStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), authService.currentUser)

    private val _isAuthSheetOpen = MutableStateFlow(false)
    val isAuthSheetOpen: StateFlow<Boolean> = _isAuthSheetOpen.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authStatusMessage = MutableStateFlow<String?>(null)
    val authStatusMessage: StateFlow<String?> = _authStatusMessage.asStateFlow()

    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    private val _userProfile = MutableStateFlow(UserProfileState())
    val userProfile: StateFlow<UserProfileState> = _userProfile.asStateFlow()

    private val _isOnboardingCompleted = MutableStateFlow(true)
    val isOnboardingCompleted: StateFlow<Boolean> = _isOnboardingCompleted.asStateFlow()

    private val _isComposeOpen = MutableStateFlow(false)
    val isComposeOpen: StateFlow<Boolean> = _isComposeOpen.asStateFlow()

    private val _isNotificationsOpen = MutableStateFlow(false)
    val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

    private val _activeChatConversationId = MutableStateFlow<String?>(null)
    val activeChatConversationId: StateFlow<String?> = _activeChatConversationId.asStateFlow()

    private val _appTheme = MutableStateFlow(AppDesignTheme.OBSIDIAN_GLASS)
    val appTheme: StateFlow<AppDesignTheme> = _appTheme.asStateFlow()

    private val _isDesignChooserOpen = MutableStateFlow(false)
    val isDesignChooserOpen: StateFlow<Boolean> = _isDesignChooserOpen.asStateFlow()

    init {
        val database = NuraDatabase.getDatabase(application)
        repository = NuraRepository(database.nuraDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()

            // Observe Firestore posts and save to local repository if available
            firestoreRepo.getFirestorePosts().collect { firestorePosts ->
                for (post in firestorePosts) {
                    repository.createPost(post)
                }
            }
        }
    }

    fun setAuthSheetOpen(isOpen: Boolean) {
        _isAuthSheetOpen.value = isOpen
    }

    suspend fun signInWithGoogle(context: Context) {
        _isAuthLoading.value = true
        _authStatusMessage.value = "Launching Google Sign-In..."
        try {
            val launchResult = authService.launchGoogleSignIn()
            if (launchResult.isSuccess) {
                val credentialResponse = launchResult.getOrThrow()
                val credential = credentialResponse.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val authResult = authService.signInWithGoogleCredential(googleIdTokenCredential.idToken)
                    if (authResult.isSuccess) {
                        _authStatusMessage.value = "Successfully authenticated with Google!"
                        updateProfileFromUser(authResult.getOrThrow())
                    } else {
                        _authStatusMessage.value = "Auth failed: ${authResult.exceptionOrNull()?.message}"
                    }
                } else {
                    _authStatusMessage.value = "Received credential type: ${credential.type}"
                }
            } else {
                _authStatusMessage.value = "Google Sign-In canceled or unavailable on this device."
            }
        } catch (e: Exception) {
            _authStatusMessage.value = "Google error: ${e.message}"
        } finally {
            _isAuthLoading.value = false
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authStatusMessage.value = "Authenticating with Email..."
            val result = authService.signInWithEmail(email, pass)
            if (result.isSuccess) {
                _authStatusMessage.value = "Signed in successfully!"
                updateProfileFromUser(result.getOrThrow())
            } else {
                _authStatusMessage.value = "Error: ${result.exceptionOrNull()?.message}"
            }
            _isAuthLoading.value = false
        }
    }

    fun signUpWithEmail(email: String, pass: String, displayName: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authStatusMessage.value = "Creating account..."
            val result = authService.signUpWithEmail(email, pass, displayName)
            if (result.isSuccess) {
                _authStatusMessage.value = "Account created successfully!"
                updateProfileFromUser(result.getOrThrow())
            } else {
                _authStatusMessage.value = "Error: ${result.exceptionOrNull()?.message}"
            }
            _isAuthLoading.value = false
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authStatusMessage.value = "Signing in as Guest..."
            val result = authService.signInAnonymously()
            if (result.isSuccess) {
                _authStatusMessage.value = "Signed in as Guest."
                updateProfileFromUser(result.getOrThrow())
            } else {
                _authStatusMessage.value = "Error: ${result.exceptionOrNull()?.message}"
            }
            _isAuthLoading.value = false
        }
    }

    fun signOut() {
        authService.signOut()
        _authStatusMessage.value = "Signed out."
    }

    private fun updateProfileFromUser(user: FirebaseUser) {
        val name = user.displayName ?: if (user.isAnonymous) "Guest User" else "Nura Member"
        val email = user.email ?: ""
        _userProfile.value = _userProfile.value.copy(
            displayName = name,
            username = "@${name.lowercase().replace(" ", "")}",
            email = email
        )
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
    }

    fun setComposeOpen(isOpen: Boolean) {
        _isComposeOpen.value = isOpen
    }

    fun setNotificationsOpen(isOpen: Boolean) {
        _isNotificationsOpen.value = isOpen
    }

    fun setDesignChooserOpen(isOpen: Boolean) {
        _isDesignChooserOpen.value = isOpen
    }

    fun setAppTheme(theme: AppDesignTheme) {
        _appTheme.value = theme
    }

    fun openChat(conversationId: String) {
        _activeChatConversationId.value = conversationId
    }

    fun closeChat() {
        _activeChatConversationId.value = null
    }

    fun updateProfile(
        displayName: String,
        username: String,
        bio: String,
        country: String,
        language: String,
        email: String = "contact@amina.design",
        phone: String = "+90 555 019 2831",
        website: String = "https://www.linktr.ee/amina.design",
        businessAddress: String = "Sisli, Istanbul, Turkey",
        isPrivate: Boolean,
        isCreator: Boolean,
        isBusiness: Boolean,
        category: String
    ) {
        _userProfile.value = _userProfile.value.copy(
            displayName = displayName,
            username = if (username.startsWith("@")) username else "@$username",
            bio = bio,
            country = country,
            language = language,
            email = email,
            phone = phone,
            website = website,
            businessAddress = businessAddress,
            isPrivate = isPrivate,
            isCreator = isCreator,
            isBusiness = isBusiness,
            category = category
        )
    }

    fun addHighlight(title: String, coverUrl: String) {
        val newHighlight = StoryHighlight(
            id = System.currentTimeMillis().toString(),
            title = title.uppercase(),
            coverUrl = coverUrl
        )
        _userProfile.value = _userProfile.value.copy(
            highlights = _userProfile.value.highlights + newHighlight
        )
    }

    fun completeOnboarding(displayName: String, username: String, country: String, language: String, interests: List<String>) {
        _userProfile.value = _userProfile.value.copy(
            displayName = displayName,
            username = if (username.startsWith("@")) username else "@$username",
            country = country,
            language = language,
            interests = interests
        )
        _isOnboardingCompleted.value = true
    }

    fun setOnboardingCompleted(completed: Boolean) {
        _isOnboardingCompleted.value = completed
    }
}
