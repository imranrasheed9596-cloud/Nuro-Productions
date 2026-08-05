package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.DirectMessageEntity
import com.example.data.repository.NuraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: NuraRepository) : ViewModel() {

    val allMessages: StateFlow<List<DirectMessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _messageInput = MutableStateFlow("")
    val messageInput: StateFlow<String> = _messageInput.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _activeCallState = MutableStateFlow<CallState?>(null)
    val activeCallState: StateFlow<CallState?> = _activeCallState.asStateFlow()

    data class CallState(
        val name: String,
        val handle: String,
        val isVideo: Boolean,
        val isMuted: Boolean = false,
        val isCameraOff: Boolean = false
    )

    fun setMessageInput(text: String) {
        _messageInput.value = text
    }

    fun sendMessage(conversationId: String, senderHandle: String, senderName: String) {
        val text = _messageInput.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            val msg = DirectMessageEntity(
                id = "msg_${System.currentTimeMillis()}",
                conversationId = conversationId,
                senderHandle = senderHandle,
                senderName = senderName,
                text = text
            )
            repository.sendMessage(msg)
            _messageInput.value = ""
        }
    }

    fun sendVoiceNote(conversationId: String, senderHandle: String, senderName: String, durationSec: Int) {
        viewModelScope.launch {
            val msg = DirectMessageEntity(
                id = "msg_voice_${System.currentTimeMillis()}",
                conversationId = conversationId,
                senderHandle = senderHandle,
                senderName = senderName,
                text = "🎤 Voice message (${durationSec}s)",
                isVoice = true,
                voiceDurationSec = durationSec
            )
            repository.sendMessage(msg)
        }
    }

    fun startCall(name: String, handle: String, isVideo: Boolean) {
        _activeCallState.value = CallState(name = name, handle = handle, isVideo = isVideo)
    }

    fun endCall() {
        _activeCallState.value = null
    }

    fun toggleMuteCall() {
        _activeCallState.value = _activeCallState.value?.let {
            it.copy(isMuted = !it.isMuted)
        }
    }

    fun toggleCameraCall() {
        _activeCallState.value = _activeCallState.value?.let {
            it.copy(isCameraOff = !it.isCameraOff)
        }
    }
}
