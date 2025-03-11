package com.multigp.racesync.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.ProgressHUD
import com.multigp.racesync.composables.text.CustomTextField
import com.multigp.racesync.composables.text.PasswordTextField
import com.multigp.racesync.domain.model.ShakingState
import com.multigp.racesync.domain.model.shakable
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.viewmodels.LoginFormUiState
import com.multigp.racesync.viewmodels.LoginUiState
import com.multigp.racesync.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    onClickRegisterAccount: () -> Unit = {},
    onClickRecoverPassword: () -> Unit = {}
) {
    val formUiState by loginViewModel.formUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.launch_bkgd_foreground), // Replace with your image resource
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.25f
        )
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            containerColor = Color.Transparent // Makes Scaffold background transparent
        ) { paddingValues ->
            Box(modifier = modifier.padding(paddingValues = paddingValues)) {
                Surface(
                    modifier = modifier.padding(all = 16.dp),
                    color = Color.Transparent, // Semi-transparent background
                ) {
                    LoginScreenContent(
                        uiState = formUiState,
                        viewModel = loginViewModel,
                        modifier = modifier,
                        onClickRegisterAccount = onClickRegisterAccount,
                        onClickRecoverPassword = onClickRecoverPassword
                    )
                }

                when (loginUiState) {
                    is LoginUiState.Loading -> {
                        ProgressHUD(
                            modifier = modifier,
                            text = loginUiState.messageId
                        )
                    }
                    is LoginUiState.Error -> {
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(
                                message = loginUiState.message,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    uiState: LoginFormUiState,
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onClickRegisterAccount: () -> Unit = {},
    onClickRecoverPassword: () -> Unit = {}
) {
    val state = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.racesync_logo_splash),
            contentDescription = "RaceSync Logo"
        )
        Spacer(modifier = modifier.weight(1f))
        LoginForm(
            uiState.email,
            uiState.password,
            isValidForm = uiState.isValidForm,
            onEmailChanged = { viewModel.onEmailChanged(it) },
            onPasswordChanged = { viewModel.onPasswordChanged(it) },
            modifier = modifier,
            onClickLogin = { viewModel.onLogin() },
            onClickRegisterAccount = onClickRegisterAccount,
            onClickRecoverPassword = onClickRecoverPassword
        )
        Text(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.login_term_of_use),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = modifier.weight(1f))
        Footer()
    }
}

@Composable
fun LoginForm(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    isValidForm: Boolean = false,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onClickLogin: () -> Unit = {},
    onClickRegisterAccount: () -> Unit = {},
    onClickRecoverPassword: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val shakeState = ShakingState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.bodyMedium
        )
        CustomTextField(
            text = email,
            placeholder = R.string.login_email_placeholder,
            icon = Icons.Default.Email,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            onTextChanged = onEmailChanged
        )
        PasswordTextField(
            password = password,
            placeholder = R.string.login_password_placeholder,
            icon = Icons.Default.Lock,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password,
            onPasswordChanged = onPasswordChanged
        )
        TextButton(
            onClick = onClickRecoverPassword,
            modifier = modifier.padding(top = 8.dp),
            contentPadding = PaddingValues(start = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
        }
        TextButton(
            onClick = onClickRegisterAccount,
            modifier = modifier.padding(top = 4.dp),
            contentPadding = PaddingValues(start = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.login_create_account),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Button(
            modifier = Modifier.shakable(shakeState)
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                scope.launch {
                    if(isValidForm){
                        onClickLogin()
                    } else {
                        shakeState.shake(
                            animationDuration = 40
                        )
                    }
                }
            },
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.7f),
                contentColor = Color.Black,
                disabledContainerColor = Color.White.copy(alpha = 0.7f),
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text(
                text = stringResource(R.string.login_btn_login),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }
    }
}


@Composable
fun Footer(modifier: Modifier = Modifier) {
    Surface(
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = modifier,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium,
                text = stringResource(R.string.footer_powered_by),
                fontSize = 8.sp

            )
            Spacer(modifier = modifier.height(10.dp))
            Image(
                modifier = modifier.width(80.dp),
                painter = painterResource(id = R.drawable.logo_powered_by),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    RaceSyncTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            LoginScreen(LoginUiState.None)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    RaceSyncTheme {
        LoginForm("farooq.zaman@me.com", "qwer1234")
    }
}

@Preview(showBackground = true)
@Composable
fun FooterPreview() {
    RaceSyncTheme {
        Footer()
    }
}

