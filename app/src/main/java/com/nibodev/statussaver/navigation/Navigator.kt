package com.nibodev.statussaver.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.nibodev.statussaver.console

typealias ComposableFunction = @Composable () -> Unit
typealias ComposableStack = java.util.Stack<ComposableFunction>

val LocalNavController = staticCompositionLocalOf { NavigationController() }


@Composable
fun Navigator(
    navController: NavigationController,
    content: ComposableFunction
) {
    if (navController.size() == 0)
        navController.push(content)
    NavigateThrough(navController)
}


@Composable
private fun NavigateThrough(
    controller: NavigationController
) {
    // we read the mutable state here i.e topContent
    // when this changes, NavigateThrough runs again because of recomposition
    Crossfade(targetState = controller.topContent) {
        BackHandler(controller.size() > 1) {
            controller.pop()
        }
        it()
    }
}


/**
 * Controls the navigation with push, pop and replace
 * Call the Navigator Composable and pass in this navigation controller
 *
 * Each screen is represented by UIComponent object,
 * To construct an UIComponent call uiComponent builder or sub class the UIComponent
 * Instead of passing ui component you can also pass a lambda
 */
open class NavigationController {
    private val uiStack = ComposableStack()
    var topContent: ComposableFunction by mutableStateOf({})

    fun push(content: ComposableFunction) {
        uiStack.push(content)
        topContent = uiStack.peek()
        console("total screens = ${uiStack.size}")
    }

    fun pop() {
        if (uiStack.size > 1) {
            uiStack.pop()
            topContent = peek()
        }
    }


    /**
     * Replace the top UIComponent
     * with another.
     */
    fun replace(content: ComposableFunction) {
        uiStack.pop()
        uiStack.push(content)
        topContent = peek()
    }

    fun peek() = uiStack.peek()
    fun size() = uiStack.size
}


/*
/**
 * AnimatedNavigation controller delegates to Transition object to
 * perform transiton from a Ui Component to another on making a push or pop
 */
interface Transition {
    fun transition(from: ComposableFunction, to: ComposableFunction)
}

/**
 * Supports screen transitions
 * @param transitionObject provide your custom transition implementation or
 * use one define in transition package
 */
class AnimatedNavigationController(var transitionObject: Transition) : NavigationController() {
    override fun push(content: ComposableFunction) {
        val from = peek()
        val to = content
        super.push(content)
        transitionObject.transition(from, to)
    }

    override fun pop() {
        val from = peek()
        pop()
        val to = peek()
        transitionObject.transition(from, to)
    }
}

 */