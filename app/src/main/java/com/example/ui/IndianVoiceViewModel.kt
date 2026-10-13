package com.example.ui

import android.app.Application
import android.content.Intent
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.IndianSoundEffect
import com.example.audio.IndianSoundSynthesizer
import com.example.data.SavedClipsRepository
import com.example.model.IndianLanguage
import com.example.model.IndianPhrase
import com.example.model.IndianVoiceItem
import com.example.model.IndianVoicePersona
import com.example.model.PhraseCategory
import com.example.model.SavedClip
import com.example.model.VoiceGenderFilter
import com.example.tts.IndianTtsManager
import com.example.tts.TtsLanguageStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class AppNavTab(val title: String, val icon: String) {
    STUDIO("Voice Studio", "🎙️"),
    PHRASEBOOK("Desi Phrases", "📖"),
    SOUNDBOARD("Indian Sounds", "🪕"),
    SAVED_CLIPS("Saved Clips", "💾")
}

data class UiState(
    val currentTab: AppNavTab = AppNavTab.STUDIO,
    val selectedLanguage: IndianLanguage = IndianLanguage.MARATHI,
    val textInput: String = IndianLanguage.MARATHI.defaultText,
    val selectedVoice: IndianVoiceItem? = null,
    val selectedPersona: IndianVoicePersona = IndianVoicePersona.NATURAL_DEFAULT,
    val selectedGenderFilter: VoiceGenderFilter = VoiceGenderFilter.ALL,
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.0f,
    val isTanpuraDroneEnabled: Boolean = false,
    val isSynthesizingFile: Boolean = false,
    val saveDialogVisible: Boolean = false,
    val saveClipTitle: String = "",
    val diagnosticDialogVisible: Boolean = false,
    val snackbarMessage: String? = null,

    // Phrasebook
    val phrasebookLanguage: IndianLanguage = IndianLanguage.MARATHI,
    val selectedPhraseCategory: PhraseCategory? = null,
    val phraseSearchQuery: String = "",

    // Saved audio player
    val playingClipId: String? = null,
    val playbackProgress: Float = 0f
)

class IndianVoiceViewModel(application: Application) : AndroidViewModel(application) {

    val ttsManager = IndianTtsManager(application, viewModelScope)
    val soundSynthesizer = IndianSoundSynthesizer(viewModelScope)
    val clipsRepo = SavedClipsRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var playbackProgressJob: Job? = null

    init {
        // Collect TTS voices when initialized
        viewModelScope.launch {
            ttsManager.state.collect { ttsState ->
                if (ttsState.isInitialized && _uiState.value.selectedVoice == null) {
                    val voices = ttsState.availableVoices[_uiState.value.selectedLanguage]
                    if (!voices.isNullOrEmpty()) {
                        _uiState.value = _uiState.value.copy(selectedVoice = voices.firstOrNull())
                    }
                }
            }
        }
    }

    fun selectTab(tab: AppNavTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectLanguage(lang: IndianLanguage) {
        val currentText = _uiState.value.textInput
        val isCurrentDefault = IndianLanguage.values().any { it.defaultText == currentText }
        val newText = if (isCurrentDefault || currentText.isBlank()) lang.defaultText else currentText

        val voices = ttsManager.state.value.availableVoices[lang]
        val voice = voices?.firstOrNull()

        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            textInput = newText,
            selectedVoice = voice
        )
    }

    fun updateTextInput(newText: String) {
        _uiState.value = _uiState.value.copy(textInput = newText)
    }

    fun selectVoice(voice: IndianVoiceItem) {
        _uiState.value = _uiState.value.copy(selectedVoice = voice)
    }

