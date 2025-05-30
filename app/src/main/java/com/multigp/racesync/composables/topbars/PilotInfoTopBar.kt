package com.multigp.racesync.composables.topbars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.multigp.racesync.R
import com.multigp.racesync.composables.text.TextWithFlag
import com.multigp.racesync.ui.theme.RaceSyncTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilotInfoTopBar(
    title: String,
    countryCode:String,
    modifier: Modifier = Modifier,
    isLoggedInUser: Boolean = false,
    onGoBack: () -> Unit = {},
    onClickShare: () -> Unit = {},
    onClickShowQRCode: () -> Unit = {}
) {
    TopAppBar(
        title = {
            TextWithFlag(text = title, countryCode = countryCode)
        },
        navigationIcon = {
            IconButton(onClick = { onGoBack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Go Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        actions = {
            if(isLoggedInUser) {
                IconButton(onClick = { onClickShowQRCode() }) {
                    Icon(
                        imageVector = Icons.Outlined.QrCode,
                        contentDescription = "QR Code",
                        tint = Color.Black,
                    )
                }
            }
//            IconButton(onClick = { onClickShare() }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.ic_share),
//                    contentDescription = "Share Pilot"
//                )
//            }
        }
    )
}


@Preview(showBackground = true, heightDp = 48)
@Composable
fun PilotInfoTopBarPreview(){
    RaceSyncTheme {
        PilotInfoTopBar(title = "Farooq Zaman", countryCode = "PK")
    }
}