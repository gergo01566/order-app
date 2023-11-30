package com.example.onlab.screens.login

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlab.R
import com.example.onlab.screen.login.LoginUiState
import com.example.onlab.viewModels.LoginViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginScreen(
    navigateFromTo: (String, String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
){
    val showLoginFrom = rememberSaveable { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(false) }
    val passwordReset = remember { mutableStateOf(false) }

    val uiState by viewModel.uiState

    Scaffold(
        content = { it ->
            it.calculateBottomPadding()
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Image(painter = painterResource(id = R.drawable.ic_launcher_round), contentDescription = "Logo", modifier = Modifier
                    .size(200.dp)
                    .padding(10.dp))
                if (showLoginFrom.value)
                    UserForm(
                        uiState = uiState,
                        onEmailChange = { viewModel.onEmailChange(it) },
                        onPasswordChange = {viewModel.onPasswordChange(it)} ){ email, password ->
                        viewModel.onSignInClick(navigateFromTo,
                            onFailure = {
                                showDialog.value = true
                            },
                            onComplete = {

                            })
                    }
                else
                    UserForm(
                        isCreateAccount = true,
                        uiState = uiState,
                        onEmailChange = {viewModel.onEmailChange(it)},
                        onPasswordChange = {viewModel.onPasswordChange(it)}){ email, password ->
                        viewModel.onSignUpClick(navigateFromTo,
                            onFailure = {
                                showDialog.value = true
                            },
                            onComplete = {
                            })
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
            onDismissRequest = { passwordReset.value = false },
            onCompleteRequest = {
                viewModel.onResetPassword(
                    email = it,
                    onFailure = {
                        passwordReset.value = false
                    },
                    onComplete = {
                        passwordReset.value = false
                    }
                )
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetDialog(
    showDialog: Boolean,
    onCompleteRequest: (String) -> Unit,
    onDismissRequest: () -> Unit
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
                        onCompleteRequest(email)
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
                    Text("MégRsem")
                }
            })
    }
}


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
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onDone: (String, String) -> Unit = { email, pwd -> }
) {
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequest = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = validateInputs(uiState.email, uiState.password)

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
        NewEmailInputField(
            email = uiState.email,
            enabled = !loading,
            onValueChange = { onEmailChange(it)},
            onAction = KeyboardActions {
                passwordFocusRequest.requestFocus()
            })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequest),
            password = uiState.password,
            labelID = "Jelszó",
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                if(!valid) return@KeyboardActions
                onDone(uiState.email.trim(), uiState.password.trim())
            },
            onPasswordChange = { onPasswordChange(it)},
        )
        SubmitButton(
            textId = if (isCreateAccount) "Regisztráció" else "Bejelentkezés",
            loading = loading,
            validInputs = valid
        ){
            onDone(uiState.email.trim(), uiState.password.trim())
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
    password: String,
    labelID: String,
    enabled: Boolean,
    onPasswordChange: (String) -> Unit,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onAction: KeyboardActions,) {
    val visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()
    OutlinedTextField(
        value = password,
        onValueChange = { onPasswordChange(it)},
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
fun NewEmailInputField(
    modifier: Modifier = Modifier,
    email: String,
    labelId: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
    onValueChange: (String) -> Unit,
){
    OutlinedTextField(
        value = email,
        onValueChange = { onValueChange(it)},
        label = { Text(text = labelId)},
        singleLine = true,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        modifier = modifier
            .padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            .fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = imeAction),
        keyboardActions = onAction
    )
}

fun validateInputs(email: String, password: String): Boolean {
    return email.isNotEmpty() && password.isNotEmpty() && password.length >= 6 && email.contains('@')
}
