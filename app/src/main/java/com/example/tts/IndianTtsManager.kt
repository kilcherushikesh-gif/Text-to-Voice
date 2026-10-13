package com.example.tts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.example.audio.Mp3AudioExporter
import com.example.model.IndianLanguage
import com.example.model.IndianVoiceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

sealed class TtsLanguageStatus {
    object Checking : TtsLanguageStatus()
    object Available : TtsLanguageStatus()
    object MissingData : TtsLanguageStatus()
    object NotSupported : TtsLanguageStatus()
}

data class IndianTtsState(
    val isInitialized: Boolean = false,
    val isSpeaking: Boolean = false,
    val currentUtteranceId: String? = null,
    val activeLanguage: IndianLanguage = IndianLanguage.MARATHI,
    val languageStatuses: Map<IndianLanguage, TtsLanguageStatus> = emptyMap(),
    val availableVoices: Map<IndianLanguage, List<IndianVoiceItem>> = emptyMap(),
    val errorMessage: String? = null
) {
    val marathiStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.MARATHI] ?: TtsLanguageStatus.Checking
    val hindiStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.HINDI] ?: TtsLanguageStatus.Checking
    val englishStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.INDIAN_ENGLISH] ?: TtsLanguageStatus.Checking
    val tamilStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.TAMIL] ?: TtsLanguageStatus.Checking
    val teluguStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.TELUGU] ?: TtsLanguageStatus.Checking
    val bengaliStatus: TtsLanguageStatus get() = languageStatuses[IndianLanguage.BENGALI] ?: TtsLanguageStatus.Checking
}

