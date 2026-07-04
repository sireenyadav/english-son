package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.CompEnglishViewModel

@Composable
fun LoginScreen(
    viewModel: CompEnglishViewModel,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }
    val error by viewModel.loginError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ThemeBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = ThemeTextDark
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your 4-digit student code to continue.",
            style = MaterialTheme.typography.bodyLarge,
            color = ThemeTextGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = code,
            onValueChange = {
                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    code = it
                }
            },
            label = { Text("Student Code") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("login_code_input"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple40,
                unfocusedBorderColor = ThemeBorder
            )
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.loginWithCode(code) },
            enabled = code.length == 4,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("login_button"),
            shape = RoundedCornerShape(100.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ThemeDarkPurple)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = Color.White
            )
        }
    }
}
