package com.multigp.racesync.domain.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

class ShakingState {

    val xPosition = Animatable(0f)

    suspend fun shake(animationDuration: Int = 50) {
        val shakeAnimationSpec: AnimationSpec<Float> = tween(animationDuration)
        shakeToLeftThenRight(shakeAnimationSpec)
    }

    private suspend fun shakeToLeftThenRight(shakeAnimationSpec: AnimationSpec<Float>) {
            xPosition.animateTo(-20f, shakeAnimationSpec)
            xPosition.animateTo(20f, shakeAnimationSpec)
            xPosition.animateTo(-10f, shakeAnimationSpec)
            xPosition.animateTo(10f, shakeAnimationSpec)
            xPosition.animateTo(-5f, shakeAnimationSpec)
            xPosition.animateTo(5f, shakeAnimationSpec)
    }
}

fun Modifier.shakable(
    state: ShakingState,
): Modifier {
    return graphicsLayer {
        translationX = state.xPosition.value
    }
}