package com.example.recipeapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Base URL for Spoonacular API
private const val BASE_URL = "https://api.spoonacular.com/"

// Moshi builder to handle JSON parsing
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit instance
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

// Define the API service interface
interface SpoonacularApiService {
    // Existing search endpoint
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("cuisine") cuisine: String? = null,
        @Query("diet") diet: String? = null,
        @Query("maxCalories") maxCalories: Int? = null,
        @Query("offset") offset: Int = 0,
        @Query("number") number: Int = 10, // adjust number of results per page
        @Query("apiKey") apiKey: String = "d85d90ecd7534967a75ee31a9df51317"
    ): RecipeResponse

    // New endpoint for getting detailed recipe information
    @GET("recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") id: String,
        @Query("apiKey") apiKey: String = "d85d90ecd7534967a75ee31a9df51317"
    ): Recipe
}

// Singleton object to access the API service
object SpoonacularApi {
    val retrofitService: SpoonacularApiService by lazy {
        retrofit.create(SpoonacularApiService::class.java)
    }
}