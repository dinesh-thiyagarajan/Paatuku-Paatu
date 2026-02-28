package com.workspace.paatukupaatu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.workspace.core.player.PlayerServiceConnection
import com.workspace.core.player.PlayerState
import com.workspace.paatukupaatu.config.AdConfig
import com.workspace.paatukupaatu.navigation.PaatukuPaatuNavHost
import com.workspace.paatukupaatu.navigation.Route
import com.workspace.paatukupaatu.ui.composables.BottomNavigationBar
import com.workspace.paatukupaatu.ui.composables.MiniPlayer
import com.workspace.paatukupaatu.ui.theme.PaatukuPaatuTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val playerServiceConnection: PlayerServiceConnection by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerServiceConnection.bind()

        setContent {
            @OptIn(ExperimentalMaterial3Api::class)
            PaatukuPaatuTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val controller by playerServiceConnection.playerController.collectAsState()
                val playerState = controller?.playerState?.collectAsState()?.value
                    ?: PlayerState()

                val showBottomBar = currentRoute != Route.NowPlaying.route
                val showMiniPlayer =
                    playerState.currentAudio != null && currentRoute != Route.NowPlaying.route

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        PaatukuPaatuNavHost(
                            navController = navController,
                            adsEnabled = AdConfig.adsEnabled
                        )

                        AnimatedVisibility(
                            visible = showMiniPlayer,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            enter = slideInVertically { it },
                            exit = slideOutVertically { it }
                        ) {
                            MiniPlayer(
                                playerState = playerState,
                                onPlayPause = {
                                    if (playerState.isPlaying) {
                                        controller?.pause()
                                    } else {
                                        controller?.resume()
                                    }
                                },
                                onNext = { controller?.next() },
                                onClick = {
                                    navController.navigate(Route.NowPlaying.route) {
                                        launchSingleTop = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            // Audio file opened from external app - will be handled by the player
            // once audio list is loaded and user can select from home
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerServiceConnection.unbind()
    }
}
