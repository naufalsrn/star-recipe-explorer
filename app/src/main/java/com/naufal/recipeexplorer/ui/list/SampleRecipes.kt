package com.naufal.recipeexplorer.ui.list

import com.naufal.recipeexplorer.data.model.Recipe

/** Hardcoded the data from api. */
val sampleRecipes = listOf(
    sample(1, "Classic Margherita Pizza", "Italian", "Easy", 4.6, 98, 20, 15, "Dinner"),
    sample(2, "Vegetarian Stir-Fry", "Asian", "Medium", 4.7, 26, 15, 20, "Lunch"),
    sample(3, "Chocolate Chip Cookies", "American", "Easy", 4.9, 13, 15, 10, "Snack", "Dessert"),
    sample(4, "Chicken Alfredo Pasta", "Italian", "Medium", 4.9, 82, 15, 20, "Lunch", "Dinner"),
    sample(5, "Mango Salsa Chicken", "Mexican", "Easy", 4.9, 63, 15, 25, "Dinner"),
    sample(6, "Quinoa Salad with Avocado", "Mediterranean", "Easy", 4.4, 59, 20, 15, "Lunch", "Side Dish"),
    sample(7, "Tomato Basil Bruschetta", "Italian", "Easy", 4.7, 95, 15, 10, "Appetizer"),
    sample(8, "Beef and Broccoli Stir-Fry", "Asian", "Medium", 4.7, 58, 20, 15, "Dinner"),
    sample(9, "Caprese Salad", "Italian", "Easy", 4.6, 82, 10, 0, "Lunch"),
    sample(10, "Shrimp Scampi Pasta", "Italian", "Medium", 4.3, 5, 15, 20, "Dinner"),
)

private fun sample(
    id: Int,
    name: String,
    cuisine: String,
    difficulty: String,
    rating: Double,
    reviewCount: Int,
    prepTimeMinutes: Int,
    cookTimeMinutes: Int,
    vararg mealType: String,
) = Recipe(
    id = id,
    name = name,
    image = "https://cdn.dummyjson.com/recipe-images/$id.webp",
    ingredients = emptyList(),
    instructions = emptyList(),
    prepTimeMinutes = prepTimeMinutes,
    cookTimeMinutes = cookTimeMinutes,
    servings = 4,
    difficulty = difficulty,
    cuisine = cuisine,
    caloriesPerServing = 0,
    tags = emptyList(),
    mealType = mealType.toList(),
    rating = rating,
    reviewCount = reviewCount,
)
