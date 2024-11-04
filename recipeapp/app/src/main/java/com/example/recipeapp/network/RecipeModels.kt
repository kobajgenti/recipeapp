package com.example.recipeapp.network

import com.squareup.moshi.Json

data class RecipeResponse(
    @Json(name = "results") val recipes: List<Recipe>
)

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val instructions: String?,
    val ingredients: List<Ingredient>?
)

data class Ingredient(
    val name: String,
    val amount: Double,
    val unit: String
)