package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.RoutineViewModel

@Composable
fun FloatingMusicPlayerWidget(
    viewModel: RoutineViewModel,
    onOpenHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val songs by viewModel.songsList.collectAsStateWithLifecycle()
    val playingIndex by viewModel.currentSongIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val progress by viewModel.playbackProgress.collectAsStateWithLifecycle()
    val duration by viewModel.playbackDuration.collectAsStateWithLifecycle()

    val currentSong = remember(songs, playingIndex) {
        songs.getOrNull(playingIndex)
    }

    if (currentSong == null) return

    // Smooth spinning disc rotation animation that remembers previous angle when paused
    val rotationAngle = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            rotationAngle.animateTo(
                targetValue = rotationAngle.value + 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 6000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // Progress bar calculations
    val progressFraction = if (duration > 0) progress.toFloat() / duration else 0f

    // Time text formatter helper (e.g., 65s -> "1:05")
    val currentFormatted = remember(progress) {
        val min = progress / 60
        val sec = progress % 60
        String.format("%d:%02d", min, sec)
    }
    val durationFormatted = remember(duration) {
        val min = duration / 60
        val sec = duration % 60
        String.format("%d:%02d", min, sec)
    }

    // Glass-morphic widget container
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xBB0D0E15) // Frosted glass transparent dark
        ),
        modifier = modifier
            .fillMaxWidth(0.92f)
            .border(
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Spinning Vinyl disc illustration
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFF222533),
                                    Color(0xFF11121A),
                                    Color(0xFF030303),
                                    Color(0xFF11121A),
                                    Color(0xFF222533)
                                )
                            )
                        )
                        .rotate(rotationAngle.value)
                        .clickable { onOpenHub() },
                    contentAlignment = Alignment.Center
                ) {
                    // Vinyl grooves
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                    )
                    // Core dynamic graphic
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF14B8A6), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // 2. Song info & tracker text
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenHub() }
                ) {
                    Text(
                        text = currentSong.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = if (currentSong.isEmptySlot) "Slot Available for MP3" else currentSong.artist,
                        fontSize = 10.sp,
                        color = Color.LightGray.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }

                // 3. Control interface
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    IconButton(
                        onClick = { viewModel.previousSong() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Song",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play/Pause Toggler
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF14B8A6))
                            .clickable { viewModel.togglePlayPause() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Toggle Play Pause",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Next Button
                    IconButton(
                        onClick = { viewModel.nextSong() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Song",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Open Full Hub Button
                    IconButton(
                        onClick = onOpenHub,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueueMusic,
                            contentDescription = "Audio Hub",
                            tint = Color(0xFF14B8A6),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 4. Progress indicator & Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentFormatted,
                    fontSize = 9.sp,
                    color = Color.LightGray.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(1.5.dp)),
                    color = Color(0xFF14B8A6),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = durationFormatted,
                    fontSize = 9.sp,
                    color = Color.LightGray.copy(alpha = 0.6f)
                )
            }
        }
    }
}
