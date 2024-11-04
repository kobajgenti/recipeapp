package com.example.recipeapp.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class RecipeRepository(private val apiService: SpoonacularApiService) {

    fun searchRecipes(
        query: String,
        cuisine: String? = null,
        diet: String? = null,
        maxCalories: Int? = null,
        offset: Int = 0
    ): Flow<RecipeResponse> = flow {
        val response = apiService.searchRecipes(query, cuisine, diet, maxCalories)
        emit(response)
    }.catch { e ->
        e.printStackTrace()
    }

    // Updated function to get recipe details by ID with better error handling
    suspend fun getRecipeById(id: String): Recipe? {
        return try {
            val response = apiService.getRecipeById(id)
            // Log the response to check if ingredients are present
            println("Recipe details response: $response")
            response
        } catch (e: Exception) {
            println("Error fetching recipe details: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}