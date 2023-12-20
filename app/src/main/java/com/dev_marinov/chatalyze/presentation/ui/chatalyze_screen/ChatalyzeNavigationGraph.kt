package com.dev_marinov.chatalyze.presentation.ui.chatalyze_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.dev_marinov.chatalyze.presentation.ui.call_screen.CallsScreen
import com.dev_marinov.chatalyze.presentation.ui.chat_screen.ChatScreen
import com.dev_marinov.chatalyze.presentation.ui.chats_screen.ChatsScreen
import com.dev_marinov.chatalyze.presentation.ui.profile_screen.ProfileScreen
import com.dev_marinov.chatalyze.presentation.util.SENDER_PHONE
import com.dev_marinov.chatalyze.presentation.util.ScreenRoute
import com.dev_marinov.chatalyze.presentation.util.RECIPIENT_NAME
import com.dev_marinov.chatalyze.presentation.util.RECIPIENT_PHONE

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatalyzeNavigationGraph(
    navHostController: NavHostController,
    authHostController: NavHostController,
    viewModel: ChatalyzeScreenViewModel
) {
    NavHost(navController = navHostController, startDestination = ScreenRoute.ChatsScreen.route) {
        composable(route = ScreenRoute.ChatsScreen.route) {
            ChatsScreen(navController = navHostController)
        }
        composable(route = ScreenRoute.CallScreen.route) {
            CallsScreen(navController = navHostController)
        }
        composable(route = ScreenRoute.ProfileScreen.route) {
            ProfileScreen(
                navHostController = navHostController,
                authHostController = authHostController
            )
        }

//        composable(route = ScreenRoute.ChatScreen.route) {
//            ChatScreen(
//                navHostController = navHostController
//            )
//        }

        ///////////////////////
        composable(
            route = ScreenRoute.ChatScreen.route,
            arguments = listOf(
                navArgument(RECIPIENT_NAME) {
                    type = NavType.StringType
                },
                navArgument(RECIPIENT_PHONE) {
                    type = NavType.StringType
                },
                navArgument(SENDER_PHONE) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            ChatScreen(
                navHostController = navHostController,
                recipientName = entry.arguments?.getString(RECIPIENT_NAME),
                recipientPhone = entry.arguments?.getString(RECIPIENT_PHONE),
                senderPhone = entry.arguments?.getString(SENDER_PHONE)
            )
        }
    }
}