    fun selectGenderFilter(filter: VoiceGenderFilter) {
        _uiState.value = _uiState.value.copy(selectedGenderFilter = filter)

        // If current voice doesn't match filter, pick first matching
        val currentLang = _uiState.value.selectedLanguage
        val voices = ttsManager.state.value.availableVoices[currentLang] ?: emptyList()
        val matching = when (filter) {
            VoiceGenderFilter.ALL -> voices
            VoiceGenderFilter.FEMALE -> voices.filter { it.genderHint == "Female" }
            VoiceGenderFilter.MALE -> voices.filter { it.genderHint == "Male" }
        }
        if (matching.isNotEmpty() && matching.none { it.id == _uiState.value.selectedVoice?.id }) {
            _uiState.value = _uiState.value.copy(selectedVoice = matching.first())
        }
    }

    fun selectPersona(persona: IndianVoicePersona) {
        val currentLang = _uiState.value.selectedLanguage
        val voices = ttsManager.state.value.availableVoices[currentLang] ?: emptyList()

        // Match preferred gender if persona specifies one
        var targetVoice = _uiState.value.selectedVoice
        if (persona.preferredGender != "Natural") {
            val matchingGenderVoice = voices.find { it.genderHint.equals(persona.preferredGender, ignoreCase = true) }
            if (matchingGenderVoice != null) {
                targetVoice = matchingGenderVoice
            }
        }

        _uiState.value = _uiState.value.copy(
            selectedPersona = persona,
            speechRate = persona.defaultSpeechRate,
            pitch = persona.defaultPitch,
            selectedVoice = targetVoice
        )
    }

    fun setSpeechRate(rate: Float) {
        _uiState.value = _uiState.value.copy(
            speechRate = rate.coerceIn(0.5f, 2.0f),
            selectedPersona = IndianVoicePersona.CUSTOM
        )
    }

    fun setPitch(pitch: Float) {
        _uiState.value = _uiState.value.copy(
            pitch = pitch.coerceIn(0.5f, 2.0f),
            selectedPersona = IndianVoicePersona.CUSTOM
        )
    }

    fun toggleTanpuraDrone() {
        val next = !_uiState.value.isTanpuraDroneEnabled
        _uiState.value = _uiState.value.copy(isTanpuraDroneEnabled = next)
        soundSynthesizer.setTanpuraAmbiance(next)
    }

