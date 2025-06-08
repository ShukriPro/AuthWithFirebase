package com.shukri.authwithfirebase

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
@Composable
fun SignUpScreen(navController: NavHostController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = email.isBlank()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min 6 chars)") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = password.length < 6
        )

        error?.let {
            Spacer(Modifier.height(4.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            if (email.isBlank() || password.length < 6) {
                error = "Enter valid email and password (min 6 chars)"
                return@Button
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                        Toast.makeText(context, "Signup success. Verify your email.", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    } else {
                        error = "Signup failed: ${it.exception?.localizedMessage ?: "Unknown error"}"
                    }
                }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign Up")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = {
            navController.navigate("login") {
                popUpTo("signup") { inclusive = true }
            }
        }) {
            Text("Already have an account? Login")
        }
    }
}
