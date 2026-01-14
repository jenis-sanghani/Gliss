package com.gliss.motionui.ui.components

import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.platform.LocalContext
import com.gliss.motionui.R

@Composable
fun AbstractShaderBackground(
    modifier: Modifier = Modifier,
    speed: Float = 1.0f
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val context = LocalContext.current
        val shaderContent = remember {
            context.resources.openRawResource(R.raw.abstract_shader).bufferedReader().use { it.readText() }
        }
        val shader = remember(shaderContent) { RuntimeShader(shaderContent) }
        
        val infiniteTransition = rememberInfiniteTransition(label = "shaderTime")
        val time by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(60000 / speed.toInt().coerceAtLeast(1), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "time"
        )

        Canvas(modifier = modifier.fillMaxSize()) {
            shader.setFloatUniform("resolution", size.width, size.height)
            shader.setFloatUniform("time", time)
            shader.setFloatUniform("mouse", 0f, 0f) // Ready for future touch integration
            shader.setFloatUniform("intensity_factor", 1.0f)
            
            drawRect(brush = ShaderBrush(shader))
        }
    }
}
