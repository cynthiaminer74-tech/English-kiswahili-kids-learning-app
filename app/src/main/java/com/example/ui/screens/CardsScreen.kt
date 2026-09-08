package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.Flashcard
import com.example.model.VocabularyRepository
import com.example.ui.theme.*

@Composable
fun CardsScreen(
  selectedCategory: Category?,
  onSelectCategory: (Category?) -> Unit,
  onSpeak: (String, Boolean) -> Unit,
  onAwardStar: (Int) -> Unit,
  onPlayFlipSound: () -> Unit,
  isAudioMuted: Boolean,
  showSyllables: Boolean,
  modifier: Modifier = Modifier,
) {
  val allCards = remember { VocabularyRepository.flashcards }
  val filteredCards = remember(selectedCategory) {
    if (selectedCategory == null) allCards else allCards.filter { it.category == selectedCategory }
  }

  var currentIndex by remember(selectedCategory) { mutableIntStateOf(0) }
  val currentCard = filteredCards.getOrNull(currentIndex) ?: allCards.first()

  var isFlipped by remember(currentCard.id) { mutableStateOf(false) }

  // 3D Card flip animation
  val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
    label = "card_flip"
  )

  // Star award celebration effect
  var showCelebration by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(bottom = 12.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. Category Filter Selector Row
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      item {
        val isAllSelected = selectedCategory == null
        FilterChip(
          selected = isAllSelected,
          onClick = { onSelectCategory(null) },
          label = {
            Text(
              text = "🌟 Zote (${allCards.size})",
              fontWeight = if (isAllSelected) FontWeight.ExtraBold else FontWeight.Medium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SavannaPrimary,
            selectedLabelColor = Color.White,
            containerColor = AppSurfaceContainerLowest,
            labelColor = SavannaPrimary
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isAllSelected,
            borderColor = if (isAllSelected) SavannaPrimary else SavannaPrimaryContainer.copy(alpha = 0.5f)
          ),
          shape = RoundedCornerShape(20.dp)
        )
      }

      items(Category.entries.toTypedArray()) { cat ->
        val isSelected = selectedCategory == cat
        val catCount = allCards.count { it.category == cat }
        FilterChip(
          selected = isSelected,
          onClick = { onSelectCategory(cat) },
          label = {
            Text(
              text = "${cat.emoji} ${cat.titleSw} ($catCount)",
              fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = cat.accentColor,
            selectedLabelColor = Color.White,
            containerColor = AppSurfaceContainerLowest,
            labelColor = AppOnSurface
          ),
          border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = if (isSelected) cat.accentColor else cat.accentColor.copy(alpha = 0.4f)
          ),
          shape = RoundedCornerShape(20.dp)
        )
      }
    }

    // 2. Card Counter & Indicator
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "Kadi ${currentIndex + 1} ya ${filteredCards.size}",
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SavannaPrimary
      )

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.TouchApp,
          contentDescription = null,
          tint = SafariSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isFlipped) "Gusa kugeuza nyuma" else "Gusa kusikia Kiswahili!",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = SafariSecondary
        )
      }
    }

    // 3. Main 3D Flipping Flashcard with Swipe Detection
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp)
        .pointerInput(filteredCards.size, currentIndex) {
          detectHorizontalDragGestures { _, dragAmount ->
            if (dragAmount > 50 && currentIndex > 0) {
              currentIndex--
              isFlipped = false
              onPlayFlipSound()
            } else if (dragAmount < -50 && currentIndex < filteredCards.size - 1) {
              currentIndex++
              isFlipped = false
              onPlayFlipSound()
            }
          }
        },
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxSize()
          .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12 * density
          }
          .shadow(
            elevation = 10.dp,
            shape = RoundedCornerShape(28.dp),
            ambientColor = currentCard.category.accentColor.copy(alpha = 0.2f),
            spotColor = currentCard.category.accentColor.copy(alpha = 0.35f)
          )
          .clickable {
            onPlayFlipSound()
            val willBeFlipped = !isFlipped
            isFlipped = willBeFlipped
            if (willBeFlipped && !isAudioMuted) {
              onSpeak(currentCard.kiswahili, false)
            }
          },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isFlipped) currentCard.category.lightBg else Color.White
        ),
        border = CardDefaults.outlinedCardBorder().copy(
          width = 3.dp,
          brush = Brush.linearGradient(
            listOf(
              currentCard.category.accentColor,
              SavannaPrimaryContainer
            )
          )
        )
      ) {
        if (rotation <= 90f) {
          // FRONT SIDE: English Word + Emoji + Tap Cue
          CardFrontContent(
            card = currentCard,
            onSpeakEnglish = {
              if (!isAudioMuted) onSpeak(currentCard.english, true)
            }
          )
        } else {
          // BACK SIDE: Kiswahili Word + Syllables + Big Audio Speaker + Example
          Box(
            modifier = Modifier
              .fillMaxSize()
              .graphicsLayer { rotationY = 180f }
          ) {
            CardBackContent(
              card = currentCard,
              showSyllables = showSyllables,
              onSpeakSwahili = {
                if (!isAudioMuted) onSpeak(currentCard.kiswahili, false)
              }
            )
          }
        }
      }

      // Star Burst overlay when "Najua!" is clicked
      if (showCelebration) {
        CelebrationBadge()
      }
    }

    // 4. Giant Navigation and Mastery Buttons
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Row of Prev / Voice / Next
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Prev Button
        FilledTonalButton(
          onClick = {
            if (currentIndex > 0) {
              currentIndex--
              isFlipped = false
              onPlayFlipSound()
            }
          },
          enabled = currentIndex > 0,
          modifier = Modifier
            .size(54.dp)
            .shadow(2.dp, CircleShape),
          shape = CircleShape,
          contentPadding = PaddingValues(0.dp),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppSurfaceContainerLowest,
            contentColor = SavannaPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = "Previous Card",
            modifier = Modifier.size(28.dp)
          )
        }

        // Giant Pronounce / Speak Button
        Button(
          onClick = {
            if (!isAudioMuted) {
              onSpeak(currentCard.kiswahili, false)
            }
          },
          modifier = Modifier
            .height(54.dp)
            .weight(1f)
            .padding(horizontal = 10.dp)
            .shadow(4.dp, RoundedCornerShape(27.dp)),
          shape = RoundedCornerShape(27.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = SavannaPrimary,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Rounded.VolumeUp,
            contentDescription = "Pronounce Kiswahili",
            modifier = Modifier.size(26.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Sikia Kiswahili",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Next Button
        FilledTonalButton(
          onClick = {
            if (currentIndex < filteredCards.size - 1) {
              currentIndex++
              isFlipped = false
              onPlayFlipSound()
            }
          },
          enabled = currentIndex < filteredCards.size - 1,
          modifier = Modifier
            .size(54.dp)
            .shadow(2.dp, CircleShape),
          shape = CircleShape,
          contentPadding = PaddingValues(0.dp),
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = AppSurfaceContainerLowest,
            contentColor = SavannaPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Rounded.ArrowForward,
            contentDescription = "Next Card",
            modifier = Modifier.size(28.dp)
          )
        }
      }

      // "Najua!" (I know it!) Mastery Star Button
      Button(
        onClick = {
          onAwardStar(5)
          showCelebration = true
          if (!isAudioMuted) {
            onSpeak("Hongera! Umejua ${currentCard.kiswahili}!", false)
          }
          if (currentIndex < filteredCards.size - 1) {
            currentIndex++
            isFlipped = false
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .shadow(3.dp, RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = SafariSecondary,
          contentColor = Color.White
        )
      ) {
        Icon(
          imageVector = Icons.Rounded.Star,
          contentDescription = "Award Star",
          tint = GoldStar,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Najua Neno Hili! (+5 ⭐)",
          fontSize = 15.sp,
          fontWeight = FontWeight.ExtraBold
        )
      }
    }
  }

  LaunchedEffect(showCelebration) {
    if (showCelebration) {
      kotlinx.coroutines.delay(1600)
      showCelebration = false
    }
  }
}

