package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AiMessage(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class AiViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<AiMessage>>(
        listOf(
            AiMessage(
                id = "init",
                text = "Assalamu Alaikum! I am Nura's AI Islamic Assistant. Ask me any question on Islamic practices, Qur'an verses, or Hadith. All answers carry transparent citations and respect scholarly differences (ikhtilaf).",
                isUser = false
            )
        )
    )
    val messages: StateFlow<List<AiMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    fun setUserInput(text: String) {
        _userInput.value = text
    }

    fun sendMessage() {
        val text = _userInput.value.trim()
        if (text.isBlank() || _isLoading.value) return

        val userMsg = AiMessage(id = "user_${System.currentTimeMillis()}", text = text, isUser = true)
        _messages.value = _messages.value + userMsg
        _userInput.value = ""
        _isLoading.value = true

        viewModelScope.launch {
            val answer = GeminiService.askIslamicAssistant(text)
            val aiMsg = AiMessage(id = "ai_${System.currentTimeMillis()}", text = answer, isUser = false)
            _messages.value = _messages.value + aiMsg
            _isLoading.value = false
        }
    }

    fun clearHistory() {
        _messages.value = listOf(
            AiMessage(
                id = "init",
                text = "Assalamu Alaikum! I am Nura's AI Islamic Assistant. Ask me any question on Islamic practices, Qur'an verses, or Hadith.",
                isUser = false
            )
        )
    }
}
