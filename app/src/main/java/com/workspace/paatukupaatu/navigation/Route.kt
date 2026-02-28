package com.workspace.paatukupaatu.navigation

sealed class Route(val route: String) {
    object Home : Route("home")
    object Favorites : Route("favorites")
    object About : Route("about")
    object NowPlaying : Route("now_playing")
}
