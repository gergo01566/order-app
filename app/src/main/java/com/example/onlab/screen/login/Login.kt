package com.example.onlab.screen.login

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlab.R
import com.example.onlab.navigation.ProductScreens
import com.example.onlab.viewModels.LoginScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.currentCoroutineContext
import kotlin.math.log
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.onlab.navigation.AppNavigation
import com.example.onlab.screen.order.OrdersScreen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(navController: NavController, loginScreenViewModel: LoginScreenViewModel = viewModel()) {
    val showLoginFrom = rememberSaveable { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(false) }
    val passwordReset = remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            it.calculateBottomPadding()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                if (showLoginFrom.value)
                    UserForm(false, false) { email, pwd ->
                        loginScreenViewModel.signInWithEmailAndPassword(
                            email = email,
                            password = pwd,
                            orders = {
                                navController.navigate(route = "OrdersScreen")
                            },
                            onFailure = {
                                showDialog.value = true
                            }
                        )
                    }
                else {
                    UserForm(false, true) { email, pwd ->
                        loginScreenViewModel.createUserWithEmailAndPassword(
                            email = email,
                            password = pwd,
                            orders = {
                                navController.navigate(route = "OrdersScreen")
                            },
                            onFailure = {
                                showDialog.value = true
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val text = if (showLoginFrom.value) "Regisztráció" else "Bejelentkezés"
                    Text(text = if (showLoginFrom.value) "Új felhasználó?" else "Már van fiókod?")
                    Text(
                        text,
                        modifier = Modifier
                            .clickable {
                                showLoginFrom.value = !showLoginFrom.value
                            }
                            .padding(start = 5.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
                if (showLoginFrom.value) {
                    Text(
                        "Elfelejtett jelszó",
                        modifier = Modifier
                            .clickable {
                                passwordReset.value = true
                            }
                            .padding(start = 5.dp),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.secondaryVariant
                    )
                }
            }
        }
    )

    if (showDialog.value && showLoginFrom.value) {
        AlertDialogExample(title = "Hiba", onDismissRequest = { /*TODO*/ }) {
            showDialog.value = false
        }
    } else if (showDialog.value && !showLoginFrom.value) {
        AlertDialogExample(
            title = "Hiba",
            text = "Az e-mail cím vagy jelszó formátuma nem megfelelő vagy ezzel az e-maillel már regisztráltak!",
            onDismissRequest = { /*TODO*/ }) {
            showDialog.value = false
        }
    }

    if (passwordReset.value) {
        PasswordResetDialog(
            showDialog = passwordReset.value,
            loginScreenViewModel = loginScreenViewModel,
            onDismissRequest = { passwordReset.value = false },
            onFailure = {
                passwordReset.value = false
                scope.launch { scaffoldState.snackbarHostState.showSnackbar("Helytelen e-mail cím") }
            },
            onComplete = {
                passwordReset.value = false
                scope.launch { scaffoldState.snackbarHostState.showSnackbar("E-mailt küldtünk jelszavad helyreállításához") }
            }
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetDialog(
    showDialog: Boolean,
    loginScreenViewModel: LoginScreenViewModel,
    onDismissRequest: () -> Unit,
    onComplete: () -> Unit,
    onFailure: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissRequest,
            icon = {
                Icon(Icons.Filled.Info, contentDescription = "Example Icon")
            },
            title = {
                Text(text = "Elfelejtett jelszó")
            },
            text = {
                Column() {
                    Text(text = "Jelszavad helyreállításához add meg az e-mail címedet!", textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.padding(10.dp))
                    androidx.compose.material3.TextField(
                        value = email,
                        maxLines = 1,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        singleLine = true,
                    )
                }

            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        if (email.isNotEmpty()) {
                            loginScreenViewModel.resetPassword(email, onFailure = {onFailure()}){
                                onComplete()
                            }
                        }
                    }
                ) {
                    Text("Megerősítés")
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text("Mégsem")
                }
            })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogExample(
    text: String = "Helytelen felhasználónév vagy jelszó",
    title: String,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
        AlertDialog(
            title = {
                Text(text = title)
            },
            text = {
                Text(text = text)
            },
            onDismissRequest = {
                onDismissRequest()
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirmation()
                    }
                ) {
                    Text("Ok")
                }
            }
        )

}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = validateInputs(email.value, password.value)

    val modifier = Modifier
        .height(500.dp)
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (!isCreateAccount) "Bejelentkezés" else "Regisztráció",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (isCreateAccount) {
            Text(text = stringResource(id = R.string.help_text), modifier = Modifier.padding(14.dp))
        }
        EmailInput(
            emailState = email,
            enabled = !loading,
            onAction = KeyboardActions {
            passwordFocusRequest.requestFocus()
        })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            passwordState = password,
            labelID = "Jelszó",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if(!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
            }
        )
        SubmitButton(
            textId = if (isCreateAccount) "Regisztráció" else "Bejelentkezés",
            loading = loading,
            validInputs = valid
        ){
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
    }
}

@Composable
fun SubmitButton(
    textId: String,
    loading: Boolean,
    validInputs: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape
    ) {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp))
        else (Text (text = textId, modifier = Modifier.padding(5.dp)))
    }
}

@Composable
fun PasswordInput(
    modifier: Modifier,
    passwordState: MutableState<String>,
    labelID: String,
    enabled: Boolean,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions,) {
    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = {
        passwordState.value = it
    },
        label = { Text(text = labelID)},
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction),
    visualTransformation = visualTransformation,
    trailingIcon = {PasswordVisibility(passwordVisibility = passwordVisibility)},
    keyboardActions = onAction)
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value
    IconButton(onClick = {passwordVisibility.value = !visible}){
        Icons.Default.Close
    }
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default)
{
    InputField(
        modifier = modifier,
        valueState = emailState,
        labelId = labelId,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onAction = onAction
    )

}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelId: String,
    enabled: Boolean,
    isSingleLine:Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default
){
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it},
        label = { Text(text = labelId)},
        singleLine = isSingleLine,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction =imeAction),
        keyboardActions = onAction
    )
}

fun validateInputs(email: String, password: String): Boolean {
    return email.isNotEmpty() && password.isNotEmpty() && password.length >= 6 && email.contains('@')
}

