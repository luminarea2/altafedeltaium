package com.example.altafedeltium

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.altafedeltium.ui.SupermarketApp
import com.example.altafedeltium.ui.theme.AltafedeltiumTheme
import com.example.altafedeltium.ui.viewmodel.AuthSessionStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AuthSessionStore.initialize(this)
        enableEdgeToEdge()
        setContent {
            AltafedeltiumTheme {
                SupermarketApp()
            }
        }
    }
}