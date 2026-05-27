package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.surface != Color(0xFFFFFFFF)
    
    val cardBg by animateColorAsState(
        targetValue = if (isDark) Color(0x0DFFFFFF) else Color(0xFFFFFFFF),
        animationSpec = tween(durationMillis = 600),
        label = "cardBg"
    )

    val borderColor1 by animateColorAsState(
        targetValue = if (isDark) Color(0x38FFFFFF) else Color(0xFFE5E7EB),
        animationSpec = tween(durationMillis = 600),
        label = "border1"
    )

    val borderColor2 by animateColorAsState(
        targetValue = if (isDark) Color(0x0AFFFFFF) else Color(0xFFE5E7EB),
        animationSpec = tween(durationMillis = 600),
        label = "border2"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .border(
                BorderStroke(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(borderColor1, borderColor2)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
