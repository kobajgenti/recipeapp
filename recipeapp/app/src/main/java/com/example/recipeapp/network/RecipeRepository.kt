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

    // New function to get recipe details by ID
    suspend fun getRecipeById(id: String): Recipe? {
        return try {
            apiService.getRecipeById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}