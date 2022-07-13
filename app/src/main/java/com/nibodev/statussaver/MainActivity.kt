package com.nibodev.statussaver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.lifecycle.ViewModelProvider
import com.nibodev.statussaver.navigation.LocalNavController
import com.nibodev.statussaver.navigation.Navigator
import com.nibodev.statussaver.ui.screen.LoadingPage
import com.nibodev.statussaver.ui.theme.WhatsappStatusSaverTheme


class MainActivity : ComponentActivity() {
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setContent {
            WhatsappStatusSaverTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = LocalNavController.current
                    Navigator(
                        navController = navController,
                        content = {
                           LoadingPage()
                        }
                    )
                }
            }
        }
    }
}
