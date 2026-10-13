package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Text to Voice", appName)
  }

  @Test
  fun `verify indian languages support`() {
    val languages = com.example.model.IndianLanguage.values()
    assertEquals(6, languages.size)
    assertTrue(languages.any { it.languageCode == "mr" })
    assertTrue(languages.any { it.languageCode == "hi" })
    assertTrue(languages.any { it.languageCode == "en" })
    assertTrue(languages.any { it.languageCode == "ta" })
    assertTrue(languages.any { it.languageCode == "te" })
    assertTrue(languages.any { it.languageCode == "bn" })
  }

  @Test
  fun `verify indian phrases loaded`() {
    val phrases = com.example.model.IndianPhraseData.phrases
    assertTrue(phrases.isNotEmpty())
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.MARATHI })
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.HINDI })
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.INDIAN_ENGLISH })
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.TAMIL })
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.TELUGU })
    assertTrue(phrases.any { it.language == com.example.model.IndianLanguage.BENGALI })
  }

  @Test
  fun `verify voice personas available`() {
    val personas = com.example.model.IndianVoicePersona.values()
    assertTrue(personas.size >= 5)
    assertTrue(personas.any { it == com.example.model.IndianVoicePersona.CHILD_YOUTHFUL })
    assertTrue(personas.any { it == com.example.model.IndianVoicePersona.ADULT_FEMALE })
    assertTrue(personas.any { it == com.example.model.IndianVoicePersona.ADULT_MALE })
    assertTrue(personas.any { it == com.example.model.IndianVoicePersona.MATURE_ELDER })
  }
}
