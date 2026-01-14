package com.gliss.motionui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
                    text = "Touchra",
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

            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                TouchCard(
                    title = "AI Assistant",
                    subtitle = "Get gesture tips & help",
                    onClick = {
                        AnalyticsManager.logGesture("tap", "AI Assistant")
                        navController.navigate(Screen.AiAssistant.route)
                    }
                )
                TouchCard(
                    title = "Card Stack",
                    subtitle = "Fluid swipe animations",
                    onClick = {
                        AnalyticsManager.logGesture("tap", "Card Stack")
                        navController.navigate(Screen.CardStack.route)
                    }
                )
                TouchCard(
                    title = "Gesture Lab",
                    subtitle = "Interactive touch experiments",
                    onClick = {
                        AnalyticsManager.logGesture("tap", "Gesture Lab")
                        navController.navigate(Screen.GestureLab.route)
                    }
                )
                TouchCard(
                    title = "Motion UI",
                    subtitle = "Reactive visual experience",
                    onClick = {
                        AnalyticsManager.logGesture("tap", "Motion UI")
                        navController.navigate(Screen.InteractiveVisual.route)
                    }
                )
            }
        }
    }
}
