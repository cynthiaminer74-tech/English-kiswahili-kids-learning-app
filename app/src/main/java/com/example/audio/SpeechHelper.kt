package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class SpeechHelper(private val context: Context) {
  private var tts: TextToSpeech? = null
  private var isInitialized = false
  private var currentRate = 0.8f
  private var toneGenerator: ToneGenerator? = null

  init {
    try {
      toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
    } catch (e: Exception) {
      Log.w("SpeechHelper", "ToneGenerator init failed", e)
    }

    tts = TextToSpeech(context) { status ->
      if (status == TextToSpeech.SUCCESS) {
        isInitialized = true
        // Try Swahili locale first, or fallback gracefully
        val swahiliLocale = Locale("sw", "KE")
        val result = tts?.setLanguage(swahiliLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
          val generalSw = Locale("sw")
          val res2 = tts?.setLanguage(generalSw)
          if (res2 == TextToSpeech.LANG_MISSING_DATA || res2 == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts?.language = Locale.ENGLISH
          }
        }
        tts?.setSpeechRate(currentRate)
        tts?.setPitch(1.1f) // slightly cheerful, clear voice for young children
      }
    }
  }

  fun setSpeechRate(rate: Float) {
    currentRate = rate
    if (isInitialized) {
      tts?.setSpeechRate(rate)
    }
  }

  fun speak(text: String, isEnglish: Boolean = false) {
    if (!isInitialized || tts == null) return

    try {
      if (isEnglish) {
        tts?.language = Locale.ENGLISH
      } else {
        val swLocale = Locale("sw", "KE")
        val res = tts?.setLanguage(swLocale)
        if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
          tts?.setLanguage(Locale("sw"))
        }
      }
      tts?.setSpeechRate(currentRate)
      val params = Bundle()
      params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "maneno_tts")
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "maneno_tts")
    } catch (e: Exception) {
      Log.e("SpeechHelper", "TTS speak failed", e)
    }
  }

  fun playSuccessChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 200)
    } catch (e: Exception) {
      Log.w("SpeechHelper", "Chime failed", e)
    }
  }

  fun playFlipChime() {
    try {
      toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
    } catch (e: Exception) {
      Log.w("SpeechHelper", "Flip chime failed", e)
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
      toneGenerator?.release()
    } catch (e: Exception) {
      Log.e("SpeechHelper", "Shutdown failed", e)
    }
  }
}
