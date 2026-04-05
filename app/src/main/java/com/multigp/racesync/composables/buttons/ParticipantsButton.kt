package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.R
import com.multigp.racesync.ui.theme.ParticipantBadgeBackground
import com.multigp.racesync.ui.theme.ParticipantBadgeContent
import com.multigp.racesync.ui.theme.RaceSyncTheme

@Composable
fun ParticipantsButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(start = 10.dp, end = 8.dp, top = 2.dp, bottom = 2.dp),
        modifier = modifier.defaultMinSize(minHeight = 24.dp, minWidth = 1.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ParticipantBadgeBackground,
            contentColor = ParticipantBadgeContent
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_group),
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParticipantsButtonPreview(){
    RaceSyncTheme {
        ParticipantsButton("10")
    }
}