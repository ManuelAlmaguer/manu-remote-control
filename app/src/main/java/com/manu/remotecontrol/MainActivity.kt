package com.manu.remotecontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.manu.remotecontrol.presentation.navigation.ManuNavHost
import com.manu.remotecontrol.presentation.theme.ManuRemoteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = hiltViewModel()
            val settings by mainViewModel.settings.collectAsStateWithLifecycle()

            ManuRemoteTheme(
                themeMode = settings.themeMode,
                dynamicColor = settings.dynamicColor
            ) {
                ManuNavHost()
            }
        }
    }
}