@Composable
private fun CardFrontContent(
  card: Flashcard,
  onSpeakEnglish: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Badge & Audio Hint
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = card.category.titleSw.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = card.category.accentColor,
        letterSpacing = 1.sp,
        modifier = Modifier
          .background(card.category.accentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      )

      IconButton(
        onClick = onSpeakEnglish,
        modifier = Modifier
          .size(36.dp)
          .background(AppSurfaceVariant, CircleShape)
      ) {
        Icon(
          imageVector = Icons.Rounded.VolumeDown,
          contentDescription = "Hear English",
          tint = SavannaPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    // Huge Emoji Center
    Box(
      modifier = Modifier
        .size(140.dp)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            listOf(card.category.accentColor.copy(alpha = 0.15f), Color.Transparent)
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = card.emoji,
        fontSize = 80.sp,
        textAlign = TextAlign.Center
      )
    }

    // English Word in Big Display Type
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = card.english,
        fontSize = 34.sp,
        fontWeight = FontWeight.ExtraBold,
        color = AppOnSurface,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Gusa kugeuza na kuona Kiswahili 🔄",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppOnSurfaceVariant,
        textAlign = TextAlign.Center
      )
    }

    // Example sentence preview
    Text(
      text = "\"${card.exampleSentenceEn}\"",
      fontSize = 13.sp,
      fontWeight = FontWeight.Normal,
      color = AppOnSurfaceVariant,
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .background(AppSurfaceVariant.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
        .padding(horizontal = 12.dp, vertical = 8.dp)
    )
  }
}

