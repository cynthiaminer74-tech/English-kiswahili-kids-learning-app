package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.SpeechHelper
import com.example.model.Category
import com.example.ui.components.AppHeader
import com.example.ui.components.AppTab
import com.example.ui.components.BottomNavBar
import com.example.ui.screens.CardsScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TwigaCornerScreen
import com.example.ui.theme.AppBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SafariSecondary
import com.example.ui.theme.SavannaPrimary
import com.example.ui.theme.SavannaPrimaryContainer

class MainActivity : ComponentActivity() {
  private lateinit var speechHelper: SpeechHelper

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    speechHelper = SpeechHelper(this)

    setContent {
      MyApplicationTheme {
        MainAppScreen(speechHelper = speechHelper)
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    speechHelper.shutdown()
  }
}

@Composable
fun MainAppScreen(speechHelper: SpeechHelper) {
  var currentTab by remember { mutableStateOf(AppTab.CARDS) }
  var selectedCategory by remember { mutableStateOf<Category?>(null) }
  var starsCount by remember { mutableIntStateOf(120) }
  var isAudioMuted by remember { mutableStateOf(false) }
  var speechRate by remember { mutableFloatStateOf(0.8f) }
  var showSyllables by remember { mutableStateOf(true) }
  var showProfileDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      AppHeader(
        starsCount = starsCount,
        isAudioMuted = isAudioMuted,
        onToggleAudio = { isAudioMuted = !isAudioMuted },
        onProfileClick = { showProfileDialog = true }
      )
    },
    bottomBar = {
      BottomNavBar(
        currentTab = currentTab,
        onTabSelected = { tab ->
          currentTab = tab
        }
      )
    },
    containerColor = AppBackground,
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentTab) {
        AppTab.CARDS -> {
          CardsScreen(
            selectedCategory = selectedCategory,
            onSelectCategory = { cat -> selectedCategory = cat },
            onSpeak = { text, isEn ->
              if (!isAudioMuted) speechHelper.speak(text, isEn)
            },
            onAwardStar = { added -> starsCount += added },
            onPlayFlipSound = { speechHelper.playFlipChime() },
            isAudioMuted = isAudioMuted,
            showSyllables = showSyllables
          )
        }

        AppTab.CATEGORIES -> {
          CategoriesScreen(
            onSelectCategory = { cat ->
              selectedCategory = cat
              currentTab = AppTab.CARDS
            }
          )
        }

        AppTab.QUIZ -> {
          QuizScreen(
            onSpeak = { text, isEn ->
              if (!isAudioMuted) speechHelper.speak(text, isEn)
            },
            onAwardStar = { added -> starsCount += added },
            onPlaySuccess = { speechHelper.playSuccessChime() },
            isAudioMuted = isAudioMuted
          )
        }

        AppTab.TWIGA -> {
          TwigaCornerScreen(
            speechRate = speechRate,
            onSpeechRateChange = { rate ->
              speechRate = rate
              speechHelper.setSpeechRate(rate)
            },
            showSyllables = showSyllables,
            onToggleSyllables = { showSyllables = it },
            onSpeak = { text, isEn ->
              if (!isAudioMuted) speechHelper.speak(text, isEn)
            },
            onPlaySuccess = { speechHelper.playSuccessChime() },
            isAudioMuted = isAudioMuted
          )
        }
      }
    }
  }

  // Learner Profile / Progress Dialog
  if (showProfileDialog) {
    Dialog(onDismissRequest = { showProfileDialog = false }) {
      Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(SavannaPrimaryContainer, RoundedCornerShape(32.dp)),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "🧒", fontSize = 36.sp)
          }

          Text(
            text = "Amani Wanjiku Kibet",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = SavannaPrimary
          )

          Text(
            text = "Grade 1 CBC • Lower Primary\nKenya Curriculum Framework",
            fontSize = 13.sp,
            color = SafariSecondary,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Divider(color = Color(0xFFF0F3FF))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "$starsCount", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SavannaPrimary)
              Text(text = "Stars Earned", fontSize = 11.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "8", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SafariSecondary)
              Text(text = "Stickers", fontSize = 11.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "48+", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SavannaPrimary)
              Text(text = "Words", fontSize = 11.sp, color = Color.Gray)
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Button(
            onClick = { showProfileDialog = false },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SavannaPrimary),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(text = "Rudi Kujifunza (Continue)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name! Karibu Maneno English Kiswahili Kids", modifier = modifier)
}

