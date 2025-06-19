package com.jonathon.nebulink.presentation.navigation

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Home : Screen("home")
    object Game : Screen("game/{puzzleId}") {
        fun createRoute(puzzleId: String) = "game/$puzzleId"
    }
    object ThemeStore : Screen("theme_store")
    object Settings : Screen("settings")
    object Stats : Screen("stats")
    object LuxJournal : Screen("lux_journal")
}

sealed class BottomNavScreen(val route: String, val title: String) {
    object Daily : BottomNavScreen("daily", "Daily")
    object Discover : BottomNavScreen("discover", "Discover")
    object Practice : BottomNavScreen("practice", "Practice")
    object Profile : BottomNavScreen("profile", "Profile")
}
