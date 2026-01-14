package com.gliss.motionui.ai

class AiRepository(private val client: AiClient = AiClient()) {
    suspend fun fetchResponse(message: String): String {
        return client.getAiResponse(message)
    }
}
