package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SquareCategoryButton(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    completedCount: Int,
    totalCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.surface != Color(0xFFFFFFFF)

    val cardBg by animateColorAsState(
        targetValue = if (isDark) Color(0x14FFFFFF) else Color(0xFAF9F6),
        animationSpec = tween(durationMillis = 400),
        label = "cardBg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isDark) accentColor.copy(alpha = 0.4f) else accentColor.copy(alpha = 0.62f),
        animationSpec = tween(durationMillis = 400),
        label = "borderColor"
    )

    val textAndIconColor = if (isDark) accentColor else accentColor.copy(red = accentColor.red * 0.85f, green = accentColor.green * 0.85f, blue = accentColor.blue * 0.85f)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(22.dp))
            .background(cardBg)
            .border(2.dp, borderColor, RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag("square_button_${title.lowercase().replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top portion: completed count indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(accentColor.copy(alpha = 0.22f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    val isAllCompleted = completedCount == totalCount && totalCount > 0
                    Text(
                        text = if (totalCount == 0) "0/0" else "$completedCount/$totalCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isAllCompleted) Color(0xFF10B981) else textAndIconColor,
                        maxLines = 1,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Middle: beautifully styled larger icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.25f), accentColor.copy(alpha = 0.08f))
                        )
                    )
                    .border(1.5.dp, accentColor.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = textAndIconColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom: Beautiful uppercase title
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
