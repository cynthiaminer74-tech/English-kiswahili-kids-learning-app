package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Sticker
import com.example.model.VocabularyRepository
import com.example.ui.theme.*

enum class TwigaSubTab {
  STICKERS,
  CERTIFICATE,
}

@Composable
fun TwigaCornerScreen(
  speechRate: Float,
  onSpeechRateChange: (Float) -> Unit,
  showSyllables: Boolean,
  onToggleSyllables: (Boolean) -> Unit,
  onSpeak: (String, Boolean) -> Unit,
  onPlaySuccess: () -> Unit,
  isAudioMuted: Boolean,
  modifier: Modifier = Modifier,
) {
  var activeSubTab by remember { mutableStateOf(TwigaSubTab.STICKERS) }
  val context = LocalContext.current

  var studentName by remember { mutableStateOf("Amani Wanjiku Kibet") }
  var isEditingName by remember { mutableStateOf(false) }

  // Walimu Tip of the Day tips list
  val teacherTips = listOf(
    "\"Use physical pointing during daily breakfast! Say 'Hiki ni kikombe' (This is a cup) and have your Grade 1 child tap the matching card on Maneno for 3-second reinforcement.\"",
    "\"Sing the numbers aloud! 'Moja, mbili, tatu...' while climbing steps or counting mangoes together.\"",
    "\"Reward effort, not just speed! Praising 'Umejaribu vizuri sana' builds resilient Kiswahili speaking confidence.\"",
    "\"Point out animal signs during bus or car rides: Simba, Tembo, and Twiga are great conversation starters!\""
  )
  var tipIndex by remember { mutableIntStateOf(0) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Sub-Tab Switcher: "Stika Zangu" vs "Cheti Changu"
    item {
      Surface(
        shape = RoundedCornerShape(24.dp),
        color = AppSurfaceContainerHigh,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Button(
            onClick = { activeSubTab = TwigaSubTab.STICKERS },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (activeSubTab == TwigaSubTab.STICKERS) SavannaPrimaryContainer else Color.Transparent,
              contentColor = if (activeSubTab == TwigaSubTab.STICKERS) SavannaOnPrimaryContainer else AppOnSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Rounded.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Stika Zangu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = { activeSubTab = TwigaSubTab.CERTIFICATE },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (activeSubTab == TwigaSubTab.CERTIFICATE) SavannaPrimaryContainer else Color.Transparent,
              contentColor = if (activeSubTab == TwigaSubTab.CERTIFICATE) SavannaOnPrimaryContainer else AppOnSurfaceVariant
            ),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Cheti Changu", fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // 2. Twiga Greeting Banner
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(68.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(listOf(SavannaPrimaryContainer, SunOrangeTertiaryContainer))
              )
              .border(2.dp, SavannaPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.twiga_mascot),
              contentDescription = "Twiga Cheerful Mascot",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "CBC GRADE 1 HERO",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SafariSecondary,
                letterSpacing = 0.5.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(Icons.Rounded.Star, contentDescription = null, tint = GoldStar, modifier = Modifier.size(14.dp))
            }
            Text(
              text = "Hongera sana! 🌟",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = SavannaPrimary
            )
            Text(
              text = "Umepata stika 8 kati ya 10! Unaendelea vizuri sana na Kiswahili!",
              fontSize = 12.sp,
              color = AppOnSurfaceVariant,
              lineHeight = 16.sp
            )
          }
        }
      }
    }

    if (activeSubTab == TwigaSubTab.STICKERS) {
      // 3. Acacia Seed Meter Progress Bar
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CollectionsBookmark, contentDescription = null, tint = SavannaPrimary, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Kitabu cha Stika (Album)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppOnSurface)
              }
              Text(
                text = "8 / 10 Zimepatikana",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SafariSecondary,
                modifier = Modifier
                  .background(SafariSecondaryContainer, RoundedCornerShape(12.dp))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            // Progress bar
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CircleShape)
                .background(AppSurfaceVariant)
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth(0.80f)
                  .fillMaxHeight()
                  .clip(CircleShape)
                  .background(
                    Brush.horizontalGradient(listOf(SavannaPrimaryContainer, SafariSecondary))
                  )
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "80% Kamili", fontSize = 11.sp, color = AppOnSurfaceVariant)
              Text(text = "Stika 2 zimebaki kufungua Mchezo Mkubwa!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SunOrangeTertiary)
            }
          }
        }
      }

      // 4. Collectable 3D Badges Grid
      item {
        Text(
          text = "Gusa stika kusikia sauti na shangwe! 🦁✨",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = SavannaPrimary,
          modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
      }

      val stickers = VocabularyRepository.stickers
      val chunkedStickers = stickers.chunked(2)
      items(chunkedStickers.size) { rowIndex ->
        val rowItems = chunkedStickers[rowIndex]
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          rowItems.forEach { sticker ->
            Box(modifier = Modifier.weight(1f)) {
              StickerBadgeCard(
                sticker = sticker,
                onClick = {
                  if (sticker.isUnlocked) {
                    onPlaySuccess()
                    if (!isAudioMuted) {
                      onSpeak(sticker.cheerText, false)
                    }
                    Toast.makeText(context, sticker.cheerText, Toast.LENGTH_SHORT).show()
                  } else {
                    Toast.makeText(context, "Kamilisha: ${sticker.requirementText}", Toast.LENGTH_SHORT).show()
                  }
                }
              )
            }
          }
          if (rowItems.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    } else {
      // 5. Republic of Kenya CBC Certificate View
      item {
        Card(
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = CardDefaults.outlinedCardBorder().copy(
            width = 3.dp,
            brush = Brush.linearGradient(listOf(SavannaPrimaryContainer, SafariSecondary, SunOrangeTertiaryContainer))
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Certificate Gold Seal Icon
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                  Brush.radialGradient(listOf(SavannaPrimaryContainer, SavannaPrimary))
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Rounded.WorkspacePremium, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "REPUBLIC OF KENYA • CBC CURRICULUM",
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold,
              color = SafariSecondary,
              letterSpacing = 1.sp
            )
            Text(
              text = "CHETI CHA UMAHIRI",
              fontSize = 24.sp,
              fontWeight = FontWeight.Black,
              color = AppOnSurface,
              letterSpacing = (-0.5).sp
            )
            Text(
              text = "Grade 1 Kiswahili & English Star",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = SavannaPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Tunathibitisha kwa furaha kubwa kwamba:",
              fontSize = 12.sp,
              color = AppOnSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Student Name Plaque (Editable for Personalization!)
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = AppSurfaceContainerHigh,
              modifier = Modifier
                .clickable { isEditingName = true }
                .padding(vertical = 4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
              ) {
                Icon(Icons.Rounded.ChildCare, contentDescription = null, tint = SavannaPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = studentName,
                  fontSize = 17.sp,
                  fontWeight = FontWeight.Black,
                  color = AppOnSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Rounded.Edit, contentDescription = "Edit Name", tint = SafariSecondary, modifier = Modifier.size(16.dp))
              }
            }

            if (isEditingName) {
              OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("Jina la Mwanafunzi (Student Name)") },
                trailingIcon = {
                  IconButton(onClick = { isEditingName = false }) {
                    Icon(Icons.Rounded.Check, contentDescription = "Done")
                  }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Amefaulu kwa uhodari mkubwa kujifunza zaidi ya maneno 40 ya Kiswahili na Kiingereza katika ngazi ya Grade 1 CBC kwa ufasaha, shauku, na alama za juu!",
              fontSize = 13.sp,
              textAlign = TextAlign.Center,
              lineHeight = 18.sp,
              color = AppOnSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Official Seal & Signatures Strip
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .background(AppSurfaceVariant, RoundedCornerShape(16.dp))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Pets, contentDescription = null, tint = SavannaPrimary, modifier = Modifier.size(22.dp))
                Text(text = "Twiga Rafiki", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Mascot & Kiongozi", fontSize = 9.sp, color = AppOnSurfaceVariant)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GoldStar),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Rounded.MilitaryTech, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Text(text = "KICD LEVEL 1", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = SavannaPrimary)
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.Draw, contentDescription = null, tint = SafariSecondary, modifier = Modifier.size(22.dp))
                Text(text = "Mwalimu Jane", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Lead CBC Educator", fontSize = 9.sp, color = AppOnSurfaceVariant)
              }
            }
          }
        }

        // Action Buttons for Certificate
        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = {
            Toast.makeText(context, "Cheti cha $studentName kimehifadhiwa! 🖨️ Ready to print!", Toast.LENGTH_LONG).show()
          },
          shape = RoundedCornerShape(24.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SavannaPrimaryContainer, contentColor = SavannaOnPrimaryContainer),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(3.dp, RoundedCornerShape(24.dp))
        ) {
          Icon(Icons.Rounded.Download, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Pakua Cheti (Download PDF / Print) 🖨️", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))
        FilledTonalButton(
          onClick = {
            Toast.makeText(context, "Cheti kimeshirikiwa kwa familia! 📲", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(24.dp),
          modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
          Icon(Icons.Rounded.Share, contentDescription = null, tint = SafariSecondary)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Shiriki na Wazazi (Share with Family) 📲", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SafariSecondary)
        }
      }
    }

    // 6. Pronunciation & Audio Tweaks Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Rounded.Tune, contentDescription = null, tint = SavannaPrimary, modifier = Modifier.size(22.dp))
            Text(
              text = "Pronunciation & Audio Tweaks",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = AppOnSurface
            )
          }

          // Speech Speed Slider (0.6x to 1.0x)
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "Speech Speed", fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(
                text = when {
                  speechRate <= 0.65f -> "Very Slow (0.6x)"
                  speechRate <= 0.85f -> "Beginner (0.8x)"
                  else -> "Normal Speed (1.0x)"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SavannaPrimary
              )
            }
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = "🐢", fontSize = 20.sp)
              Slider(
                value = speechRate,
                onValueChange = onSpeechRateChange,
                valueRange = 0.6f..1.0f,
                steps = 3,
                colors = SliderDefaults.colors(
                  thumbColor = SavannaPrimaryContainer,
                  activeTrackColor = SavannaPrimary
                ),
                modifier = Modifier
                  .weight(1f)
                  .padding(horizontal = 8.dp)
              )
              Text(text = "🐇", fontSize = 20.sp)
            }
          }

          Divider(color = AppSurfaceVariant)

          // Syllable breakdown toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Syllable Breakdown Badges", fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(
                text = "Shows [SI • MBA] and [M • BWA] guides on card back",
                fontSize = 12.sp,
                color = AppOnSurfaceVariant
              )
            }
            Switch(
              checked = showSyllables,
              onCheckedChange = onToggleSyllables,
              colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SavannaPrimary)
            )
          }
        }
      }
    }

    // 7. Walimu Tip of the Day
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.TipsAndUpdates, contentDescription = null, tint = SavannaPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Walimu Tip of the Day", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SavannaPrimary)
          }

          Text(
            text = teacherTips[tipIndex % teacherTips.size],
            fontSize = 13.sp,
            color = AppOnSurface,
            lineHeight = 18.sp
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Mwalimu Jane, Nairobi", fontSize = 11.sp, color = AppOnSurfaceVariant)
            TextButton(
              onClick = { tipIndex++ },
              colors = ButtonDefaults.textButtonColors(contentColor = SavannaPrimary)
            ) {
              Text(text = "Next Tip ➔", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 8. SEO 3-Word Phrases & Curriculum Discovery Tags
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = "CURRICULUM & SEARCH DISCOVERY TAGS",
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          color = AppOnSurfaceVariant,
          letterSpacing = 0.5.sp
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TagChip(text = "# English Kiswahili Kids", isPrimary = true)
          TagChip(text = "# Kiswahili for kids", isPrimary = false)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TagChip(text = "# Grade 1 Kiswahili CBC", isPrimary = false)
          TagChip(text = "# learn Kiswahili Kenya", isPrimary = false)
        }
      }
    }
  }
}

