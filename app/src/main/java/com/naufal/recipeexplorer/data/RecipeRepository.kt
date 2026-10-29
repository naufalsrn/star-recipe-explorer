package com.naufal.recipeexplorer.data

import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.data.remote.RecipeApi
import java.util.concurrent.ConcurrentHashMap

/**
 * The single place ViewModels get recipe data from. It hides where the data
 * comes from, so screens don't need to know about HTTP or JSON.
 *
 * Recipes loaded for the list are kept in memory, so opening a recipe's
 * details is instant and works even if the network has just dropped.
 */
class RecipeRepository(private val api: RecipeApi) {

    private val cache = ConcurrentHashMap<Int, Recipe>()

    /** Returns recipes matching [query]. A blank query means "all recipes". */
    suspend fun getRecipes(query: String): List<Recipe> {
        val recipes = if (query.isBlank()) api.getRecipes() else api.searchRecipes(query)
        recipes.forEach { cache[it.id] = it }
        return recipes
    }

    /** Returns one recipe, from memory if the list already loaded it. */
    suspend fun getRecipe(id: Int): Recipe =
        cache[id] ?: api.getRecipe(id).also { cache[id] = it }
}
