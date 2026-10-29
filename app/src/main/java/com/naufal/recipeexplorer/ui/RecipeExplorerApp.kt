package com.naufal.recipeexplorer.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.naufal.recipeexplorer.ui.detail.RecipeDetailScreen
import com.naufal.recipeexplorer.ui.detail.RecipeDetailViewModel.Companion.RECIPE_ID_ARG
import com.naufal.recipeexplorer.ui.list.RecipeListScreen
import com.naufal.recipeexplorer.ui.welcome.WelcomeScreen

/** The app's screens and how to move between them. */
@Composable
fun RecipeExplorerApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(onEnter = { navController.navigate("recipes") })
        }
        composable("recipes") {
            RecipeListScreen(onRecipeClick = { id -> navController.navigate("recipes/$id") })
        }
        composable(
            route = "recipes/{$RECIPE_ID_ARG}",
            arguments = listOf(navArgument(RECIPE_ID_ARG) { type = NavType.IntType }),
        ) {
            RecipeDetailScreen(onBack = { navController.navigateUp() })
        }
    }
}
