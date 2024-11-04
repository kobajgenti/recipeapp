package com.example.recipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.network.Recipe
import com.example.recipeapp.network.RecipeRepository
import com.example.recipeapp.network.RecipeResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(private val repository: RecipeRepository) : ViewModel() {
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> get() = _recipes

    private var currentOffset = 0
    private var isLoading = false

    fun searchRecipes(query: String, cuisine: String? = null, diet: String? = null, maxCalories: Int? = null, reset: Boolean = false) {
        if (isLoading) return

        if (reset) {
            currentOffset = 0
            _recipes.value = emptyList() // Reset to an empty list when performing a new search
        }

        viewModelScope.launch {
            isLoading = true
            repository.searchRecipes(query, cuisine, diet, maxCalories, currentOffset).collect { response ->
                _recipes.value = _recipes.value + response.recipes // Append new results to the existing list
                currentOffset += response.recipes.size // Increment the offset by the number of recipes fetched
            }
            isLoading = false
        }
    }

    // New function to get recipe details by ID
    fun getRecipeById(id: String, onResult: (Recipe?) -> Unit) {
        viewModelScope.launch {
            val recipe = repository.getRecipeById(id)
            onResult(recipe)
        }
    }
}