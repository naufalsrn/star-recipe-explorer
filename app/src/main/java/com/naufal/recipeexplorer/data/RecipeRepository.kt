package com.naufal.recipeexplorer.data

import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.data.model.RecipePage
import com.naufal.recipeexplorer.data.remote.RecipeApi
import java.util.concurrent.ConcurrentHashMap

class RecipeRepository(private val api: RecipeApi) {

    private val cache = ConcurrentHashMap<Int, Recipe>()

    suspend fun getRecipes(query: String, limit: Int, skip: Int): RecipePage {
        val page = if (query.isBlank()) api.getRecipes(limit, skip) else api.searchRecipes(query, limit, skip)
        page.recipes.forEach { cache[it.id] = it }
        return page
    }

    suspend fun getRecipe(id: Int): Recipe =
        cache[id] ?: api.getRecipe(id).also { cache[id] = it }
}
