package com.example.namma_platform.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_platform.model.Train
import com.example.namma_platform.ui.TrainViewModel
import com.example.namma_platform.ui.components.CoachLayout
import com.example.namma_platform.ui.theme.AccentYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TrainViewModel,
    onTrainClick: (String) -> Unit
) {
    val stations by viewModel.stations.collectAsState()
    val selectedStation by viewModel.selectedStation.collectAsState()
    val trains by viewModel.trains.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSpeakingTrainId by viewModel.isSpeaking.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Namma Platform", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("ನಮ್ಮ ಪ್ಲಾಟ್‌ಫಾರ್ಮ್", fontSize = 14.sp)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header with Time and Station Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn, 
                                contentDescription = null, 
                                tint = AccentYellow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Current Station",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = currentTime,
                            fontWeight = FontWeight.Bold,
                            color = AccentYellow,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { expanded = true },
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = selectedStation?.let { "${it.name} (${it.nameKn})" } ?: "Select Station",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            stations.forEach { station ->
                                DropdownMenuItem(
                                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                                    text = { Text("${station.name} (${station.nameKn})") },
                                    onClick = {
                                        viewModel.selectStation(station)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Upcoming Trains / ಮುಂದಿನ ರೈಲುಗಳು",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (trains.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Warning, 
                                contentDescription = null, 
                                modifier = Modifier.size(48.dp), 
                                tint = Color.Gray
                            )
                            Text("No trains scheduled", color = Color.Gray)
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
                    ) {
                        items(trains) { train ->
                            TrainCard(
                                train = train,
                                isSpeaking = isSpeakingTrainId == train.id,
                                onSpeak = { viewModel.speakAnnouncement(train) },
                                onClick = { onTrainClick(train.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrainCard(train: Train, isSpeaking: Boolean, onSpeak: () -> Unit, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "speaking")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow, // Train icon would be better but PlayArrow is standard
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${train.number} | ${train.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = train.nameKn,
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 26.dp)
                    )
                }
                
                Surface(
                    color = AccentYellow,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PF", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(train.platform, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Notifications, 
                    contentDescription = null, 
                    modifier = Modifier.size(16.dp), 
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Arrival: ${train.arrivalTime}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Coach Sequence / ಬೋಗಿ ವಿನ್ಯಾಸ:",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            CoachLayout(coachSequence = train.coachSequence)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSpeak,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSpeaking) AccentYellow else MaterialTheme.colorScheme.primary,
                    contentColor = if (isSpeaking) Color.Black else Color.White
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSpeaking) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .graphicsLayer(alpha = alpha)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Announcing... / ಘೋಷಿಸಲಾಗುತ್ತಿದೆ...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow, 
                            contentDescription = null, 
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Speak Announcement / ಘೋಷಣೆ ಕೇಳಿ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
