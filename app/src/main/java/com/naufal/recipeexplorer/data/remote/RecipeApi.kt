package com.naufal.recipeexplorer.data.remote

import com.naufal.recipeexplorer.data.model.Recipe
import com.naufal.recipeexplorer.data.model.RecipePage
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class RecipeApi(private val baseUrl: String = "https://dummyjson.com") {

    suspend fun getRecipes(limit: Int, skip: Int): RecipePage =
        parsePage(HttpClient.getText("$baseUrl/recipes?limit=$limit&skip=$skip"))

    suspend fun searchRecipes(query: String, limit: Int, skip: Int): RecipePage {
        val q = URLEncoder.encode(query, "UTF-8")
        return parsePage(HttpClient.getText("$baseUrl/recipes/search?q=$q&limit=$limit&skip=$skip"))
    }

    suspend fun getRecipe(id: Int): Recipe =
        JSONObject(HttpClient.getText("$baseUrl/recipes/$id")).toRecipe()

    private fun parsePage(body: String): RecipePage {
        val json = JSONObject(body)
        val array = json.getJSONArray("recipes")
        return RecipePage(
            recipes = List(array.length()) { i -> array.getJSONObject(i).toRecipe() },
            total = json.optInt("total"),
        )
    }

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
        mealType = optJSONArray("mealType").toStringList(),
        rating = optDouble("rating", 0.0),
        reviewCount = optInt("reviewCount"),
    )

    private fun JSONArray?.toStringList(): List<String> =
        if (this == null) emptyList() else List(length()) { i -> getString(i) }
}