class IndianTtsManager(
    private val context: Context,
    private val scope: CoroutineScope
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null

    private val _state = MutableStateFlow(IndianTtsState())
    val state: StateFlow<IndianTtsState> = _state.asStateFlow()

    // Amplitudes for animated audio visualizer (14 bars)
    private val _audioAmplitudes = MutableStateFlow(List(14) { 0.15f })
    val audioAmplitudes: StateFlow<List<Float>> = _audioAmplitudes.asStateFlow()

    private var visualizerJob: Job? = null
    private var fileSynthesisCallback: ((Boolean, String?) -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setupTtsEngine()
        } else {
            _state.value = _state.value.copy(
                isInitialized = false,
                errorMessage = "Failed to initialize Android Text-to-Speech engine. Please check TTS settings."
            )
        }
    }

    private fun setupTtsEngine() {
        val engine = tts ?: return

        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        isSpeaking = true,
                        currentUtteranceId = utteranceId
                    )
                    startVisualizerAnimation()
                }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch(Dispatchers.Main) {
                    _state.value = _state.value.copy(
                        isSpeaking = false,
                        currentUtteranceId = null
                    )
                    stopVisualizerAnimation()
                    if (utteranceId?.startsWith("file_") == true) {
                        fileSynthesisCallback?.invoke(true, null)
                        fileSynthesisCallback = null
                    }
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                handleTtsError(utteranceId, "Audio synthesis error occurred")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                handleTtsError(utteranceId, "TTS Error code: $errorCode")
            }
        })

        // Check availability of all supported Indian languages
        val statusMap = mutableMapOf<IndianLanguage, TtsLanguageStatus>()
        for (lang in IndianLanguage.values()) {
            statusMap[lang] = checkLanguageAvailability(lang.locale)
        }

        // Discover voices for each language
        val voicesMap = mutableMapOf<IndianLanguage, List<IndianVoiceItem>>()
        for (lang in IndianLanguage.values()) {
            voicesMap[lang] = discoverVoicesForLanguage(lang)
        }

        _state.value = _state.value.copy(
            isInitialized = true,
            languageStatuses = statusMap,
            availableVoices = voicesMap,
            errorMessage = null
        )
    }

    private fun handleTtsError(utteranceId: String?, message: String) {
        scope.launch(Dispatchers.Main) {
            _state.value = _state.value.copy(
                isSpeaking = false,
                currentUtteranceId = null,
                errorMessage = message
            )
            stopVisualizerAnimation()
            if (utteranceId?.startsWith("file_") == true) {
                fileSynthesisCallback?.invoke(false, message)
                fileSynthesisCallback = null
            }
        }
    }

    private fun checkLanguageAvailability(locale: Locale): TtsLanguageStatus {
        val result = tts?.isLanguageAvailable(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
        return when (result) {
            TextToSpeech.LANG_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_AVAILABLE,
            TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE -> TtsLanguageStatus.Available
            TextToSpeech.LANG_MISSING_DATA -> TtsLanguageStatus.MissingData
            else -> TtsLanguageStatus.NotSupported
        }
    }

    private fun discoverVoicesForLanguage(lang: IndianLanguage): List<IndianVoiceItem> {
        val engine = tts ?: return createDefaultVoiceItems(lang)
        val allVoices: Set<Voice>? = try {
            engine.voices
        } catch (_: Exception) {
            null
        }

        if (allVoices.isNullOrEmpty()) {
            return createDefaultVoiceItems(lang)
        }

        val matching = allVoices.filter { voice ->
            val vLocale = voice.locale
            when (lang) {
                IndianLanguage.MARATHI -> vLocale.language.equals("mr", ignoreCase = true)
                IndianLanguage.HINDI -> vLocale.language.equals("hi", ignoreCase = true)
                IndianLanguage.INDIAN_ENGLISH ->
                    vLocale.language.equals("en", ignoreCase = true) &&
                    (vLocale.country.equals("IN", ignoreCase = true) || voice.name.contains("in", ignoreCase = true))
                IndianLanguage.TAMIL -> vLocale.language.equals("ta", ignoreCase = true)
                IndianLanguage.TELUGU -> vLocale.language.equals("te", ignoreCase = true)
                IndianLanguage.BENGALI -> vLocale.language.equals("bn", ignoreCase = true)
            }
        }

        if (matching.isEmpty()) {
            return createDefaultVoiceItems(lang)
        }

        val list = mutableListOf<IndianVoiceItem>()
        // Default device voice
        list.add(
            IndianVoiceItem(
                id = "${lang.languageCode}_default",
                name = "default",
                displayName = "Default ${lang.displayName} Voice",
                language = lang,
                genderHint = "Natural"
            )
        )

        var femaleCount = 1
        var maleCount = 1

        matching.forEach { voice ->
            val vName = voice.name.lowercase()
            val gender = when {
                vName.contains("female") || vName.contains("-f-") || vName.contains("mrf") ||
                vName.contains("hif") || vName.contains("enf") || vName.contains("taf") ||
                vName.contains("tef") || vName.contains("bnf") -> "Female"
                vName.contains("male") || vName.contains("-m-") || vName.contains("mrm") ||
                vName.contains("him") || vName.contains("enm") || vName.contains("tam") ||
                vName.contains("tem") || vName.contains("bnm") -> "Male"
                else -> if (femaleCount <= maleCount) "Female" else "Male"
            }

            val count = if (gender == "Female") femaleCount++ else maleCount++
            val display = "Indian $gender Voice $count"

            list.add(
                IndianVoiceItem(
                    id = voice.name,
                    name = voice.name,
                    displayName = display,
                    language = lang,
                    isNetworkRequired = voice.isNetworkConnectionRequired,
                    genderHint = gender
                )
            )
        }

        return list
    }

    private fun createDefaultVoiceItems(lang: IndianLanguage): List<IndianVoiceItem> {
        return listOf(
            IndianVoiceItem(
                id = "${lang.languageCode}_default",
                name = "default",
                displayName = "Default ${lang.displayName} Voice",
                language = lang,
                genderHint = "Natural"
            ),
            IndianVoiceItem(
                id = "${lang.languageCode}_female_sim",
                name = "female_sim",
                displayName = "Indian Female Voice (Expressive)",
                language = lang,
                genderHint = "Female"
            ),
            IndianVoiceItem(
                id = "${lang.languageCode}_male_sim",
                name = "male_sim",
                displayName = "Indian Male Voice (Deep)",
                language = lang,
                genderHint = "Male"
            )
        )
    }

    fun speak(
        text: String,
        language: IndianLanguage,
        voiceItem: IndianVoiceItem?,
        speechRate: Float = 1.0f,
        pitch: Float = 1.0f
    ) {
        val engine = tts ?: return
        if (text.isBlank()) return

        engine.language = language.locale

        if (voiceItem != null && voiceItem.name != "default") {
            try {
                val available = engine.voices?.find { it.name == voiceItem.name }
                if (available != null) {
                    engine.voice = available
                }
            } catch (_: Exception) { }
        }

        var effectivePitch = pitch
        if (voiceItem != null) {
            if (voiceItem.genderHint == "Female" && voiceItem.name.contains("sim")) {
                effectivePitch = (pitch * 1.22f).coerceIn(0.5f, 2.0f)
            } else if (voiceItem.genderHint == "Male" && voiceItem.name.contains("sim")) {
                effectivePitch = (pitch * 0.88f).coerceIn(0.5f, 2.0f)
            }
        }

        engine.setPitch(effectivePitch)
        engine.setSpeechRate(speechRate)

        val utteranceId = "speak_${UUID.randomUUID()}"
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }

        engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
    }

    fun stop() {
        tts?.stop()
        _state.value = _state.value.copy(
            isSpeaking = false,
            currentUtteranceId = null
        )
        stopVisualizerAnimation()
    }

    fun synthesizeToMp3File(
        text: String,
        title: String,
        language: IndianLanguage,
        voiceItem: IndianVoiceItem?,
        speechRate: Float,
        pitch: Float,
        destinationMp3File: File,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val tempWavFile = File(context.cacheDir, "temp_tts_${System.currentTimeMillis()}.wav")

        synthesizeToWavFile(
            text = text,
            language = language,
            voiceItem = voiceItem,
            speechRate = speechRate,
            pitch = pitch,
            destinationFile = tempWavFile
        ) { success, error ->
            if (!success) {
                onComplete(false, error)
                return@synthesizeToWavFile
            }

            scope.launch(Dispatchers.IO) {
                try {
                    val converted = Mp3AudioExporter.convertWavToMp3(
                        wavFile = tempWavFile,
                        targetMp3File = destinationMp3File,
                        title = title,
                        language = language
                    )
                    tempWavFile.delete()

                    scope.launch(Dispatchers.Main) {
                        if (converted) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, "Failed to encode audio into MP3 format")
                        }
                    }
                } catch (e: Exception) {
                    tempWavFile.delete()
                    scope.launch(Dispatchers.Main) {
                        onComplete(false, e.localizedMessage ?: "Conversion error")
                    }
                }
            }
        }
    }

    fun synthesizeToWavFile(
        text: String,
        language: IndianLanguage,
        voiceItem: IndianVoiceItem?,
        speechRate: Float,
        pitch: Float,
        destinationFile: File,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val engine = tts
        if (engine == null) {
            onComplete(false, "TTS engine not ready")
            return
        }

        if (text.isBlank()) {
            onComplete(false, "Text cannot be empty")
            return
        }

        engine.language = language.locale
        if (voiceItem != null && voiceItem.name != "default") {
            try {
                val available = engine.voices?.find { it.name == voiceItem.name }
                if (available != null) {
                    engine.voice = available
                }
            } catch (_: Exception) { }
        }

        var effectivePitch = pitch
        if (voiceItem != null) {
            if (voiceItem.genderHint == "Female" && voiceItem.name.contains("sim")) {
                effectivePitch = (pitch * 1.22f).coerceIn(0.5f, 2.0f)
            } else if (voiceItem.genderHint == "Male" && voiceItem.name.contains("sim")) {
                effectivePitch = (pitch * 0.88f).coerceIn(0.5f, 2.0f)
            }
        }

        engine.setPitch(effectivePitch)
        engine.setSpeechRate(speechRate)

        val utteranceId = "file_${UUID.randomUUID()}"
        val params = Bundle()
        fileSynthesisCallback = onComplete

        val result = engine.synthesizeToFile(text, params, destinationFile, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            fileSynthesisCallback = null
            onComplete(false, "Failed to initiate file synthesis (error: $result)")
        }
    }

    private fun startVisualizerAnimation() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch(Dispatchers.Default) {
            while (isActive) {
                val bars = List(14) {
                    val base = 0.2f + Random.nextFloat() * 0.75f
                    base.coerceIn(0.1f, 1.0f)
                }
                _audioAmplitudes.value = bars
                delay(80)
            }
        }
    }

    private fun stopVisualizerAnimation() {
        visualizerJob?.cancel()
        visualizerJob = null
        _audioAmplitudes.value = List(14) { 0.15f }
    }

    fun getInstallTtsIntent(): Intent {
        return Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
    }

    fun getTtsSettingsIntent(): Intent {
        return Intent("com.android.settings.TTS_SETTINGS")
    }

    fun destroy() {
        stop()
        try {
            tts?.shutdown()
        } catch (_: Exception) { }
        tts = null
    }
}
