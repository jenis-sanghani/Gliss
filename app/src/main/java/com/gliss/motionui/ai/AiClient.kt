package com.gliss.motionui.ai

import kotlinx.coroutines.delay

/**
 * Mock AI Client for Gliss Assistant.
 * In a real app, this would integrate with Gemini or another LLM API.
 */
class AiClient {
    suspend fun getAiResponse(message: String): String {
        // Simulate network delay
        delay(1500)
        
        return when {
            message.contains("gesture", ignoreCase = true) -> 
                "In Gliss, gestures are handled using pointerInput and Animatable for fluid motion. Try the Gesture Lab to see it in action!"
            message.contains("premium", ignoreCase = true) -> 
                "Gliss Premium unlocks advanced AI features and complex gesture patterns. Check out the Premium screen!"
            message.contains("animation", ignoreCase = true) -> 
                "We use Jetpack Compose Animatable for almost everything here. It provides the best performance for interactive UI."
            else -> "That's a great question about Gliss! How else can I help you explore our gesture-based world?"
        }
    }
}
