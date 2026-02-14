package com.gliss.motionui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gliss.motionui.analytics.AnalyticsManager
import com.gliss.motionui.navigation.Screen
import com.gliss.motionui.ui.components.TouchCard

@Composable
fun HomeScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView("Home")
    }
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gliss",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                )
                IconButton(
                    onClick = { 
                        AnalyticsManager.logEvent("premium_icon_clicked")
                        navController.navigate(Screen.Premium.route)
                    },
                    modifier = Modifier.background(Color(0xFFB39DDB).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Premium", tint = Color(0xFFB39DDB))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    TouchCard(
                        title = "AI Assistant",
                        subtitle = "Get gesture tips & help",
                        onClick = {
                            AnalyticsManager.logGesture("tap", "AI Assistant")
                            navController.navigate(Screen.AiAssistant.route)
                        }
                    )
                }

                item {
                    TouchCard(
                        title = "Card Stack",
                        subtitle = "Fluid swipe animations",
                        onClick = {
                            AnalyticsManager.logGesture("tap", "Card Stack")
                            navController.navigate(Screen.CardStack.route)
                        }
                    )
                }

                item {
                    TouchCard(
                        title = "Gesture Lab",
                        subtitle = "Interactive touch experiments",
                        onClick = {
                            AnalyticsManager.logGesture("tap", "Gesture Lab")
                            navController.navigate(Screen.GestureLab.route)
                        }
                    )
                }

                item {
                    TouchCard(
                        title = "Motion UI",
                        subtitle = "Reactive visual experience",
                        onClick = {
                            AnalyticsManager.logGesture("tap", "Motion UI")
                            navController.navigate(Screen.InteractiveVisual.route)
                        }
                    )
                }

                item {
                    TouchCard(
                        title = "Holographic",
                        subtitle = "Holographic exp",
                        onClick = {
                            AnalyticsManager.logGesture("tap", "Holographic UI")
                            navController.navigate(Screen.Holographic.route)
                        }
                    )
                }
            }
        }
    }
}
