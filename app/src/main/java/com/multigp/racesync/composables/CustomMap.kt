package com.multigp.racesync.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CustomMap(
    location: LatLng,
    markerTitle: String,
    modifier: Modifier = Modifier,
    markerSnippet: String = "",
    onMapClick: ((LatLng)->Unit)? = null
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 10f)
    }

    GoogleMap(
        modifier = modifier.fillMaxWidth(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false),
        properties = MapProperties(mapType = MapType.NORMAL),
        onMapClick = onMapClick
    ) {
        Marker(
            state = MarkerState(position = location),
            title = markerTitle,
            snippet = markerSnippet
        )
    }
}