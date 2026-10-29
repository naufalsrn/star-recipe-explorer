package com.naufal.recipeexplorer.data

import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.data.remote.RecipeApi

/**
 * The single place ViewModels get recipe data from. It hides where the data
 * comes from, so screens don't need to know about HTTP or JSON.
 */
class RecipeRepository(private val api: RecipeApi) {

    /** Returns recipes matching [query]. A blank query means "all recipes". */
    suspend fun getRecipes(query: String): List<Recipe> =
        if (query.isBlank()) api.getRecipes() else api.searchRecipes(query)
}
