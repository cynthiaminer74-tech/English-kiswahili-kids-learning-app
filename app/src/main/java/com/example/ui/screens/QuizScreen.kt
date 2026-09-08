package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Flashcard
import com.example.model.VocabularyRepository
import com.example.ui.theme.*
import kotlinx.coroutines.delay

enum class QuizMode(val title: String, val emoji: String) {
  MULTIPLE_CHOICE("Tafuta Neno!", "🎯"),
  MEMORY_MATCH("Oanisha Kadi", "🃏"),
}

@Composable
fun QuizScreen(
  onSpeak: (String, Boolean) -> Unit,
  onAwardStar: (Int) -> Unit,
  onPlaySuccess: () -> Unit,
  isAudioMuted: Boolean,
  modifier: Modifier = Modifier,
) {
  var selectedMode by remember { mutableStateOf(QuizMode.MULTIPLE_CHOICE) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Mode Switcher Pills
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = AppSurfaceContainerHigh,
        modifier = Modifier.fillMaxWidth(0.9f)
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          QuizMode.entries.forEach { mode ->
            val isSelected = mode == selectedMode
            Button(
              onClick = { selectedMode = mode },
              shape = RoundedCornerShape(20.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) SavannaPrimaryContainer else Color.Transparent,
                contentColor = if (isSelected) SavannaOnPrimaryContainer else AppOnSurfaceVariant
              ),
              elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null,
              modifier = Modifier.weight(1f)
            ) {
              Text(
                text = "${mode.emoji} ${mode.title}",
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
              )
            }
          }
        }
      }
    }

    if (selectedMode == QuizMode.MULTIPLE_CHOICE) {
      MultipleChoiceQuiz(
        onSpeak = onSpeak,
        onAwardStar = onAwardStar,
        onPlaySuccess = onPlaySuccess,
        isAudioMuted = isAudioMuted
      )
    } else {
      MemoryMatchGame(
        onSpeak = onSpeak,
        onAwardStar = onAwardStar,
        onPlaySuccess = onPlaySuccess,
        isAudioMuted = isAudioMuted
      )
    }
  }
}

