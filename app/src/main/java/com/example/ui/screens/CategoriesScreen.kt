package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.VocabularyRepository
import com.example.ui.theme.*

@Composable
fun CategoriesScreen(
  onSelectCategory: (Category) -> Unit,
  modifier: Modifier = Modifier,
) {
  val allCards = VocabularyRepository.flashcards

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(AppBackground)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
  ) {
    // Header Intro Banner
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SavannaPrimaryContainer.copy(alpha = 0.18f)),
        border = CardDefaults.outlinedCardBorder().copy(
          width = 1.dp,
          brush = androidx.compose.ui.graphics.SolidColor(SavannaPrimaryContainer)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(SavannaPrimaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(text = "📚", fontSize = 26.sp)
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "KENYA CBC GRADE 1",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SafariSecondary,
                letterSpacing = 0.5.sp
              )
            }
            Text(
              text = "Makundi ya Msamiati",
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = SavannaPrimary
            )
            Text(
              text = "Choose a topic below to practice flashcards and sound.",
              fontSize = 13.sp,
              color = AppOnSurfaceVariant
            )
          }
        }
      }
    }

    // List of Categories
    items(Category.entries.toTypedArray()) { category ->
      val count = allCards.count { it.category == category }
      CategoryCardItem(
        category = category,
        wordCount = count,
        onClick = { onSelectCategory(category) }
      )
    }

    // CBC Strands Reference Banner
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AppSurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Rounded.School,
              contentDescription = null,
              tint = SafariSecondary,
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "KICD Grade 1 Curriculum Strands",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = AppOnSurface
            )
          }

          StrandItem(
            icon = Icons.Rounded.Hearing,
            title = "Kusikiliza na Kuzungumza",
            desc = "Active listening and vocal prompt articulation"
          )
          StrandItem(
            icon = Icons.Rounded.MenuBook,
            title = "Kusoma (Reading Readiness)",
            desc = "Letter-sound blending and sight words in Swahili"
          )
          StrandItem(
            icon = Icons.Rounded.Psychology,
            title = "Msamiati (Vocabulary Building)",
            desc = "Contextual words for home, family, animals, and nature"
          )
        }
      }
    }
  }
}

@Composable
private fun CategoryCardItem(
  category: Category,
  wordCount: Int,
  onClick: () -> Unit,
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.weight(1f)
      ) {
        // Emoji Circle
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(category.accentColor.copy(alpha = 0.15f))
            .border(1.5.dp, category.accentColor.copy(alpha = 0.4f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(text = category.emoji, fontSize = 28.sp)
        }

        Column {
          Text(
            text = category.tag.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = category.accentColor,
            letterSpacing = 0.5.sp
          )
          Text(
            text = category.titleSw,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppOnSurface
          )
          Text(
            text = "${category.titleEn} • $wordCount Maneno",
            fontSize = 13.sp,
            color = AppOnSurfaceVariant
          )
        }
      }

      // Giant "Jifunze" Action Button
      Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = category.accentColor,
          contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        modifier = Modifier.shadow(2.dp, RoundedCornerShape(20.dp))
      ) {
        Text(text = "Jifunze", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
          imageVector = Icons.Rounded.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
private fun StrandItem(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  desc: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(AppSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
      .padding(10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .clip(CircleShape)
        .background(Color.White),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = SavannaPrimary,
        modifier = Modifier.size(18.dp)
      )
    }

    Column {
      Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppOnSurface)
      Text(text = desc, fontSize = 11.sp, color = AppOnSurfaceVariant)
    }
  }
}
