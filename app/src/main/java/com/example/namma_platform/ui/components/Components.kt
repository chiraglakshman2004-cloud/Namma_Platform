package com.example.namma_platform.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.namma_platform.ui.theme.*

@Composable
fun CoachLayout(coachSequence: List<String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(coachSequence) { coach ->
            CoachItem(coach)
        }
    }
}

@Composable
fun CoachItem(name: String) {
    val backgroundColor = when {
        name.contains("ENGINE", ignoreCase = true) -> EngineColor
        name.contains("GENERAL", ignoreCase = true) -> GeneralCoachColor
        name.contains("LADIES", ignoreCase = true) -> LadiesCoachColor
        else -> StandardCoachColor
    }

    val isSpecial = name.contains("GENERAL", ignoreCase = true) || name.contains("LADIES", ignoreCase = true)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .shadow(if (isSpecial) 4.dp else 2.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(backgroundColor, backgroundColor.copy(alpha = 0.8f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                color = if (backgroundColor == AccentYellow) TextDark else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Connecting Coupler
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(4.dp)
                .background(Color.Gray)
        )

        // Wheels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.DarkGray))
            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color.DarkGray))
        }
        
        // Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.LightGray)
        )
    }
}
