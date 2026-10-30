package com.naufal.recipeexplorer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = BrandGreen,
    secondary = BrandOrange,
    background = Cream,
    surface = Cream,
)

@Composable
fun RecipeExplorerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
