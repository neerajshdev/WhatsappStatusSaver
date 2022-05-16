package com.nibodev.composeapp

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nibodev.composeapp.ui.screens.MainScreen
import com.nibodev.composeapp.ui.screens.Route
import com.nibodev.composeapp.ui.theme.ComposeAppTheme
import com.nibodev.layer.data.RepoImpl
import com.nibodev.layer.data.RepoImplUntilSdk28


fun d(msg: String) {
    if (BuildConfig.DEBUG) Log.d("Console", msg)
}

class MainActivity : ComponentActivity() {

    lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionManager.initWith(this)

        model = ViewModelProvider(this)[MainViewModel::class.java]
        model.repo = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) RepoImpl().with(this)
            .downloadLoc(Constants.downloadPath)
            .baseUri(Constants.whatsappDocumentUri) else RepoImplUntilSdk28(Constants.whatsappPath)


        setContent {
            val navController = rememberNavController()
            ComposeAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = Route.MainScreen
                ) {
                    composable(Route.MainScreen) { MainScreen(navController) }
                }
            }
        }
    }
}




