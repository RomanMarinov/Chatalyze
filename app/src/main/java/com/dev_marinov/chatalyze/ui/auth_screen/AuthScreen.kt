package com.dev_marinov.chatalyze.ui.auth_screen

import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintLogin
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintPassword
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AuthScreen(
    navController: NavHostController,
    viewModel: AuthScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SystemBars(false)
    SystemUiControllerHelper.StatusBarColor()
    GradientBackgroundHelper.GradientBackground()



    var textLoginState by remember { mutableStateOf("") }
    var textPasswordState by remember { mutableStateOf("") }
    val messageLogin = stringResource(id = R.string.login_warning)
    val messagePassword = stringResource(id = R.string.password_warning)
    val messageLoginPassword = stringResource(id = R.string.login_password_warning)
    val context = LocalContext.current



//    val systemUiController = rememberSystemUiController()
//    //     systemUiController.isStatusBarVisible = false // Скрыть строку состояния (status bar)
//    systemUiController.isNavigationBarVisible = false // Скрыть панель навигации (navigation bar)
    //       systemUiController.isSystemBarsVisible = false // Скрыть и строку состояния, и панель навигации
    //  systemUiController.navigationBarDarkContentEnabled = false // Установить темный цвет иконок на панели навигации


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintLogin(
                value = textLoginState,
                onValueChanged = { textLoginState = it },
                hintText = "login",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 8.dp, end = 16.dp),
                icon = Icons.Rounded.Person
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            TextFieldHintPassword(
                value = textPasswordState,
                onValueChanged = { textPasswordState = it },
                hintText = "password",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(20))
                    .background(MaterialTheme.colors.surface)
                    .padding(start = 8.dp, end = 8.dp),
                icon = Icons.Rounded.Lock
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {

                    },
                color = Color.White,
                fontSize = 14.sp,
                text = "Forgot password"
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .width(250.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    checkLengthAndSendAuthRequest(
                        textLoginState = textLoginState,
                        textPasswordState = textPasswordState,
                        context = context,
                        messageLogin = messageLogin,
                        messagePassword = messagePassword,
                        messageLoginPassword = messageLoginPassword,
                        viewModel = viewModel
                    )
                },
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.main_violet)),
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.auth_bt_sign_in),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .width(250.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {
                    navController.navigate("sign_up_screen")
                },
                shape = RoundedCornerShape(100),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
            ) {
                Text(
                    modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                    text = stringResource(id = R.string.auth_bt_sign_up),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

fun checkLengthAndSendAuthRequest(
    textLoginState: String,
    textPasswordState: String,
    context: Context,
    messageLogin: String,
    messagePassword: String,
    messageLoginPassword: String,
    viewModel: AuthScreenViewModel
) {

    if ((textLoginState.length in 1..4) && (textPasswordState.length in 1..4)) {
        ShowToastHelper.createToast(
            message = messageLoginPassword,
            context = context
        )
    } else if (textLoginState.isEmpty() && textPasswordState.isEmpty()) {
        ShowToastHelper.createToast(
            message = messageLoginPassword,
            context = context
        )
    } else if (textLoginState.isEmpty() || (textLoginState.length in 1..4)) {
        ShowToastHelper.createToast(
            message = messageLogin,
            context = context
        )
    } else if (textPasswordState.isEmpty() || (textPasswordState.length in 1..4)) {
        ShowToastHelper.createToast(
            message = messagePassword,
            context = context
        )
    } else {
        ShowToastHelper.createToast(
            message = "выполняем запрос на вход",
            context = context
        )
        viewModel.getToken(
            login = textLoginState,
            password = textPasswordState
        )
    }
}