package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun AppHeader(
  starsCount: Int,
  isAudioMuted: Boolean,
  onToggleAudio: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
    shadowElevation = 3.dp,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Twiga Mascot + Title
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(SavannaPrimaryContainer, SunOrangeTertiaryContainer)
              )
            )
            .border(2.dp, SavannaPrimary, CircleShape)
            .shadow(2.dp, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.twiga_mascot),
            contentDescription = "Twiga the Giraffe Mascot",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        Column {
          Text(
            text = "Maneno!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SavannaPrimary,
            letterSpacing = (-0.5).sp
          )
          Text(
            text = "English Kiswahili Kids",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SafariSecondary,
            letterSpacing = 0.2.sp
          )
        }
      }

      // Right: Volume button + Star counter + Profile
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Audio Mute/Unmute
        IconButton(
          onClick = onToggleAudio,
          modifier = Modifier
            .size(40.dp)
            .background(AppSurfaceVariant, CircleShape)
        ) {
          Icon(
            imageVector = if (isAudioMuted) Icons.Rounded.VolumeOff else Icons.Rounded.VolumeUp,
            contentDescription = "Toggle Audio",
            tint = if (isAudioMuted) Color.Gray else SavannaPrimary,
            modifier = Modifier.size(22.dp)
          )
        }

        // Animated Star Badge
        val infiniteTransition = rememberInfiniteTransition(label = "star_pulse")
        val scale by infiniteTransition.animateFloat(
          initialValue = 1f,
          targetValue = 1.08f,
          animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
          ),
          label = "scale"
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SavannaPrimaryContainer.copy(alpha = 0.22f))
            .border(1.dp, SavannaPrimaryContainer.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 9.dp, vertical = 5.dp)
        ) {
          Icon(
            imageVector = Icons.Rounded.Star,
            contentDescription = "Stars",
            tint = GoldStar,
            modifier = Modifier
              .size(18.dp)
              .scale(scale)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "$starsCount",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SavannaOnPrimaryContainer
          )
        }

        // Child Profile Avatar
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(SafariSecondary)
            .clickable { onProfileClick() },
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = "Child Profile",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
