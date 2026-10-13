package com.example.model

import java.util.Locale

enum class IndianLanguage(
    val displayName: String,
    val nativeName: String,
    val languageCode: String,
    val countryCode: String,
    val regionTag: String,
    val defaultText: String
) {
    MARATHI(
        displayName = "Marathi",
        nativeName = "मराठी",
        languageCode = "mr",
        countryCode = "IN",
        regionTag = "mr-IN",
        defaultText = "नमस्कार! मी तुमचा मराठी आवाज आहे. आपण कसे आहात?"
    ),
    HINDI(
        displayName = "Hindi",
        nativeName = "हिंदी",
        languageCode = "hi",
        countryCode = "IN",
        regionTag = "hi-IN",
        defaultText = "नमस्ते! मैं आपकी भारतीय हिंदी आवाज़ हूँ। आपका दिन बहुत शुभ और मंगलमय हो।"
    ),
    INDIAN_ENGLISH(
        displayName = "Indian English",
        nativeName = "English (India)",
        languageCode = "en",
        countryCode = "IN",
        regionTag = "en-IN",
        defaultText = "Namaste! Welcome to Indian Voice Studio. I am speaking in authentic Indian English accent."
    ),
    TAMIL(
        displayName = "Tamil",
        nativeName = "தமிழ்",
        languageCode = "ta",
        countryCode = "IN",
        regionTag = "ta-IN",
        defaultText = "வணக்கம்! நான் உங்கள் தமிழ் குரல். நீங்கள் எப்படி இருக்கிறீர்கள்?"
    ),
    TELUGU(
        displayName = "Telugu",
        nativeName = "తెలుగు",
        languageCode = "te",
        countryCode = "IN",
        regionTag = "te-IN",
        defaultText = "నమస్కారం! నేను మీ తెలుగు వాయిస్. మీరు ఎలా ఉన్నారు?"
    ),
    BENGALI(
        displayName = "Bengali",
        nativeName = "বাংলা",
        languageCode = "bn",
        countryCode = "IN",
        regionTag = "bn-IN",
        defaultText = "নমস্কার! আমি আপনার বাংলা কণ্ঠস্বর। আপনি কেমন আছেন?"
    );

    val locale: Locale
        get() = Locale(languageCode, countryCode)
}

enum class VoiceGenderFilter(val displayName: String, val icon: String) {
    ALL("All Voices", "👥"),
    FEMALE("Female", "👩"),
    MALE("Male", "👨")
}

enum class IndianVoicePersona(
    val id: String,
    val displayName: String,
    val icon: String,
    val defaultPitch: Float,
    val defaultSpeechRate: Float,
    val description: String,
    val preferredGender: String = "Natural"
) {
    NATURAL_DEFAULT(
        id = "natural",
        displayName = "Natural Voice",
        icon = "🧑",
        defaultPitch = 1.0f,
        defaultSpeechRate = 1.0f,
        description = "Balanced, authentic conversational Indian speech"
    ),
    ADULT_FEMALE(
        id = "female_adult",
        displayName = "Adult Female",
        icon = "👩",
        defaultPitch = 1.15f,
        defaultSpeechRate = 1.0f,
        description = "Clear, warm and polite feminine voice",
        preferredGender = "Female"
    ),
    ADULT_MALE(
        id = "male_adult",
        displayName = "Adult Male",
        icon = "👨",
        defaultPitch = 0.90f,
        defaultSpeechRate = 1.0f,
        description = "Confident, crisp and professional masculine tone",
        preferredGender = "Male"
    ),
    CHILD_YOUTHFUL(
        id = "youthful_kid",
        displayName = "Youthful / Kid",
        icon = "👧",
        defaultPitch = 1.35f,
        defaultSpeechRate = 1.10f,
        description = "High energy, bright and lively youthful tone"
    ),
    MATURE_ELDER(
        id = "mature_elder",
        displayName = "Mature Elder",
        icon = "👴",
        defaultPitch = 0.80f,
        defaultSpeechRate = 0.88f,
        description = "Deep, measured, and dignified wise elder cadence"
    ),
    STORYTELLER(
        id = "radio_host",
        displayName = "Radio / Storyteller",
        icon = "📻",
        defaultPitch = 0.95f,
        defaultSpeechRate = 0.92f,
        description = "Rich acoustic broadcast resonance with melodic pacing"
    ),
    CUSTOM(
        id = "custom",
        displayName = "Custom Tuning",
        icon = "⚙️",
        defaultPitch = 1.0f,
        defaultSpeechRate = 1.0f,
        description = "Manually adjusted pitch and rate controls"
    )
}

data class IndianVoiceItem(
    val id: String,
    val name: String,
    val displayName: String,
    val language: IndianLanguage,
    val isNetworkRequired: Boolean = false,
    val genderHint: String = "Natural" // Female, Male, Natural
)
