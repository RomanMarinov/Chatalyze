package com.dev_marinov.chatalyze.presentation.ui.forgot_password_screen

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.dev_marinov.chatalyze.R
import com.dev_marinov.chatalyze.presentation.util.GradientBackgroundHelper
import com.dev_marinov.chatalyze.presentation.util.TextFieldHintEmail
import com.dev_marinov.chatalyze.util.ShowToastHelper
import com.dev_marinov.chatalyze.util.SystemUiControllerHelper

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: ForgotPasswordScreenViewModel = hiltViewModel()
) {
    SystemUiControllerHelper.SetSystemBars(false)
    SystemUiControllerHelper.SetStatusBarColor()
    GradientBackgroundHelper.SetGradientBackground()

    val softwareKeyboardController = LocalSoftwareKeyboardController.current

    var textEmailState by remember { mutableStateOf("") }
    val messageEmail = stringResource(id = R.string.email_warning)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
          //  .padding(top = 200.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    softwareKeyboardController?.hide()
                }
            ),
     //   verticalArrangement = Arrangement.Center,
      //  horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val constraintsTop = ConstraintSet {

            val img_back = createRefFor("img_back")
            val header = createRefFor("header")
            val email = createRefFor("email")
            val get_password = createRefFor("get_password")

            constrain(img_back) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(header) {
                //top.linkTo(email.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(email.top)
//                width = Dimension.value(40.dp)
//                height = Dimension.wrapContent
            }

            constrain(email) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }

            constrain(get_password) {
                top.linkTo(email.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
//                bottom.linkTo(parent.bottom)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            }


        }

        ConstraintLayout(
            constraintSet = constraintsTop,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, bottom = 8.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_back_to_prev_screen),
                contentDescription = "back",
                modifier = Modifier
                    .layoutId("img_back")
                    .padding(start = 8.dp, top = 16.dp)
                    .systemBarsPadding() // Добавить отступ от скрытого статус-бара
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        Log.d("4444", " clidk back")
                        navController.popBackStack("auth_screen", false)
                    }
            )

            Box(
                modifier = Modifier
                    .layoutId("header")
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier,
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    text = stringResource(id = R.string.header_forgot_screen)
                )
            }

            Box(
                modifier = Modifier
                    .layoutId("email")
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                TextFieldHintEmail(
                    value = textEmailState,
                    onValueChanged = { textEmailState = it },
                    hintText = stringResource(id = R.string.email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(20))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 12.dp, end = 16.dp),
                    icon = Icons.Rounded.Mail,
//                isFocus = {
//
//                }
                )
            }

            Box(
                modifier = Modifier
                    .imePadding()
                    .layoutId("get_password")
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                OutlinedButton(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .border(
                            border = BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(100.dp)
                        ),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    onClick = {
//                        checkLengthAndSendRegisterRequest(
//                            textEmailState = textEmailState,
//                            messageEmail = messageEmail,
//                            context = context,
//                            viewModel = viewModel
//                        )

                        ShowToastHelper.createToast(
                            message = "выполняем проверку",
                            context = context
                        )
                        viewModel.sendEmail(email = "marinov37@mail.ru")

                    }
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = R.string.get_password),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun checkLengthAndSendRegisterRequest(
    textEmailState: String,
    messageEmail: String,
    context: Context,
    viewModel: ForgotPasswordScreenViewModel
) {

    val isEmpty = textEmailState.isEmpty()
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(textEmailState).matches()
    val containsCom = textEmailState.contains(".com")
    val containsGmailCom = textEmailState.contains("@gmail.com")
    val containsRu = textEmailState.contains(".ru")
    val containsGmailRu = textEmailState.contains("gmail.ru")

//    Log.d(
//        "4444", " isEmpty" + isEmpty + " isEmailValid=" + isEmailValid
//                + " containsCom=" + containsCom + " containsRu=" + containsRu
//    )
//
//    if (textEmailState.length in 1..4) {
//        ShowToastHelper.createToast(
//            message = messageEmail,
//            context = context
//        )
//    } else if (textEmailState.isEmpty()) {
//        ShowToastHelper.createToast(
//            message = messageEmail,
//            context = context
//        )
//    } else if (isEmpty || ((isEmailValid && !containsCom) && (isEmailValid && !containsGmailCom) && (isEmailValid && !containsRu))
//        || ((!isEmailValid && !containsCom) && (!isEmailValid && !containsGmailCom) && (!isEmailValid && !containsRu))
//        || (containsGmailCom && containsRu) || (containsGmailRu)
//    ) {
//        ShowToastHelper.createToast(
//            message = messageEmail,
//            context = context
//        )
//    } else {
//        ShowToastHelper.createToast(
//            message = "выполняем проверку",
//            context = context
//        )
//        viewModel.sendEmail(email = textEmailState)
//    }
    ShowToastHelper.createToast(
        message = "выполняем проверку",
        context = context
    )
    viewModel.sendEmail(email = "marinov37@mail.ru")
}
