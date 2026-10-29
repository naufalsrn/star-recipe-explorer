package com.naufal.recipeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.naufal.recipeexplorer.ui.RecipeExplorerApp
import com.naufal.recipeexplorer.ui.theme.RecipeExplorerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeExplorerTheme {
                RecipeExplorerApp()
            }
        }
    }
}
