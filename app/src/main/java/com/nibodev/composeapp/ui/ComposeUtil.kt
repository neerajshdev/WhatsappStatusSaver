package com.nibodev.composeapp.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*

/**
 * Observe the first item index and offset
 */
@Composable
fun LazyListState.Observe(
    firstVisibleItem: ((index: Int, offset: Int) -> Unit)? = null,
    deltaScroll: ((scrolled: Int) -> Unit)? = null
) {
    val index = firstVisibleItemIndex
    val offset = firstVisibleItemScrollOffset
    var prevIndex by remember { mutableStateOf(0) }
    var prevOffset by remember { mutableStateOf(0) }
    var prevHeight by remember { mutableStateOf(0) }
    SideEffect {
        firstVisibleItem?.invoke(index, offset)
        deltaScroll?.invoke(
            if (index != prevIndex) {
                if (index > prevIndex) prevHeight - prevOffset + offset else prevHeight - offset + prevOffset
            } else {
                offset - prevOffset
            }.also {
                prevHeight = firstVisibleItemHeight()
                prevIndex = index
                prevOffset = offset
            }
        )
    }
}


fun LazyListState.firstVisibleItemHeight(): Int {
    val firstItem = layoutInfo.visibleItemsInfo.firstOrNull()
    return firstItem?.size ?: 0
}

/**
 * Helper for animating a Number
 */

class Animatable(value: Float) {
    private var state by mutableStateOf(value)

    suspend fun snapTo(newValue: Float) {
        state = newValue
    }

    suspend fun animateTo(targetValue: Float, animSpec: AnimationSpec<Float> = spring()) {
        animate(
            state, targetValue,
            animationSpec = animSpec
        ) { newValue, velocity ->
            state = newValue
        }
    }

    operator fun invoke(): Float = state
}