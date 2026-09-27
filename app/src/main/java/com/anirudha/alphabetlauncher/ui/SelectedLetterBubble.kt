package com.anirudha.alphabetlauncher.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anirudha.alphabetlauncher.ui.theme.AccentCyan
import com.anirudha.alphabetlauncher.ui.theme.AccentPink
import com.anirudha.alphabetlauncher.ui.theme.AccentViolet
import kotlin.math.roundToInt

/**
 * Floating Circular Selected Letter Bubble composable.
 * Appears during touch & drag interaction on the A-Z alphabet bar.
 */
@Composable
fun SelectedLetterBubble(
    letter: Char?,
    isVisible: Boolean,
    touchYPx: Float,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible && letter != null,
        enter = scaleIn(animationSpec = spring(stiffness = 800f)),
        exit = scaleOut(animationSpec = spring(stiffness = 800f)),
        modifier = modifier
    ) {
        if (letter != null) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = 0,
                            y = (touchYPx - 100).roundToInt().coerceAtLeast(40)
                        )
                    }
                    .size(76.dp)
                    .shadow(16.dp, CircleShape, ambientColor = AccentCyan, spotColor = AccentViolet)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF0284C7),
                                Color(0xFF4F46E5),
                                Color(0xFF1E1B4B)
                            )
                        )
                    )
                    .border(2.dp, Brush.horizontalGradient(listOf(AccentCyan, AccentPink)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
