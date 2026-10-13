package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SaffronPrimaryLight

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 48.dp,
    barWidth: Dp = 6.dp,
    barSpacing: Dp = 4.dp
) {
    val barGradient = Brush.verticalGradient(
        colors = listOf(
            GoldAccent,
            SaffronPrimaryLight,
            SaffronPrimary
        )
    )

    val idleColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        amplitudes.forEachIndexed { index, amp ->
            val targetHeight = if (isSpeaking) {
                (maxHeight * amp).coerceIn(8.dp, maxHeight)
            } else {
                8.dp
            }

            val animatedHeight by animateFloatAsState(
                targetValue = targetHeight.value,
                animationSpec = tween(durationMillis = 80),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .then(
                        if (isSpeaking) {
                            Modifier.background(barGradient)
                        } else {
                            Modifier.background(idleColor)
                        }
                    )
            )

            if (index < amplitudes.lastIndex) {
                Box(modifier = Modifier.width(barSpacing))
            }
        }
    }
}
