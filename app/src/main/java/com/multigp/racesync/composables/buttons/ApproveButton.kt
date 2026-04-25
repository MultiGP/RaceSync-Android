package com.multigp.racesync.composables.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.multigp.racesync.domain.model.RaceApprovalState
import com.multigp.racesync.ui.theme.JoinButtonClosedGray
import com.multigp.racesync.ui.theme.JoinButtonGreen
import com.multigp.racesync.ui.theme.RaceCellTitleColor

/**
 * Trailing pill on a series-races row visible to series owners. Tapping toggles
 * approval state. Disabled (showing "Completed") when the race is finalised.
 */
@Composable
fun ApproveButton(
    state: RaceApprovalState,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val enabled = state != RaceApprovalState.Completed && !isLoading
    val container = containerColor(state)
    val content = contentColor(state)

    OutlinedButton(
        onClick = { if (enabled) onClick() },
        enabled = enabled,
        modifier = modifier.defaultMinSize(minWidth = 96.dp, minHeight = 32.dp),
        shape = RoundedCornerShape(6.dp),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = if (isLoading) container else JoinButtonClosedGray,
            disabledContentColor = if (isLoading) content else RaceCellTitleColor
        ),
        border = BorderStroke(1.dp, color = borderColor(state))
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = content
            )
        } else {
            Text(
                text = label(state),
                fontSize = 14.sp,
                fontWeight = if (state == RaceApprovalState.Approved) FontWeight.Normal else FontWeight.Bold
            )
        }
    }
}

private fun containerColor(state: RaceApprovalState): Color = when (state) {
    RaceApprovalState.Approved -> JoinButtonGreen
    RaceApprovalState.NotApproved -> Color.White
    RaceApprovalState.Completed -> JoinButtonClosedGray
}

private fun contentColor(state: RaceApprovalState): Color = when (state) {
    RaceApprovalState.Approved -> Color.White
    RaceApprovalState.NotApproved -> JoinButtonGreen
    RaceApprovalState.Completed -> RaceCellTitleColor
}

private fun borderColor(state: RaceApprovalState): Color = when (state) {
    RaceApprovalState.Completed -> JoinButtonClosedGray
    else -> JoinButtonGreen
}

private fun label(state: RaceApprovalState): String = when (state) {
    RaceApprovalState.Approved -> "Approved"
    RaceApprovalState.NotApproved -> "Approve"
    RaceApprovalState.Completed -> "Completed"
}
