package com.example.shoplistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shoplistapp.ui.theme.ShopListAppTheme
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign

data class ShoppingItem(
    val name: String,
    val quantity: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopListAppTheme {
                val pendingItems = remember { mutableStateListOf<ShoppingItem>() }
                val doneItems = remember { mutableStateListOf<ShoppingItem>() }
                ShoppingListScreen(pendingItems, doneItems)
            }
        }
    }
}

@Composable
fun ShoppingListScreen(
    pendingItems: MutableList<ShoppingItem>,
    doneItems: MutableList<ShoppingItem>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Margins around the content
    ) {
        // Title at the top
        Text(
            text = "Shopping List",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top =  40.dp, bottom = 16.dp),
            textAlign = TextAlign.Left // Centered the title
        )

        // Input fields and Add button
        var newItemName by remember { mutableStateOf("") }
        var newItemQuantity by remember { mutableStateOf("") }

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newItemName,
                onValueChange = { newItemName = it },
                label = { Text("Item Name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            TextField(
                value = newItemQuantity,
                onValueChange = { newItemQuantity = it },
                label = { Text("Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )
            Button(onClick = {
                if (newItemName.isNotBlank() && newItemQuantity.isNotBlank()) {
                    pendingItems.add(ShoppingItem(newItemName, newItemQuantity))
                    newItemName = ""
                    newItemQuantity = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pending items list
        Text(text = "Pending Items", style = MaterialTheme.typography.titleMedium)
        if (pendingItems.isEmpty()) {
            // Display message when pending items list is empty
            Text(
                text = "No pending items.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
            items(pendingItems) { item ->
                ShoppingListItem(
                    item = item,
                    isChecked = false,
                    onCheckedChange = { checked ->
                        if (checked) {
                            pendingItems.remove(item)
                            doneItems.add(item)
                        }
                    }
                )
            }
        }
            }

        Spacer(modifier = Modifier.height(16.dp))

        // Done items section without expand/collapse functionality
        Text(text = "Done Items", style = MaterialTheme.typography.titleMedium)
        if (doneItems.isEmpty()) {
            // Display message when pending items list is empty
            Text(
                text = "No Done items.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn {
                items(doneItems) { item ->
                    ShoppingListItem(
                        item = item,
                        isChecked = true,
                        onCheckedChange = { checked ->
                            if (!checked) {
                                doneItems.remove(item)
                                pendingItems.add(item)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = "${item.name} - ${item.quantity}",
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ShopListAppTheme {
        val samplePendingItems = remember {
            mutableStateListOf(
                ShoppingItem("Apples", "2 kg"),
                ShoppingItem("Bread", "1 loaf"),
                ShoppingItem("Milk", "2 liters")
            )
        }
        val sampleDoneItems = remember {
            mutableStateListOf(
                ShoppingItem("Eggs", "12"),
                ShoppingItem("Butter", "200g")
            )
        }
        ShoppingListScreen(samplePendingItems, sampleDoneItems)
    }
}