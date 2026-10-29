package com.naufal.recipeexplorer.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A small coloured label: green for Easy, orange for Medium, red for Hard. */
@Composable
fun DifficultyLabel(difficulty: String, modifier: Modifier = Modifier) {
    val color = when (difficulty.lowercase()) {
        "easy" -> Color(0xFF2E7D32)
        "medium" -> Color(0xFFF27A1A)
        else -> Color(0xFFC62828)
    }
    Surface(color = color, contentColor = Color.White, shape = RoundedCornerShape(50), modifier = modifier) {
        Text(
            text = difficulty,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}
