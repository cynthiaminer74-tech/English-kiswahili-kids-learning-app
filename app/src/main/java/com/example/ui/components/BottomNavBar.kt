package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class AppTab(val title: String, val english: String, val icon: ImageVector) {
  CARDS("Kadi", "Cards", Icons.Rounded.Style),
  CATEGORIES("Makundi", "Topics", Icons.Rounded.GridView),
  QUIZ("Mchezo", "Games", Icons.Rounded.SportsEsports),
  TWIGA("Twiga", "Corner", Icons.Rounded.School),
}

@Composable
fun BottomNavBar(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
    shadowElevation = 8.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppTab.entries.forEach { tab ->
        val isSelected = tab == currentTab
        val bgAnimated by animateColorAsState(
          targetValue = if (isSelected) SavannaPrimaryContainer.copy(alpha = 0.25f) else Color.Transparent,
          animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
          label = "nav_bg"
        )
        val tintAnimated by animateColorAsState(
          targetValue = if (isSelected) SavannaPrimary else AppOnSurfaceVariant,
          label = "nav_tint"
        )

        val interactionSource = remember { MutableInteractionSource() }

        Column(
          modifier = Modifier
            .weight(1f)
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgAnimated)
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) { onTabSelected(tab) }
            .padding(vertical = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = tab.icon,
            contentDescription = tab.title,
            tint = tintAnimated,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = tab.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
            color = tintAnimated,
            maxLines = 1
          )
        }
      }
    }
  }
}
