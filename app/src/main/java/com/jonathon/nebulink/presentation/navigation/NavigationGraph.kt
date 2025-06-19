package com.jonathon.nebulink.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jonathon.nebulink.presentation.screens.daily.DailyScreen
import com.jonathon.nebulink.presentation.screens.discover.DiscoverScreen
import com.jonathon.nebulink.presentation.screens.game.GameScreen
import com.jonathon.nebulink.presentation.screens.home.HomeScreen
import com.jonathon.nebulink.presentation.screens.luxjournal.LuxJournalScreen
import com.jonathon.nebulink.presentation.screens.practice.PracticeScreen
import com.jonathon.nebulink.presentation.screens.profile.ProfileScreen
import com.jonathon.nebulink.presentation.screens.settings.SettingsScreen
import com.jonathon.nebulink.presentation.screens.stats.StatsScreen
import com.jonathon.nebulink.presentation.screens.themestore.ThemeStoreScreen
import com.jonathon.nebulink.presentation.screens.welcome.WelcomeScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String = Screen.Welcome.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToDaily = {
                    navController.navigate(BottomNavScreen.Daily.route)
                },
                onNavigateToGame = { puzzleId ->
                    navController.navigate(Screen.Game.createRoute(puzzleId))
                }
            )
        }

        composable(Screen.Game.route) { backStackEntry ->
            val puzzleId = backStackEntry.arguments?.getString("puzzleId")
            requireNotNull(puzzleId) { "puzzleId parameter wasn't found. Please make sure it's passed in the navigation." }
            GameScreen(
                puzzleId = puzzleId,
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.ThemeStore.route) {
            ThemeStoreScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(Screen.LuxJournal.route) {
            LuxJournalScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        // Bottom Navigation Screens
        composable(BottomNavScreen.Daily.route) {
            DailyScreen(
                onNavigateToGame = { puzzleId ->
                    navController.navigate(Screen.Game.createRoute(puzzleId))
                }
            )
        }

        composable(BottomNavScreen.Discover.route) {
            DiscoverScreen(
                onNavigateToGame = { puzzleId ->
                    navController.navigate(Screen.Game.createRoute(puzzleId))
                }
            )
        }

        composable(BottomNavScreen.Practice.route) {
            PracticeScreen(
                onNavigateToGame = { puzzleId ->
                    navController.navigate(Screen.Game.createRoute(puzzleId))
                }
            )
        }

        composable(BottomNavScreen.Profile.route) {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onNavigateToLuxJournal = {
                    navController.navigate(Screen.LuxJournal.route)
                }
            )
        }
    }
}
