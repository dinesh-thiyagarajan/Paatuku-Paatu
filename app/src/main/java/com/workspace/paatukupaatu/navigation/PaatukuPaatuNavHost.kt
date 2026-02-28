package com.workspace.paatukupaatu.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.workspace.feature.about.AboutScreen
import com.workspace.feature.favorites.FavoritesScreen
import com.workspace.feature.home.HomeScreen
import com.workspace.feature.nowplaying.NowPlayingScreen

@Composable
fun PaatukuPaatuNavHost(
    navController: NavHostController,
    adsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route,
        modifier = modifier
    ) {
        composable(Route.Home.route) {
            HomeScreen(
                onNavigateToNowPlaying = {
                    navController.navigate(Route.NowPlaying.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Favorites.route) {
            FavoritesScreen(
                onNavigateToNowPlaying = {
                    navController.navigate(Route.NowPlaying.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.About.route) {
            AboutScreen()
        }

        composable(Route.NowPlaying.route) {
            NowPlayingScreen(
                onBack = { navController.popBackStack() },
                adsEnabled = adsEnabled
            )
        }
    }
}
