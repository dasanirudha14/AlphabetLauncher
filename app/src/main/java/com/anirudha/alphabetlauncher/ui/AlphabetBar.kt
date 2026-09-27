package com.anirudha.alphabetlauncher.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anirudha.alphabetlauncher.ui.theme.AccentCyan
import com.anirudha.alphabetlauncher.ui.theme.TextMuted
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt

private val ALPHABET = ('A'..'Z').toList()

/**
 * Vertical A-Z Alphabet Bar composable with curved fisheye arc animation,
 * pointer drag & tap gesture support, and smooth release animation.
 */
@Composable
fun AlphabetBar(
    selectedLetter: Char?,
    onLetterSelected: (Char) -> Unit,
    onTouchStateChanged: (isDragging: Boolean, touchYPx: Float, currentLetter: Char?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    var isTouching by remember { mutableStateOf(false) }
    var touchYPx by remember { mutableFloatStateOf(0f) }
    var barHeightPx by remember { mutableFloatStateOf(1f) }

    val radiusPx = remember(density) { with(density) { 130.dp.toPx() } }
    val maxArcOffsetPx = remember(density) { with(density) { 45.dp.toPx() } }

    fun getLetterAtY(y: Float): Char {
        val clampedY = y.coerceIn(0f, barHeightPx - 1f)
        val index = ((clampedY / barHeightPx) * ALPHABET.size).toInt().coerceIn(0, ALPHABET.size - 1)
        return ALPHABET[index]
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(42.dp)
            .padding(vertical = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x221E293B))
            .onGloballyPositioned { coordinates ->
                barHeightPx = coordinates.size.height.toFloat().coerceAtLeast(1f)
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        isTouching = true
                        touchYPx = offset.y
                        val letter = getLetterAtY(offset.y)
                        onLetterSelected(letter)
                        onTouchStateChanged(true, touchYPx, letter)
                        tryAwaitRelease()
                        isTouching = false
                        onTouchStateChanged(false, touchYPx, null)
                    },
                    onTap = { offset ->
                        val letter = getLetterAtY(offset.y)
                        onLetterSelected(letter)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isTouching = true
                        touchYPx = offset.y
                        val letter = getLetterAtY(offset.y)
                        onLetterSelected(letter)
                        onTouchStateChanged(true, touchYPx, letter)
                    },
                    onDragEnd = {
                        isTouching = false
                        onTouchStateChanged(false, touchYPx, null)
                    },
                    onDragCancel = {
                        isTouching = false
                        onTouchStateChanged(false, touchYPx, null)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchYPx = change.position.y
                        val letter = getLetterAtY(change.position.y)
                        onLetterSelected(letter)
                        onTouchStateChanged(true, touchYPx, letter)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ALPHABET.forEachIndexed { index, char ->
                // Calculate center Y coordinate for this specific letter
                val letterCenterY = (index + 0.5f) * (barHeightPx / ALPHABET.size)
                val dist = abs(letterCenterY - touchYPx)

                // Mathematical Fisheye Arc Transformation
                val (targetScale, targetOffsetX) = if (isTouching && dist < radiusPx) {
                    val normalizedDist = (dist / radiusPx).coerceIn(0f, 1f)
                    val curveFactor = cos(normalizedDist * (Math.PI / 2)).toFloat()

                    val scale = 1.0f + 1.1f * curveFactor
                    val offsetX = -maxArcOffsetPx * curveFactor
                    Pair(scale, offsetX)
                } else {
                    Pair(1.0f, 0f)
                }

                // Smooth Release & Transition Animation using Spring
                val animatedScale by animateFloatAsState(
                    targetValue = targetScale,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "LetterScale_$char"
                )

                val animatedOffsetX by animateFloatAsState(
                    targetValue = targetOffsetX,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "LetterOffset_$char"
                )

                val isSelected = selectedLetter == char

                Text(
                    text = char.toString(),
                    fontSize = if (isSelected) 14.sp else 11.sp,
                    fontWeight = if (isSelected || animatedScale > 1.2f) FontWeight.ExtraBold else FontWeight.Medium,
                    color = when {
                        isSelected -> AccentCyan
                        animatedScale > 1.2f -> Color.White
                        else -> TextMuted
                    },
                    modifier = Modifier
                        .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                        .scale(animatedScale)
                )
            }
        }
    }
}