    fun speakCurrentText() {
        val state = _uiState.value
        if (state.textInput.isBlank()) {
            showSnackbar("Please enter some text to speak")
            return
        }

        ttsManager.speak(
            text = state.textInput,
            language = state.selectedLanguage,
            voiceItem = state.selectedVoice,
            speechRate = state.speechRate,
            pitch = state.pitch
        )
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun openSaveDialog() {
        if (_uiState.value.textInput.isBlank()) {
            showSnackbar("Enter text first before saving")
            return
        }
        val defaultTitle = when (_uiState.value.selectedLanguage) {
            IndianLanguage.MARATHI -> "मराठी आवाज"
            IndianLanguage.HINDI -> "हिंदी आवाज"
            IndianLanguage.INDIAN_ENGLISH -> "Indian Voice"
            IndianLanguage.TAMIL -> "தமிழ் குரல்"
            IndianLanguage.TELUGU -> "తెలుగు వాయిస్"
            IndianLanguage.BENGALI -> "বাংলা কণ্ঠস্বর"
        }
        _uiState.value = _uiState.value.copy(
            saveDialogVisible = true,
            saveClipTitle = "$defaultTitle (${_uiState.value.selectedPersona.displayName})"
        )
    }

    fun dismissSaveDialog() {
        _uiState.value = _uiState.value.copy(saveDialogVisible = false)
    }

    fun updateSaveTitle(title: String) {
        _uiState.value = _uiState.value.copy(saveClipTitle = title)
    }

    fun confirmSaveClip() {
        val state = _uiState.value
        val title = if (state.saveClipTitle.isBlank()) "Indian Voice MP3" else state.saveClipTitle.trim()
        val text = state.textInput
        val outputMp3File = clipsRepo.createAudioOutputFile("desi_tts", "mp3")

        _uiState.value = _uiState.value.copy(
            isSynthesizingFile = true,
            saveDialogVisible = false
        )

        ttsManager.synthesizeToMp3File(
            text = text,
            title = title,
            language = state.selectedLanguage,
            voiceItem = state.selectedVoice,
            speechRate = state.speechRate,
            pitch = state.pitch,
            destinationMp3File = outputMp3File
        ) { success, error ->
            _uiState.value = _uiState.value.copy(isSynthesizingFile = false)
            if (success) {
                val clip = SavedClip(
                    id = "clip_${System.currentTimeMillis()}",
                    title = title,
                    text = text,
                    language = state.selectedLanguage,
                    voiceName = state.selectedVoice?.displayName ?: "Default",
                    speechRate = state.speechRate,
                    pitch = state.pitch,
                    filePath = outputMp3File.absolutePath,
                    createdAtMillis = System.currentTimeMillis(),
                    format = "MP3",
                    persona = state.selectedPersona.displayName
                )
                clipsRepo.addClip(clip)
                showSnackbar("Saved \"$title.mp3\" to your clips!")
            } else {
                showSnackbar("Could not save MP3: ${error ?: "Unknown error"}")
            }
        }
    }

    fun shareCurrentSpeech() {
        val state = _uiState.value
        if (state.textInput.isBlank()) {
            showSnackbar("Enter text first to share voice audio")
            return
        }

        val tempMp3File = clipsRepo.createAudioOutputFile("share_temp", "mp3")
        _uiState.value = _uiState.value.copy(isSynthesizingFile = true)

        ttsManager.synthesizeToMp3File(
            text = state.textInput,
            title = "Text to Voice - ${state.selectedLanguage.displayName}",
            language = state.selectedLanguage,
            voiceItem = state.selectedVoice,
            speechRate = state.speechRate,
            pitch = state.pitch,
            destinationMp3File = tempMp3File
        ) { success, error ->
            _uiState.value = _uiState.value.copy(isSynthesizingFile = false)
            if (success) {
                val clip = SavedClip(
                    id = "temp_${System.currentTimeMillis()}",
                    title = "Text to Voice - ${state.selectedLanguage.displayName}",
                    text = state.textInput,
                    language = state.selectedLanguage,
                    voiceName = state.selectedVoice?.displayName ?: "Default",
                    speechRate = state.speechRate,
                    pitch = state.pitch,
                    filePath = tempMp3File.absolutePath,
                    createdAtMillis = System.currentTimeMillis(),
                    format = "MP3",
                    persona = state.selectedPersona.displayName
                )
                clipsRepo.shareAudioClip(clip)
            } else {
                showSnackbar("Could not prepare MP3 audio: ${error ?: "Unknown error"}")
            }
        }
    }

    // Phrasebook actions
    fun setPhrasebookLanguage(lang: IndianLanguage) {
        _uiState.value = _uiState.value.copy(phrasebookLanguage = lang)
    }

    fun setPhraseCategory(category: PhraseCategory?) {
        _uiState.value = _uiState.value.copy(selectedPhraseCategory = category)
    }

    fun setPhraseSearch(query: String) {
        _uiState.value = _uiState.value.copy(phraseSearchQuery = query)
    }

    fun speakPhrase(phrase: IndianPhrase) {
        ttsManager.speak(
            text = phrase.scriptText,
            language = phrase.language,
            voiceItem = null,
            speechRate = _uiState.value.speechRate,
            pitch = _uiState.value.pitch
        )
    }

    fun sharePhraseAsMp3(phrase: IndianPhrase) {
        val tempMp3File = clipsRepo.createAudioOutputFile("phrase_${phrase.language.languageCode}", "mp3")
        _uiState.value = _uiState.value.copy(isSynthesizingFile = true)

        ttsManager.synthesizeToMp3File(
            text = phrase.scriptText,
            title = "${phrase.category.displayName} (${phrase.language.displayName})",
            language = phrase.language,
            voiceItem = null,
            speechRate = _uiState.value.speechRate,
            pitch = _uiState.value.pitch,
            destinationMp3File = tempMp3File
        ) { success, error ->
            _uiState.value = _uiState.value.copy(isSynthesizingFile = false)
            if (success) {
                val clip = SavedClip(
                    id = "phrase_share_${System.currentTimeMillis()}",
                    title = phrase.scriptText.take(30),
                    text = "${phrase.scriptText}\nMeaning: ${phrase.englishMeaning}",
                    language = phrase.language,
                    voiceName = "Default",
                    speechRate = _uiState.value.speechRate,
                    pitch = _uiState.value.pitch,
                    filePath = tempMp3File.absolutePath,
                    createdAtMillis = System.currentTimeMillis(),
                    format = "MP3"
                )
                clipsRepo.shareAudioClip(clip)
            } else {
                showSnackbar("Could not share MP3: ${error ?: "Unknown error"}")
            }
        }
    }

    fun loadPhraseIntoStudio(phrase: IndianPhrase) {
        _uiState.value = _uiState.value.copy(
            currentTab = AppNavTab.STUDIO,
            selectedLanguage = phrase.language,
            textInput = phrase.scriptText
        )
        showSnackbar("Loaded into Voice Studio!")
    }

    // Soundboard actions
    fun playSoundEffect(effect: IndianSoundEffect) {
        soundSynthesizer.playSound(effect)
    }

    // Saved clips playback
    fun togglePlaySavedClip(clip: SavedClip) {
        if (_uiState.value.playingClipId == clip.id) {
            stopSavedClipPlayback()
            return
        }

        stopSavedClipPlayback()

        try {
            val player = MediaPlayer().apply {
                setDataSource(clip.filePath)
                prepare()
                start()
            }
            mediaPlayer = player
            _uiState.value = _uiState.value.copy(playingClipId = clip.id)

            val duration = player.duration.coerceAtLeast(1)
            playbackProgressJob = viewModelScope.launch {
                while (isActive && player.isPlaying) {
                    val progress = player.currentPosition.toFloat() / duration
                    _uiState.value = _uiState.value.copy(playbackProgress = progress)
                    delay(100)
                }
                stopSavedClipPlayback()
            }

            player.setOnCompletionListener {
                stopSavedClipPlayback()
            }
        } catch (_: Exception) {
            showSnackbar("Cannot play audio file")
            stopSavedClipPlayback()
        }
    }

    fun stopSavedClipPlayback() {
        playbackProgressJob?.cancel()
        playbackProgressJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) { }
        mediaPlayer = null
        _uiState.value = _uiState.value.copy(playingClipId = null, playbackProgress = 0f)
    }

    fun deleteClip(clipId: String) {
        if (_uiState.value.playingClipId == clipId) {
            stopSavedClipPlayback()
        }
        clipsRepo.deleteClip(clipId)
        showSnackbar("Clip removed")
    }

    fun shareClip(clip: SavedClip) {
        clipsRepo.shareAudioClip(clip)
    }

    // Diagnostics & TTS setup
    fun showDiagnosticDialog() {
        _uiState.value = _uiState.value.copy(diagnosticDialogVisible = true)
    }

    fun dismissDiagnosticDialog() {
        _uiState.value = _uiState.value.copy(diagnosticDialogVisible = false)
    }

    fun openAndroidTtsSettings() {
        try {
            val intent = ttsManager.getTtsSettingsIntent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {
            showSnackbar("Could not open TTS settings on this device")
        }
    }

    fun openInstallTtsData() {
        try {
            val intent = ttsManager.getInstallTtsIntent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {
            showSnackbar("Could not open TTS installation flow")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        stopSavedClipPlayback()
        soundSynthesizer.release()
        ttsManager.destroy()
    }
}
