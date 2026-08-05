package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.NoteEntity
import com.example.data.db.PostEntity
import com.example.data.repository.NuraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: NuraRepository) : ViewModel() {

    val posts: StateFlow<List<PostEntity>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stories = repository.allStories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reels = repository.allReels
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups = repository.allGroups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _newNoteText = MutableStateFlow("")
    val newNoteText: StateFlow<String> = _newNoteText.asStateFlow()

    private val _selectedNoteEmoji = MutableStateFlow("✨")
    val selectedNoteEmoji: StateFlow<String> = _selectedNoteEmoji.asStateFlow()

    fun toggleLike(post: PostEntity) {
        viewModelScope.launch {
            repository.togglePostLike(post.id, post.isLiked)
        }
    }

    fun toggleSave(post: PostEntity) {
        viewModelScope.launch {
            repository.togglePostSave(post.id, post.isSaved)
        }
    }

    fun toggleReelLike(id: String, isLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleReelLike(id, isLiked)
        }
    }

    fun toggleGroupJoin(id: String, isJoined: Boolean) {
        viewModelScope.launch {
            repository.toggleGroupJoin(id, isJoined)
        }
    }

    fun setNoteText(text: String) {
        _newNoteText.value = text
    }

    fun setNoteEmoji(emoji: String) {
        _selectedNoteEmoji.value = emoji
    }

    fun publishNote(authorHandle: String, authorName: String) {
        val text = _newNoteText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            val note = NoteEntity(
                id = "note_${System.currentTimeMillis()}",
                authorHandle = authorHandle,
                authorName = authorName,
                content = text,
                emoji = _selectedNoteEmoji.value
            )
            repository.createNote(note)
            _newNoteText.value = ""
        }
    }

    fun createPost(
        authorName: String,
        authorHandle: String,
        caption: String,
        mediaUrl: String,
        audience: String,
        location: String
    ) {
        viewModelScope.launch {
            val newPost = PostEntity(
                id = "post_${System.currentTimeMillis()}",
                authorName = authorName,
                authorHandle = authorHandle,
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                isVerified = true,
                mediaUrl = if (mediaUrl.isBlank()) "https://images.unsplash.com/photo-1541872703-74c5e44368f9?w=800" else mediaUrl,
                caption = caption,
                audience = audience,
                location = location
            )
            repository.createPost(newPost)
        }
    }
}
