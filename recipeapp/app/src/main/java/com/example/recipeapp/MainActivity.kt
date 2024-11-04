package com.example.recipeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.rememberImagePainter
import com.example.recipeapp.network.Recipe
import com.example.recipeapp.network.RecipeRepository
import com.example.recipeapp.network.SpoonacularApi
import com.example.recipeapp.viewmodel.RecipeViewModel
import com.example.recipeapp.viewmodel.RecipeViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up Repository and ViewModel with factory
        val apiService = SpoonacularApi.retrofitService
        val repository = RecipeRepository(apiService)
        val viewModelFactory = RecipeViewModelFactory(repository)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "search") {
                    composable("search") {
                        RecipeSearchScreen(navController = navController, viewModel = viewModel(factory = viewModelFactory))
                    }
                    composable("detail/{recipeId}") { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("recipeId")
                        recipeId?.let { id ->
                            RecipeDetailScreen(viewModel = viewModel(factory = viewModelFactory), recipeId = id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeSearchScreen(navController: NavHostController, viewModel: RecipeViewModel) {
    val recipes by viewModel.recipes.collectAsState()
    var query by remember { mutableStateOf("chicken") }
    var cuisine by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var maxCalories by remember { mutableStateOf(1000) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Recipe") }
        )

        FiltersSection(
            cuisine = cuisine,
            onCuisineChange = { cuisine = it },
            diet = diet,
            onDietChange = { diet = it },
            maxCalories = maxCalories,
            onMaxCaloriesChange = { maxCalories = it }
        )

        Button(onClick = { viewModel.searchRecipes(query, cuisine, diet, maxCalories, reset = true) }) {
            Text("Search")
        }

        LazyColumn {
            itemsIndexed(recipes) { index, recipe ->
                RecipeListItem(recipe = recipe) {
                    navController.navigate("detail/${recipe.id}")
                }
            }
        }
    }
}

@Composable
fun RecipeListItem(recipe: Recipe, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberImagePainter(recipe.image),
            contentDescription = null,
            modifier = Modifier.size(64.dp).padding(end = 8.dp)
        )
        Column {
            Text(recipe.title, style = MaterialTheme.typography.titleMedium) // updated from h6
            Text("Click for more details", style = MaterialTheme.typography.bodySmall) // updated from body2
        }
    }
}

@Composable
fun RecipeDetailScreen(viewModel: RecipeViewModel, recipeId: String) {
    var recipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(recipeId) {
        viewModel.getRecipeById(recipeId) { fetchedRecipe ->
            recipe = fetchedRecipe
        }
    }

    if (recipe == null) {
        Text("Loading recipe details...")
    } else {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(recipe!!.title, style = MaterialTheme.typography.headlineMedium) // updated from h4
            Image(
                painter = rememberImagePainter(recipe!!.image),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("Ingredients", style = MaterialTheme.typography.titleMedium) // updated from h6
            recipe!!.ingredients?.forEach { ingredient ->
                Text("- ${ingredient.amount} ${ingredient.unit} ${ingredient.name}", style = MaterialTheme.typography.bodyLarge) // updated from body1
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Instructions", style = MaterialTheme.typography.titleMedium) // updated from h6
            Text(recipe!!.instructions ?: "No instructions available", style = MaterialTheme.typography.bodyLarge) // updated from body1
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersSection(
    cuisine: String,
    onCuisineChange: (String) -> Unit,
    diet: String,
    onDietChange: (String) -> Unit,
    maxCalories: Int,
    onMaxCaloriesChange: (Int) -> Unit
) {
    val cuisineOptions = listOf(
        "African", "American", "British", "Cajun", "Caribbean", "Chinese",
        "Eastern European", "European", "French", "German", "Greek", "Indian",
        "Irish", "Italian", "Japanese", "Jewish", "Korean", "Latin American",
        "Mediterranean", "Mexican", "Middle Eastern", "Nordic", "Southern",
        "Spanish", "Thai", "Vietnamese"
    )

    val dietOptions = listOf(
        "Gluten Free", "Ketogenic", "Vegetarian", "Lacto-Vegetarian",
        "Ovo-Vegetarian", "Vegan", "Pescetarian", "Paleo", "Primal",
        "Low FODMAP", "Whole30"
    )

    var expandedCuisine by remember { mutableStateOf(false) }
    var expandedDiet by remember { mutableStateOf(false) }

    Column {
        // Cuisine Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedCuisine,
            onExpandedChange = { expandedCuisine = !expandedCuisine }
        ) {
            OutlinedTextField(
                value = cuisine,
                onValueChange = { onCuisineChange(it) },
                label = { Text("Cuisine") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor() // Required for dropdown alignment
                    .clickable { expandedCuisine = true } // Manually handle clicks
            )
            DropdownMenu(
                expanded = expandedCuisine,
                onDismissRequest = { expandedCuisine = false }
            ) {
                cuisineOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onCuisineChange(option)
                            expandedCuisine = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diet Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedDiet,
            onExpandedChange = { expandedDiet = !expandedDiet }
        ) {
            OutlinedTextField(
                value = diet,
                onValueChange = { onDietChange(it) },
                label = { Text("Diet") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
                    .clickable { expandedDiet = true }
            )
            DropdownMenu(
                expanded = expandedDiet,
                onDismissRequest = { expandedDiet = false }
            ) {
                dietOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onDietChange(option)
                            expandedDiet = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Max Calories Slider
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Max Calories: ")
            Slider(
                value = maxCalories.toFloat(),
                onValueChange = { onMaxCaloriesChange(it.toInt()) },
                valueRange = 0f..2000f,
                steps = 4
            )
        }
    }
}