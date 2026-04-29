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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardActions
import androidx.hilt.navigation.compose.hiltViewModel
import com.multigp.racesync.BuildConfig
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
    val sessionExpired by loginViewModel.sessionExpired.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sessionExpiredMessage = stringResource(R.string.session_expired_message)

    LaunchedEffect(sessionExpired) {
        if (sessionExpired) {
            snackbarHostState.showSnackbar(
                message = sessionExpiredMessage,
                duration = SnackbarDuration.Long
            )
            loginViewModel.consumeSessionExpired()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.mipmap.launch_bkgd_foreground),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.25f
        )
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LoginScreenContent(
                uiState = formUiState,
                onEmailChanged = loginViewModel::onEmailChanged,
                onPasswordChanged = loginViewModel::onPasswordChanged,
                onClickLogin = loginViewModel::onLogin,
                onClickRegisterAccount = onClickRegisterAccount,
                onClickRecoverPassword = onClickRecoverPassword,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            when (loginUiState) {
                is LoginUiState.Loading -> ProgressHUD(text = loginUiState.messageId)
                is LoginUiState.Error -> {
                    LaunchedEffect(loginUiState) {
                        snackbarHostState.showSnackbar(
                            message = loginUiState.message,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun LoginScreenContent(
    uiState: LoginFormUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickRegisterAccount: () -> Unit,
    onClickRecoverPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .imePadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        Image(
            painter = painterResource(id = R.drawable.racesync_logo_splash),
            contentDescription = stringResource(R.string.app_name)
        )
        Spacer(Modifier.height(40.dp))
        LoginForm(
            email = uiState.email,
            password = uiState.password,
            isValidForm = uiState.isValidForm,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onClickLogin = onClickLogin,
            onClickRegisterAccount = onClickRegisterAccount,
            onClickRecoverPassword = onClickRecoverPassword
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.login_term_of_use),
            textAlign = TextAlign.Start
        )
        Spacer(Modifier.height(48.dp))
        Footer()
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LoginForm(
    email: String,
    password: String,
    isValidForm: Boolean,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onClickLogin: () -> Unit,
    onClickRegisterAccount: () -> Unit,
    onClickRecoverPassword: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val shakeState = remember { ShakingState() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordFocusRequester = remember { FocusRequester() }

    val submit = {
        if (isValidForm) {
            keyboardController?.hide()
            focusManager.clearFocus()
            onClickLogin()
        } else {
            scope.launch { shakeState.shake(animationDuration = 40) }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.bodyMedium
        )
        CustomTextField(
            text = email,
            placeholder = R.string.login_email_placeholder,
            icon = Icons.Default.Email,
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            ),
            onTextChanged = onEmailChanged
        )
        PasswordTextField(
            password = password,
            placeholder = R.string.login_password_placeholder,
            icon = Icons.Default.Lock,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(onDone = { submit() }),
            onPasswordChanged = onPasswordChanged
        )
        TextButton(
            onClick = onClickRecoverPassword,
            modifier = Modifier.padding(top = 8.dp),
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
            modifier = Modifier.padding(top = 4.dp),
            contentPadding = PaddingValues(start = 0.dp)
        ) {
            Text(
                text = stringResource(R.string.login_create_account),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Button(
            modifier = Modifier
                .shakable(shakeState)
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = { submit() },
            shape = RoundedCornerShape(7.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = stringResource(R.string.login_btn_login),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun Footer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(R.string.footer_powered_by),
            fontSize = 8.sp
        )
        Spacer(Modifier.height(10.dp))
        Image(
            modifier = Modifier.width(80.dp),
            painter = painterResource(id = R.drawable.logo_powered_by),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "v${BuildConfig.VERSION_NAME}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    RaceSyncTheme {
        LoginScreen(LoginUiState.None)
    }
}

@Preview(showBackground = true)
@Composable
private fun FooterPreview() {
    RaceSyncTheme {
        Footer()
    }
}
