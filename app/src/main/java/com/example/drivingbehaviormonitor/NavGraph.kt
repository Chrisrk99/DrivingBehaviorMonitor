package com.example.drivingbehaviormonitor

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.drivingbehaviormonitor.screens.HomeScreen
import com.example.drivingbehaviormonitor.screens.AccelerationScreen
import com.example.drivingbehaviormonitor.screens.SpeedScreen
import com.example.drivingbehaviormonitor.screens.CorneringScreen
import com.example.drivingbehaviormonitor.screens.LaneChangeScreen
import com.example.drivingbehaviormonitor.screens.WearablesScreen
import com.example.drivingbehaviormonitor.screens.EnvironmentScreen



//  This is our app’s navigation map!
// It tells the app what screen to show when we navigate to a certain route (like "home" or "speed").
// Each screen is linked here with a route name we use in navController.navigate("routeName").
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home" //  this is the first screen the user sees
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("acceleration") {
            AccelerationScreen(navController = navController)
        }
        composable("speed") {
            SpeedScreen(navController = navController)
        }

        // We can add more route/screen links here later for other behavior categories.
        // e.g., composable("cornering") { CorneringScreen(navController) }
        composable("cornering") {
            CorneringScreen(navController = navController)
        }

        composable("lanechange") {
            LaneChangeScreen(navController = navController)
        }

        composable("wearable") {
            WearablesScreen(navController = navController)
        }

        composable("environment") {
            EnvironmentScreen(navController = navController)
        }

    }
}
