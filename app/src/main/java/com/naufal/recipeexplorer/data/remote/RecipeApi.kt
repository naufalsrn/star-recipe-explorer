package com.naufal.recipeexplorer.data.remote

import com.naufal.recipeexplorer.data.model.Recipe
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Calls the dummyjson recipe endpoints and turns the JSON responses into
 * [Recipe] objects using the built-in org.json parser.
 */
class RecipeApi(private val baseUrl: String = "https://dummyjson.com") {

    /** GET /recipes. `limit=0` asks the API for every recipe in one response. */
    suspend fun getRecipes(): List<Recipe> =
        parseRecipes(HttpClient.getText("$baseUrl/recipes?limit=0"))

    /** GET /recipes/search?q=.. */
    suspend fun searchRecipes(query: String): List<Recipe> {
        val q = URLEncoder.encode(query, "UTF-8")
        return parseRecipes(HttpClient.getText("$baseUrl/recipes/search?q=$q&limit=0"))
    }

    private fun parseRecipes(body: String): List<Recipe> {
        val array = JSONObject(body).getJSONArray("recipes")
        return List(array.length()) { i -> array.getJSONObject(i).toRecipe() }
    }

    // "opt" getters return a default instead of crashing if a field is missing.
    private fun JSONObject.toRecipe() = Recipe(
        id = getInt("id"),
        name = optString("name"),
        image = optString("image"),
        ingredients = optJSONArray("ingredients").toStringList(),
        instructions = optJSONArray("instructions").toStringList(),
        prepTimeMinutes = optInt("prepTimeMinutes"),
        cookTimeMinutes = optInt("cookTimeMinutes"),
        servings = optInt("servings"),
        difficulty = optString("difficulty"),
        cuisine = optString("cuisine"),
        caloriesPerServing = optInt("caloriesPerServing"),
        tags = optJSONArray("tags").toStringList(),
        mealType = optJSONArray("mealType").toStringList(),
        rating = optDouble("rating", 0.0),
        reviewCount = optInt("reviewCount"),
    )

    private fun JSONArray?.toStringList(): List<String> =
        if (this == null) emptyList() else List(length()) { i -> getString(i) }
}
