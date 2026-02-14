package com.gliss.motionui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.gliss.motionui.ui.screens.AiAssistantScreen
import com.gliss.motionui.ui.screens.CardStackScreen
import com.gliss.motionui.ui.screens.GestureLabScreen
import com.gliss.motionui.ui.screens.HolographicScreen
import com.gliss.motionui.ui.screens.HomeScreen
import com.gliss.motionui.ui.screens.InteractiveVisualScreen
import com.gliss.motionui.ui.screens.LaunchFlowScreen
import com.gliss.motionui.ui.screens.OnboardingScreen
import com.gliss.motionui.ui.screens.PremiumScreen
import com.gliss.motionui.ui.screens.SplashScreen

sealed class Screen(val route: String) {
    object SplashScreen : Screen("splash")
    object LaunchFlow : Screen("launch_flow")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object CardStack : Screen("card_stack")
    object GestureLab : Screen("gesture_lab")
    object AiAssistant : Screen("ai_assistant")
    object Premium : Screen("premium")
    object InteractiveVisual : Screen("visuals")
    object Holographic : Screen("Holographic")
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(700))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(700))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(700))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(700, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(700))
        }
    ) {
        composable(Screen.SplashScreen.route) {
            SplashScreen(navController)
        }
        composable(Screen.LaunchFlow.route) {
            LaunchFlowScreen(navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.CardStack.route) {
            CardStackScreen(navController)
        }
        composable(Screen.GestureLab.route) {
            GestureLabScreen(navController)
        }
        composable(Screen.AiAssistant.route) {
            AiAssistantScreen(navController)
        }
        composable(Screen.Premium.route) {
            PremiumScreen(navController)
        }
        composable(Screen.InteractiveVisual.route) {
            InteractiveVisualScreen(navController)
        }
        composable(Screen.Holographic.route) {
            HolographicScreen(navController)
        }
    }
}
