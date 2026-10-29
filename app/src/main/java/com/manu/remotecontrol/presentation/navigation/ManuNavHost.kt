package com.manu.remotecontrol.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.manu.remotecontrol.presentation.adddevice.AddDeviceScreen
import com.manu.remotecontrol.presentation.editor.RemoteEditorScreen
import com.manu.remotecontrol.presentation.home.HomeScreen
import com.manu.remotecontrol.presentation.remote.RemoteControlScreen
import com.manu.remotecontrol.presentation.settings.SettingsScreen

/** Rutas de navegación de la app. */
object Routes {
    const val HOME = "home"
    const val ADD_DEVICE = "add_device"
    const val SETTINGS = "settings"
    const val REMOTE = "remote/{deviceId}"
    const val EDITOR = "editor/{deviceId}"

    fun remote(deviceId: Long) = "remote/$deviceId"
    fun editor(deviceId: Long) = "editor/$deviceId"
}

@Composable
fun ManuNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onAddDevice = { navController.navigate(Routes.ADD_DEVICE) },
                onOpenDevice = { deviceId -> navController.navigate(Routes.remote(deviceId)) },
                onEditButtons = { deviceId -> navController.navigate(Routes.editor(deviceId)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.ADD_DEVICE) {
            AddDeviceScreen(
                onBack = { navController.popBackStack() },
                onDeviceCreated = { deviceId ->
                    navController.navigate(Routes.remote(deviceId)) {
                        popUpTo(Routes.HOME)
                    }
                },
                onCustomCreated = { deviceId ->
                    navController.navigate(Routes.editor(deviceId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = Routes.REMOTE,
            arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
        ) {
            RemoteControlScreen(
                onBack = { navController.popBackStack() },
                onEditButtons = { deviceId -> navController.navigate(Routes.editor(deviceId)) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.EDITOR,
            arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
        ) {
            RemoteEditorScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
