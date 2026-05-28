package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.RoutineViewModel

@Composable
fun MusicSettingsDialog(
    onDismiss: () -> Unit,
    viewModel: RoutineViewModel
) {
    val context = LocalContext.current
    val songs by viewModel.songsList.collectAsStateWithLifecycle()
    val playingIndex by viewModel.currentSongIndex.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()

    var showPremiumNotice by remember { mutableStateOf(false) }
    var importingSlotIndex by remember { mutableStateOf(-1) }

    // File launcher for selecting local audio files (MP3/WAV/etc)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && importingSlotIndex != -1) {
            val contentResolver = context.contentResolver
            var resolvedName = "Imported Song #${importingSlotIndex + 1}"
            try {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            resolvedName = cursor.getString(nameIndex)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Remove audio file extension if present for clean look
            val cleanTitle = resolvedName.substringBeforeLast(".")
            viewModel.updateSongInSlot(importingSlotIndex, cleanTitle, uri.toString())
        }
        importingSlotIndex = -1 // reset state
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF12131A)), // Dark slate custom
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 580.dp)
                .border(1.dp, Color(0xFF222533), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stay Raxo Audio Hub",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Track Slots & Custom MP3 Uploads",
                            fontSize = 11.sp,
                            color = Color.LightGray.copy(alpha = 0.6f)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1D2030), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFF14B8A6),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "You can unlock up to 5 custom songs. Preloaded Ambient tracks are ready for test!",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "SONG SLOTS (5 UNLOCKED / 5 PREMIUM LOCKED)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(songs) { index, song ->
                        val isCurrent = index == playingIndex && !song.isLocked && !song.isEmptySlot
                        val slotBgColor = when {
                            song.isLocked -> Color(0xFF181A25).copy(alpha = 0.4f)
                            isCurrent -> Color(0xFF14B8A6).copy(alpha = 0.12f)
                            else -> Color(0xFF181A25)
                        }
                        val borderColor = when {
                            song.isLocked -> Color(0xFF1C1E2B)
                            isCurrent -> Color(0xFF14B8A6)
                            else -> Color(0xFF222533)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(slotBgColor, RoundedCornerShape(12.dp))
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (song.isLocked) {
                                        showPremiumNotice = true
                                    } else if (song.isEmptySlot) {
                                        importingSlotIndex = index
                                        launcher.launch("audio/*")
                                    } else {
                                        viewModel.selectSong(index)
                                        if (!isPlaying) {
                                            viewModel.togglePlayPause()
                                        }
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (song.isLocked) Color(0xFF2E1922) else Color(0xFF222533),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (song.isLocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked Slot",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else if (song.isEmptySlot) {
                                    Icon(
                                        imageVector = Icons.Default.UploadFile,
                                        contentDescription = "Upload MP3",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (isCurrent && isPlaying) Icons.Default.VolumeUp else Icons.Default.MusicNote,
                                        contentDescription = "Track Slot",
                                        tint = Color(0xFF14B8A6),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = song.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (song.isLocked) Color.LightGray.copy(alpha = 0.4f) else Color.White
                                )
                                Text(
                                    text = if (song.isEmptySlot) "Click to Upload Custom MP3" else song.artist,
                                    fontSize = 11.sp,
                                    color = if (song.isLocked) Color.LightGray.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.7f)
                                )
                            }

                            if (!song.isLocked && !song.isEmptySlot) {
                                if (isCurrent && isPlaying) {
                                    Text(
                                        text = "Playing",
                                        color = Color(0xFF14B8A6),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                } else {
                                    IconButton(
                                        onClick = {
                                            viewModel.selectSong(index)
                                            if (!isPlaying) viewModel.togglePlayPause()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Play",
                                            tint = Color.LightGray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (showPremiumNotice) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = " Slots 6 to 10 are premium-locked! Complete your habit streaks of 30, 69, or 100 days to unlock premium options!",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
