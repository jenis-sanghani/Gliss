package com.gliss.motionui.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gliss.motionui.analytics.AnalyticsManager
import com.gliss.motionui.navigation.Screen
import com.gliss.motionui.ui.components.AbstractShaderBackground
import kotlin.math.abs

data class OnboardingStep(
    val title: String,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView("Onboarding")
    }

    // Refined, softer color palette
    val steps = remember {
        listOf(
            OnboardingStep(
                "Welcome to Gliss",
                "Experience fluid gestures refined for the modern interface. Elegance in every motion.",
                Color(0xFF916BFF) // Soft Lavender
            ),
            OnboardingStep(
                "Visual Feedback",
                "Subtle reactions to every touch point. A language of motion that feels natural.",
                Color(0xFF5EDAD0) // Muted Teal
            ),
            OnboardingStep(
                "Interactive Lab",
                "Experiment with calibrated physics and weighted interactions in our Gesture Lab.",
                Color(0xFFB39DDB) // Soft Purple
            ),
            OnboardingStep(
                "Intelligent Aid",
                "Context-aware assistance designed to harmonize with your creative flow.",
                Color(0xFFFF9E80) // Soft Coral
            )
        )
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    
    val step = steps[currentStep]
    
    // Smooth transition for the ambient background glow
    val animatedGlowColor by animateColorAsState(
        targetValue = step.color,
        animationSpec = tween(1000, easing = LinearOutSlowInEasing),
        label = "glowColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                },
                onDragStopped = {
                    if (offsetX < -150f && currentStep < steps.size - 1) {
                        currentStep++
                        AnalyticsManager.logEvent("onboarding_step_reached", mapOf("step" to currentStep))
                    } else if (offsetX > 150f && currentStep > 0) {
                        currentStep--
                    } else if (offsetX < -300f && currentStep == steps.size - 1) {
                        AnalyticsManager.logEvent("onboarding_completed")
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                    offsetX = 0f
                }
            )
    ) {
        // Abstract Shader Background
        AbstractShaderBackground(speed = 0.3f)
        
        // Darkened Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )

        // Ultra-soft diffused ambient glow
        Box(
            modifier = Modifier
                .size(500.dp)
                .align(Alignment.Center)
                .graphicsLayer {
                    alpha = 0.08f
                    translationX = offsetX * 0.1f // Very subtle parallax
                }
                .background(Brush.radialGradient(listOf(animatedGlowColor, Color.Transparent)))
        )

        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (fadeIn(animationSpec = tween(600, easing = EaseOutQuart)) + 
                 slideInHorizontally(animationSpec = tween(600, easing = EaseOutQuart)) { direction * it / 2 })
                    .togetherWith(fadeOut(animationSpec = tween(400)) + 
                                  slideOutHorizontally(animationSpec = tween(400)) { -direction * it / 4 })
            },
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            label = "stepContent"
        ) { targetIndex ->
            val stepData = steps[targetIndex]
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp)
                    .graphicsLayer {
                        val dragProgress = (abs(offsetX) / 1000f).coerceIn(0f, 1f)
                        scaleX = 1f - (dragProgress * 0.02f)
                        scaleY = 1f - (dragProgress * 0.02f)
                        alpha = 1f - (dragProgress * 0.2f)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(180.dp)) // Fixed top margin for stability

                // Refined circle indicator
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(stepData.color.copy(alpha = 0.15f))
                        .padding(12.dp)
                        .clip(CircleShape)
                        .background(stepData.color),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (targetIndex + 1).toString(),
                        color = Color.Black,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(72.dp))

                Text(
                    text = stepData.title,
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = stepData.description,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Minimal refined indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 84.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, _ ->
                val active = index == currentStep
                val width by animateDpAsState(
                    targetValue = if (active) 24.dp else 6.dp,
                    animationSpec = spring(stiffness = Spring.StiffnessLow),
                    label = "width"
                )
                
                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .width(width)
                        .clip(CircleShape)
                        .background(
                            if (active) step.color else Color.White.copy(alpha = 0.15f)
                        )
                )
            }
        }
        
        // Subtle footer hint
        AnimatedVisibility(
            visible = offsetX == 0f,
            enter = fadeIn(tween(1000)),
            exit = fadeOut(tween(300)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text = if (currentStep == steps.size - 1) "Swipe to begin" else "Swipe left",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp
            )
        }
    }
}
