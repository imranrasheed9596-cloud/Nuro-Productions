package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.LiveIslamicApiService
import com.example.data.model.Surah
import com.example.data.model.Verse
import com.example.data.repository.IslamicDataProvider
import com.example.data.repository.NuraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuranViewModel(private val repository: NuraRepository) : ViewModel() {

    private val _surahs = MutableStateFlow<List<Surah>>(IslamicDataProvider.getSurahList())
    val surahsList: StateFlow<List<Surah>> = _surahs.asStateFlow()

    private val _selectedSurah = MutableStateFlow<Surah>(_surahs.value.first())
    val selectedSurah: StateFlow<Surah> = _selectedSurah.asStateFlow()

    private val _verses = MutableStateFlow<List<Verse>>(IslamicDataProvider.getSampleVersesForSurah(1))
    val verses: StateFlow<List<Verse>> = _verses.asStateFlow()

    private val _isLoadingVerses = MutableStateFlow(false)
    val isLoadingVerses: StateFlow<Boolean> = _isLoadingVerses.asStateFlow()

    private val _fontSizeSp = MutableStateFlow(22)
    val fontSizeSp: StateFlow<Int> = _fontSizeSp.asStateFlow()

    private val _isPlayingAudio = MutableStateFlow(false)
    val isPlayingAudio: StateFlow<Boolean> = _isPlayingAudio.asStateFlow()

    private val _selectedReciter = MutableStateFlow("Qari Mishary Rashid Alafasy")
    val selectedReciter: StateFlow<String> = _selectedReciter.asStateFlow()

    val bookmarks = repository.quranBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadLiveSurahs()
        selectSurah(_surahs.value.first())
    }

    private fun loadLiveSurahs() {
        viewModelScope.launch {
            val liveList = LiveIslamicApiService.fetchLiveSurahList()
            if (liveList.isNotEmpty()) {
                _surahs.value = liveList
                if (_selectedSurah.value.number == 1) {
                    _selectedSurah.value = liveList.first()
                }
            }
        }
    }

    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
        _isLoadingVerses.value = true
        viewModelScope.launch {
            val liveVerses = LiveIslamicApiService.fetchLiveVersesForSurah(surah.number)
            if (liveVerses.isNotEmpty()) {
                _verses.value = liveVerses
            } else {
                _verses.value = IslamicDataProvider.getSampleVersesForSurah(surah.number)
            }
            _isLoadingVerses.value = false
        }
    }

    fun setFontSize(size: Int) {
        _fontSizeSp.value = size.coerceIn(16, 36)
    }

    fun toggleAudioPlayback() {
        _isPlayingAudio.value = !_isPlayingAudio.value
    }

    fun setReciter(reciter: String) {
        _selectedReciter.value = reciter
    }

    fun addBookmark(verse: Verse, note: String = "") {
        viewModelScope.launch {
            repository.addBookmark(
                surahNumber = verse.surahNumber,
                surahName = _selectedSurah.value.nameEnglish,
                verseNumber = verse.verseNumber,
                note = note
            )
        }
    }
}
