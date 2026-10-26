package com.naufal.recipeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.naufal.recipeexplorer.ui.theme.RecipeExplorerTheme
import com.naufal.recipeexplorer.ui.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeExplorerTheme {
                // TODO: open the recipe list once it exists
                WelcomeScreen(onEnter = {})
            }
        }
    }
}
