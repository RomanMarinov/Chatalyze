package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen.model.ChatalyzeBottomNavItem
import com.dev_marinov.chatalyze.util.ScreenRoute

@Composable
fun ChatalyzeScreen(
     viewModel: ChatalyzeScreenViewModel = hiltViewModel()
) {
    //val backStackEntry = navController.currentBackStackEntryAsState()
    viewModel.saveHideNavigationBar(false)

    val isHideBottomBar by viewModel.isHideBottomBar.collectAsStateWithLifecycle(false)

  //  viewModel.onMovieClickedHideNavigationBar(false)
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        bottomBar = {
            ChatalyzeBottomNavigationBar(
                modifier = Modifier
                   // .animateContentSize(animationSpec = tween(durationMillis = 800))
//                    .height(height = if (isHideBottomBar.value == true) 0.dp else 70.dp),
                    .height(height = if (isHideBottomBar == true) 0.dp else 70.dp),
                items = listOf(
                    ChatalyzeBottomNavItem(
                        name = "Chat",
                        route = ScreenRoute.ChatsScreen.route,
                        icon = Icons.Default.Chat,
                        badgeCount = 2
                    ),
                    ChatalyzeBottomNavItem(
                        name = "Call",
                        route = ScreenRoute.CallScreen.route,
                        icon = Icons.Default.Call,
                        badgeCount = 4
                    ),
                    ChatalyzeBottomNavItem(
                        name = "Setting",
                        route = ScreenRoute.SettingScreen.route,
                        icon = Icons.Default.Settings,
//                                badgeCount =
                    ),
                ),
                navController = navController,
                onItemClick = {
                    navController.navigate(it.route)
                }
            )
        }

    ) { paddingValues ->
        // передаем падинг чтобы список BottomNavigationBar не накладывался по поверх списка
        Box(modifier = Modifier.padding(paddingValues = paddingValues)) {
            ChatalyzeNavigationGraph(navHostController = navController, viewModel = viewModel)
        }
    }
}




