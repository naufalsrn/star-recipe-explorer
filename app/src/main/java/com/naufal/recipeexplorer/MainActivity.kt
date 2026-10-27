package com.naufal.recipeexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.naufal.recipeexplorer.ui.list.RecipeListScreen
import com.naufal.recipeexplorer.ui.theme.RecipeExplorerTheme
import com.naufal.recipeexplorer.ui.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecipeExplorerTheme {
                var showRecipes by rememberSaveable { mutableStateOf(false) }

                if (showRecipes) {
                    BackHandler { showRecipes = false }
                    RecipeListScreen()
                } else {
                    WelcomeScreen(onEnter = { showRecipes = true })
                }
            }
        }
    }
}
