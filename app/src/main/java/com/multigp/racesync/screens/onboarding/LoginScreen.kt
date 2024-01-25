package com.multigp.racesync.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multigp.racesync.R
import com.multigp.racesync.composables.CustomTextField
import com.multigp.racesync.composables.PasswordTextField
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.ui.theme.multiGPRed
import com.multigp.racesync.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onClickRegisterAccount: () -> Unit = {},
    onClickRecoverPassword: () -> Unit = {},
    onClickLogin: () -> Unit = {}
) {
    val loginUiState by viewModel.uiState.collectAsState()
    Surface(
        modifier = modifier.padding(all = 16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = modifier.weight(1f))
            Image(
                painter = painterResource(id = R.drawable.racesync_logo),
                contentDescription = "RaceSync Logo"
            )
            Spacer(modifier = modifier.weight(1f))
            LoginForm(
                loginUiState.email,
                loginUiState.password,
                onEmailChanged = { viewModel.onEmailChanged(it) },
                onPasswordChanged = { viewModel.onPasswordChanged(it) },
                modifier = modifier,
                onClickLogin = onClickLogin,
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
}


@Composable
fun LoginForm(
    email: String,
    password: String,
    modifier: Modifier = Modifier,
    onEmailChanged: (String) -> Unit = {},
    onPasswordChanged: (String) -> Unit = {},
    onClickLogin: () -> Unit = {},
    onClickRegisterAccount: () -> Unit = {},
    onClickRecoverPassword: () -> Unit = {}
) {
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
            imeAction = ImeAction.Done,
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
                color = multiGPRed,
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
                color = multiGPRed,
                style = MaterialTheme.typography.titleSmall
            )
        }
        OutlinedButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = onClickLogin
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = stringResource(R.string.login_btn_login)
            )
        }
    }
}


@Composable
fun Footer(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.surface
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
            LoginScreen()
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

