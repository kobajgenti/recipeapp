package com.example.greetingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.greetingapp.ui.theme.GreetingappTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GreetingApp()
        }
    }
}

@Composable
fun GreetingApp() {
    // State to hold the user's name input
    var name by remember { mutableStateOf("") }
    // State to hold the generated greeting message
    var greeting by remember { mutableStateOf("") }

    // Function to determine the greeting based on current time
    fun getTimeBasedGreeting(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            in 18..21 -> "Good evening"
            else -> "Good night"
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center, // Center vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        // TextField for user to input their name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Spacer for spacing

        // Button to trigger the greeting
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    greeting = "${getTimeBasedGreeting()}, $name!"
                } else {
                    greeting = "Hello!"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Greet Me")
        }

        Spacer(modifier = Modifier.height(24.dp)) // Spacer for spacing

        // Display the greeting message
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingAppPreview() {
    GreetingApp()
}