@Composable
private fun CardBackContent(
  card: Flashcard,
  showSyllables: Boolean,
  onSpeakSwahili: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(22.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Badge: Swahili Sanifu + English reference
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .background(SafariSecondary.copy(alpha = 0.14f), RoundedCornerShape(8.dp))
          .padding(horizontal = 10.dp, vertical = 4.dp)
      ) {
        Icon(
          imageVector = Icons.Rounded.CheckCircle,
          contentDescription = null,
          tint = SafariSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Kiswahili Sanifu",
          fontSize = 12.sp,
          fontWeight = FontWeight.ExtraBold,
          color = SafariSecondary
        )
      }

      Text(
        text = card.english,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AppOnSurfaceVariant
      )
    }

    // Kiswahili Word Center Display
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text(
        text = card.emoji,
        fontSize = 48.sp,
        modifier = Modifier.padding(bottom = 6.dp)
      )

      Text(
        text = card.kiswahili,
        fontSize = 36.sp,
        fontWeight = FontWeight.Black,
        color = SavannaPrimary,
        textAlign = TextAlign.Center
      )

      // Syllable Breakdown Pill (e.g. [ SI • MBA ])
      if (showSyllables) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "[ ${card.syllables} ]",
          fontSize = 15.sp,
          fontWeight = FontWeight.ExtraBold,
          color = SunOrangeTertiary,
          letterSpacing = 1.sp,
          modifier = Modifier
            .background(SunOrangeTertiaryContainer.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .border(1.dp, SunOrangeTertiaryContainer, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 4.dp)
        )
      }
    }

    // Interactive Speaker Pulse Button in Center Back
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = 1.12f,
      animationSpec = infiniteRepeatable(
        animation = tween(1000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "speaker_pulse"
    )

    Box(
      modifier = Modifier
        .size(68.dp)
        .scale(pulseScale)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(SavannaPrimary, SavannaPrimaryContainer)
          )
        )
        .clickable { onSpeakSwahili() },
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Rounded.VolumeUp,
        contentDescription = "Pronounce Word Aloud",
        tint = Color.White,
        modifier = Modifier.size(34.dp)
      )
    }

    // Context Sentence & Fun Fact
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = "💬 \"${card.exampleSentenceSw}\"",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = SafariSecondary,
        textAlign = TextAlign.Center,
        modifier = Modifier
          .fillMaxWidth()
          .background(SafariSecondaryContainer.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      )

      Text(
        text = "💡 ${card.funFact}",
        fontSize = 12.sp,
        color = AppOnSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }
  }
}

@Composable
private fun CelebrationBadge() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(28.dp)),
    contentAlignment = Alignment.Center
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(text = "🌟", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Hongera Sana!",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = SavannaPrimary
        )
        Text(
          text = "+5 Stars Imepatikana!",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = SafariSecondary
        )
      }
    }
  }
}
