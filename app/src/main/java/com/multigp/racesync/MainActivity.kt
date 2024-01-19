package com.multigp.racesync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
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
import com.multigp.racesync.composables.CustomTextField
import com.multigp.racesync.composables.PasswordTextField
import com.multigp.racesync.ui.theme.RaceSyncTheme
import com.multigp.racesync.ui.theme.multiGPRed

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaceSyncTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginView(modifier = Modifier)
                }
            }
        }
    }
}

@Composable
fun LoginView(modifier: Modifier = Modifier) {
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
            LoginForm(modifier)
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
fun LoginForm(modifier: Modifier = Modifier) {
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
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = R.string.login_email_placeholder,
            icon = Icons.Default.Email,
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email
        )
        PasswordTextField(
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = R.string.login_password_placeholder,
            icon = Icons.Default.Lock,
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        )
        Text(
            modifier = modifier
                .clickable {}
                .padding(top = 16.dp),
            color = multiGPRed,
            style = MaterialTheme.typography.titleSmall,
            text = stringResource(R.string.login_forgot_password)
        )
        Text(
            modifier = modifier
                .clickable {}
                .padding(top = 16.dp),
            color = multiGPRed,
            style = MaterialTheme.typography.titleSmall,
            text = stringResource(R.string.login_create_account)
        )
        OutlinedButton(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            onClick = {}
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
fun LoginViewPreview() {
    RaceSyncTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            LoginView()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    RaceSyncTheme {
        LoginForm()
    }
}

@Preview(showBackground = true)
@Composable
fun FooterPreview() {
    RaceSyncTheme {
        Footer()
    }
}

