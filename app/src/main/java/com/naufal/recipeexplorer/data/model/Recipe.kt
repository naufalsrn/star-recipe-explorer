package com.naufal.recipeexplorer.data.model

data class Recipe(
    val id: Int,
    val name: String,
    val image: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String,
    val cuisine: String,
    val caloriesPerServing: Int,
    val mealType: List<String>,
    val rating: Double,
    val reviewCount: Int,
) {
    val totalTimeMinutes: Int get() = prepTimeMinutes + cookTimeMinutes
}
