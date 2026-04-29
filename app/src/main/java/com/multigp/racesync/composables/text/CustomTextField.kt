package com.multigp.racesync.composables.text

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun CustomTextField(
    text: String,
    @StringRes placeholder: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    autoCorrectEnabled: Boolean = true,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onTextChanged: (String) -> Unit = {},
) {
    TextField(
        modifier = modifier,
        value = text,
        onValueChange = onTextChanged,
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        ),
        placeholder = { Text(stringResource(id = placeholder)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction,
            capitalization = capitalization,
            autoCorrectEnabled = autoCorrectEnabled
        ),
        keyboardActions = keyboardActions
    )
}

@Preview(showBackground = true, heightDp = 120)
@Composable
private fun CustomTextFieldPreview() {
    RaceSyncTheme {
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            CustomTextField(
                text = "farooq.zaman@me.com",
                placeholder = R.string.login_email_placeholder,
                icon = Icons.Default.Email
            )
        }
    }
}
