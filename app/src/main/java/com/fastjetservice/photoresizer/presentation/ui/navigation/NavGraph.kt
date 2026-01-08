package com.fastjetservice.photoresizer.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fastjetservice.photoresizer.presentation.ui.screens.EditScreen
import com.fastjetservice.photoresizer.presentation.ui.screens.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, startDestination = Screens.HomeScreen.route,

        ) {
        composable(route = Screens.HomeScreen.route) { HomeScreen(navController) }

        composable(
            route = Screens.EditScreen.routeWithArgs("0:uri"),
            arguments = listOf(
                navArgument("0") {
                    type = NavType.StringType
                }
            )
        ) {
            val uri = it.arguments?.getString("0")
            EditScreen(navController, uri = uri?.toUri())
        }

    }
}