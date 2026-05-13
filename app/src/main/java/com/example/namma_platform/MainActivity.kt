package com.example.namma_platform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.namma_platform.ui.TrainViewModel
import com.example.namma_platform.ui.screens.*
import com.example.namma_platform.ui.theme.NammaPlatformTheme

/**
 * Main Activity for Namma Platform app.
 * Sets up the high-level navigation and theme.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NammaPlatformTheme {
                val viewModel: TrainViewModel = viewModel()
                MainNavigation(viewModel)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Splash : Screen("splash", "Splash")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object TrainDetails : Screen("train_details/{trainId}", "Details") {
        fun createRoute(trainId: String) = "train_details/$trainId"
    }
}

@Composable
fun MainNavigation(viewModel: TrainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(Screen.Home, Screen.Settings)
    val showBottomBar = currentDestination?.route in bottomBarScreens.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onTimeout = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onTrainClick = { trainId ->
                        navController.navigate(Screen.TrainDetails.createRoute(trainId))
                    }
                )
            }
            composable(
                route = Screen.TrainDetails.route,
                arguments = listOf(navArgument("trainId") { type = NavType.StringType })
            ) { backStackEntry ->
                val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
                TrainDetailsScreen(
                    trainId = trainId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