@Composable
private fun MultipleChoiceQuiz(
  onSpeak: (String, Boolean) -> Unit,
  onAwardStar: (Int) -> Unit,
  onPlaySuccess: () -> Unit,
  isAudioMuted: Boolean,
) {
  val allCards = remember { VocabularyRepository.flashcards }
  var questionIndex by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }
  var streak by remember { mutableIntStateOf(0) }

  // Pick target card and 3 distractors
  val targetCard = remember(questionIndex) {
    allCards[questionIndex % allCards.size]
  }

  val options = remember(questionIndex) {
    val distractors = allCards.filter { it.id != targetCard.id }.shuffled().take(3)
    (distractors + targetCard).shuffled()
  }

  var selectedOption by remember(questionIndex) { mutableStateOf<Flashcard?>(null) }
  var isCorrect by remember(questionIndex) { mutableStateOf<Boolean?>(null) }

  // Speak question prompt automatically when new question loads
  LaunchedEffect(questionIndex) {
    if (!isAudioMuted) {
      onSpeak("Ni ipi ${targetCard.kiswahili}?", false)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Score & Streak Tracker
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.EmojiEvents, contentDescription = null, tint = GoldStar, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "Alama: $score", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SavannaPrimary)
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "🔥 Mfululizo: $streak", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SunOrangeTertiary)
      }
    }

    // Question Box with Twiga Mascot Voice
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "TAFUTA NENO LA KISWAHILI:",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = SafariSecondary,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Ni ipi \"${targetCard.kiswahili}\"?",
          fontSize = 26.sp,
          fontWeight = FontWeight.Black,
          color = SavannaPrimary,
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Which one is \"${targetCard.kiswahili}\"?",
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          color = AppOnSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Big Audio Prompt Button
        Button(
          onClick = {
            if (!isAudioMuted) onSpeak(targetCard.kiswahili, false)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = SafariSecondaryContainer,
            contentColor = SafariOnSecondaryContainer
          ),
          shape = RoundedCornerShape(20.dp)
        ) {
          Icon(Icons.Rounded.VolumeUp, contentDescription = "Pronounce Word", modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Sikiliza Neno", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
      }
    }

    // 4 Big Choice Option Cards (2x2 Grid)
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      val chunked = options.chunked(2)
      chunked.forEach { rowOptions ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          rowOptions.forEach { opt ->
            val isChosen = selectedOption == opt
            val buttonColor = when {
              isChosen && isCorrect == true -> SafariSecondaryContainer
              isChosen && isCorrect == false -> Color(0xFFFFDAD6)
              else -> Color.White
            }
            val borderTint = when {
              isChosen && isCorrect == true -> SafariSecondary
              isChosen && isCorrect == false -> Color(0xFFBA1A1A)
              else -> SavannaPrimaryContainer.copy(alpha = 0.4f)
            }

            Card(
              shape = RoundedCornerShape(20.dp),
              colors = CardDefaults.cardColors(containerColor = buttonColor),
              border = CardDefaults.outlinedCardBorder().copy(width = 2.dp, brush = androidx.compose.ui.graphics.SolidColor(borderTint)),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              modifier = Modifier
                .weight(1f)
                .height(96.dp)
                .clickable(enabled = selectedOption == null) {
                  selectedOption = opt
                  val correct = opt.id == targetCard.id
                  isCorrect = correct
                  if (correct) {
                    score += 10
                    streak++
                    onAwardStar(10)
                    onPlaySuccess()
                    if (!isAudioMuted) onSpeak("Safi sana! Ni ${opt.kiswahili}!", false)
                  } else {
                    streak = 0
                    if (!isAudioMuted) onSpeak("Jaribu tena!", false)
                  }
                }
            ) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(text = opt.emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = opt.english,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = AppOnSurface,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }
      }
    }

    // Feedback message & Next Question Button
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(76.dp)
        .padding(bottom = 12.dp),
      contentAlignment = Alignment.Center
    ) {
      if (selectedOption != null) {
        if (isCorrect == true) {
          Button(
            onClick = {
              questionIndex++
            },
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SafariSecondary,
              contentColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(4.dp, RoundedCornerShape(26.dp))
          ) {
            Text(text = "🎉 Vizuri Sana! Swali Linalofuata ➔", fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = {
              selectedOption = null
              isCorrect = null
            },
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = SunOrangeTertiary,
              contentColor = Color.White
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .shadow(3.dp, RoundedCornerShape(26.dp))
          ) {
            Text(text = "Jaribu Tena! (Try Again)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

// Memory Match Game
data class MemoryCard(
  val id: Int,
  val cardRefId: String,
  val displayText: String,
  val emoji: String,
  val isSwahili: Boolean,
  var isFlipped: Boolean = false,
  var isMatched: Boolean = false,
)

@Composable
private fun MemoryMatchGame(
  onSpeak: (String, Boolean) -> Unit,
  onAwardStar: (Int) -> Unit,
  onPlaySuccess: () -> Unit,
  isAudioMuted: Boolean,
) {
  val allCards = remember { VocabularyRepository.flashcards.shuffled().take(4) }

  var cardsList by remember {
    val items = mutableListOf<MemoryCard>()
    var idCounter = 0
    allCards.forEach { card ->
      items.add(MemoryCard(id = idCounter++, cardRefId = card.id, displayText = card.english, emoji = card.emoji, isSwahili = false))
      items.add(MemoryCard(id = idCounter++, cardRefId = card.id, displayText = card.kiswahili, emoji = card.emoji, isSwahili = true))
    }
    mutableStateOf(items.shuffled())
  }

  var firstIndex by remember { mutableStateOf<Int?>(null) }
  var secondIndex by remember { mutableStateOf<Int?>(null) }
  var matchesFound by remember { mutableIntStateOf(0) }
  var isChecking by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header status
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Oanisha Kiingereza na Kiswahili",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SavannaPrimary
      )
      Text(
        text = "Zilizo sahihi: $matchesFound / ${allCards.size}",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SafariSecondary
      )
    }

    // Grid of 8 cards (2 columns x 4 rows or 4 columns x 2 rows)
    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.weight(1f)
    ) {
      itemsIndexed(cardsList) { index, card ->
        val isFaceUp = card.isFlipped || card.isMatched

        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = when {
              card.isMatched -> SafariSecondaryContainer
              isFaceUp -> Color.White
              else -> SavannaPrimaryContainer
            }
          ),
          border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = androidx.compose.ui.graphics.SolidColor(
              if (card.isMatched) SafariSecondary else SavannaPrimary
            )
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable(enabled = !isFaceUp && !isChecking) {
              if (firstIndex == null) {
                firstIndex = index
                cardsList = cardsList.toMutableList().also { it[index] = it[index].copy(isFlipped = true) }
                if (!isAudioMuted) onSpeak(card.displayText, !card.isSwahili)
              } else if (secondIndex == null && index != firstIndex) {
                secondIndex = index
                cardsList = cardsList.toMutableList().also { it[index] = it[index].copy(isFlipped = true) }
                if (!isAudioMuted) onSpeak(card.displayText, !card.isSwahili)

                // Check match
                isChecking = true
              }
            }
        ) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            if (isFaceUp) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(6.dp)
              ) {
                Text(text = card.emoji, fontSize = 28.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = card.displayText,
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Black,
                  color = if (card.isMatched) SafariOnSecondaryContainer else SavannaPrimary,
                  textAlign = TextAlign.Center
                )
                Text(
                  text = if (card.isSwahili) "Kiswahili" else "English",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = AppOnSurfaceVariant
                )
              }
            } else {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Text(text = "❓", fontSize = 28.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Gusa Hapa",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = SavannaOnPrimaryContainer
                )
              }
            }
          }
        }
      }
    }

    // Effect to check pair match
    LaunchedEffect(isChecking) {
      if (isChecking && firstIndex != null && secondIndex != null) {
        val c1 = cardsList[firstIndex!!]
        val c2 = cardsList[secondIndex!!]
        delay(900)
        if (c1.cardRefId == c2.cardRefId) {
          // Matched!
          cardsList = cardsList.toMutableList().also {
            it[firstIndex!!] = it[firstIndex!!].copy(isMatched = true)
            it[secondIndex!!] = it[secondIndex!!].copy(isMatched = true)
          }
          matchesFound++
          onAwardStar(15)
          onPlaySuccess()
          if (!isAudioMuted) onSpeak("Safi sana!", false)
        } else {
          // No match, flip back
          cardsList = cardsList.toMutableList().also {
            it[firstIndex!!] = it[firstIndex!!].copy(isFlipped = false)
            it[secondIndex!!] = it[secondIndex!!].copy(isFlipped = false)
          }
        }
        firstIndex = null
        secondIndex = null
        isChecking = false
      }
    }

    // Restart button when completed
    if (matchesFound >= allCards.size) {
      Button(
        onClick = {
          val newCards = VocabularyRepository.flashcards.shuffled().take(4)
          val items = mutableListOf<MemoryCard>()
          var idCounter = 0
          newCards.forEach { card ->
            items.add(MemoryCard(id = idCounter++, cardRefId = card.id, displayText = card.english, emoji = card.emoji, isSwahili = false))
            items.add(MemoryCard(id = idCounter++, cardRefId = card.id, displayText = card.kiswahili, emoji = card.emoji, isSwahili = true))
          }
          cardsList = items.shuffled()
          matchesFound = 0
        },
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SafariSecondary),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 12.dp)
          .height(52.dp)
      ) {
        Icon(Icons.Rounded.Replay, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Cheza Tena! (Play Again)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