@Composable
private fun StickerBadgeCard(
  sticker: Sticker,
  onClick: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (sticker.isUnlocked) Color.White else AppSurfaceVariant.copy(alpha = 0.6f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (sticker.isUnlocked) 3.dp else 0.dp),
    border = CardDefaults.outlinedCardBorder().copy(
      width = 1.dp,
      brush = androidx.compose.ui.graphics.SolidColor(
        if (sticker.isUnlocked) SavannaPrimaryContainer.copy(alpha = 0.4f) else Color.Transparent
      )
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Text(
          text = sticker.badgeText,
          fontSize = 10.sp,
          fontWeight = FontWeight.ExtraBold,
          color = if (sticker.isUnlocked) SafariSecondary else AppOnSurfaceVariant,
          modifier = Modifier
            .background(
              if (sticker.isUnlocked) SafariSecondaryContainer else AppSurfaceVariant,
              RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Box(
        modifier = Modifier
          .size(76.dp)
          .clip(CircleShape)
          .background(
            if (sticker.isUnlocked) SavannaPrimaryContainer.copy(alpha = 0.2f) else AppSurfaceVariant
          ),
        contentAlignment = Alignment.Center
      ) {
        if (sticker.isUnlocked) {
          Image(
            painter = painterResource(id = sticker.drawableRes),
            contentDescription = sticker.nameSw,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        } else {
          Icon(Icons.Rounded.Lock, contentDescription = "Locked", tint = AppOnSurfaceVariant, modifier = Modifier.size(32.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = sticker.nameSw,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = if (sticker.isUnlocked) AppOnSurface else AppOnSurfaceVariant,
        textAlign = TextAlign.Center
      )
      Text(
        text = if (sticker.isUnlocked) sticker.nameEn else sticker.requirementText,
        fontSize = 11.sp,
        color = if (sticker.isUnlocked) AppOnSurfaceVariant else SunOrangeTertiary,
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
private fun TagChip(text: String, isPrimary: Boolean) {
  Text(
    text = text,
    fontSize = 12.sp,
    fontWeight = if (isPrimary) FontWeight.ExtraBold else FontWeight.Bold,
    color = if (isPrimary) SavannaPrimary else AppOnSurfaceVariant,
    modifier = Modifier
      .background(
        if (isPrimary) SavannaPrimaryContainer.copy(alpha = 0.2f) else AppSurfaceContainerHigh,
        RoundedCornerShape(12.dp)
      )
      .padding(horizontal = 10.dp, vertical = 6.dp)
  )
}
