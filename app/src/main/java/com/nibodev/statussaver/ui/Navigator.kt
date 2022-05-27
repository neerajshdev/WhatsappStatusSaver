package com.nibodev.statussaver.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import java.util.Stack

typealias ComposeStack = Stack<ComposableFunction>
typealias ComposableFunction = @Composable () -> Unit

val LocalNavController = staticCompositionLocalOf { NavigationController() }

@Composable
fun Navigator(
    controller: NavigationController,
    content: ComposableFunction
) {
    if (controller.size() == 0)
        controller.push(content)
    NavigateThrough(controller)
}

@Composable
private fun NavigateThrough(
    controller: NavigationController
) {
    Crossfade(targetState = controller.topContent) {
        it.invoke()
        BackHandler(controller.size() > 1) {
            controller.pop()
        }
    }
}

class NavigationController {
    private val contentStack = ComposeStack()

    var topContent: ComposableFunction by mutableStateOf({})

    fun push(content: ComposableFunction) {
        contentStack.push(content)
        topContent = contentStack.peek()
    }

    fun pop() {
        contentStack.pop()
        topContent = contentStack.peek()
    }

    fun replace(content:  ComposableFunction) {
        contentStack.pop()
        contentStack.push(content)
        topContent = contentStack.peek()
    }

    fun size() = contentStack.size
}