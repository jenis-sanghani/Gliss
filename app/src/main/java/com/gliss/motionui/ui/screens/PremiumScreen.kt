package com.gliss.motionui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gliss.motionui.analytics.AnalyticsManager
import com.gliss.motionui.billing.PremiumViewModel
import com.gliss.motionui.ui.components.AbstractShaderBackground

@Composable
fun PremiumScreen(
    navController: NavController,
    viewModel: PremiumViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        AnalyticsManager.logScreenView("Premium")
    }
    val isPremium by viewModel.isPremium.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Abstract Shader Background
        AbstractShaderBackground(speed = 0.5f)
        
        // Darkened Overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Gliss Premium",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Elevated interaction experience",
                color = Color(0xFFB39DDB),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            PremiumFeatureItem("Advanced AI Assistant")
            PremiumFeatureItem("Exclusive Gesture Patterns")
            PremiumFeatureItem("Advanced Haptic Feedback")
            PremiumFeatureItem("Custom UI Themes")

            Spacer(modifier = Modifier.weight(1f))

            if (isPremium) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B5E20))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Premium Active", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { 
                        viewModel.upgradeToPremium()
                        AnalyticsManager.logConversion("annual_premium", 9.99)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .border(1.dp, Color(0xFFB39DDB).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    enabled = !isProcessing,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(color = Color(0xFFB39DDB), modifier = Modifier.size(24.dp))
                    } else {
                        Text("Upgrade Plan — $9.99/yr", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Restore Purchase",
                color = Color.Gray,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PremiumFeatureItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFB39DDB).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFB39DDB), modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, color = Color.White, fontSize = 16.sp)
    }
}
