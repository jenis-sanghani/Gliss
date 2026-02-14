package com.gliss.motionui.ui.screens

import android.app.Activity
import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import org.intellij.lang.annotations.Language
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Creates and remembers a Brush that renders a holographic/iridescent effect using AGSL shaders.
 * This effect requires Android Tiramisu (API 33) or higher. On older versions, it falls back to a solid color.
 *
 * @param speed The speed of the holographic animation.
 * @param angle The angle of the light beam in degrees.
 * @param verticalOffset The vertical shift of the texture.
 * @param baseColor The underlying color of the material.
 * @param intensity The brightness of the holographic reflection.
 * @param beamWidth The width of the light strip.
 * @param enableGradient Whether to mix a soft gradient into the base color.
 */
@Composable
fun rememberHolographicBrush(
    speed: Float = 0.3f,
    angle: Float = -20f,
    verticalOffset: Float = 0f,
    baseColor: Color = Color(0xFFA0A4A8),
    intensity: Float = 0.85f,
    beamWidth: Float = 90f,
    enableGradient: Boolean = true
): Brush {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return SolidColor(baseColor)
    }
    val infiniteTransition = rememberInfiniteTransition(label = "HoloPhysics")
    val time by if (speed > 0f) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween((3500 / speed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "Time"
        )
    } else {
        remember { mutableStateOf(0f) }
    }
    return remember(
        time,
        angle,
        verticalOffset,
        baseColor,
        intensity,
        beamWidth,
        enableGradient
    ) {
        ConfigurableHoloBrush(
            time,
            angle,
            verticalOffset,
            baseColor,
            intensity,
            beamWidth,
            enableGradient
        )
    }
}

/**
 * Renders text with a metallic gradient fill (Gold, Silver, Bronze, etc.).
 * The gradient simulates a reflective surface.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param baseColor The primary metal color.
 * @param angle The angle of the reflection.
 */
@Composable
fun MetallicText(
    text: String,
    style: TextStyle,
    baseColor: Color = Color(0xFFFFD700),
    angle: Float = 10f,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp
        )
    ) {
        val height = size.height
        val rad = Math.toRadians(angle.toDouble())
        val startY = 0f
        val endY = height
        val startX = 0f + (height * sin(rad)).toFloat()
        val endX = 0f - (height * sin(rad)).toFloat()

        val cBase = baseColor
        val cShadow = Color(
            red = cBase.red * 0.4f,
            green = cBase.green * 0.35f,
            blue = cBase.blue * 0.3f,
            alpha = 1f
        )
        val cHighlight = Color(1f, 1f, 0.98f, 1f)

        val brush = Brush.linearGradient(
            0.0f to cShadow,
            0.45f to cBase,
            0.50f to cHighlight,
            0.55f to cBase,
            1.0f to cShadow,
            start = Offset(startX, startY),
            end = Offset(endX, endY)
        )

        drawText(textLayoutResult = textLayoutResult, brush = brush, topLeft = Offset.Zero)
    }
}

/**
 * Renders text that simulates a glowing neon tube.
 * Includes an outer blur for the glow and an inner white core for the plasma effect.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param color The color of the neon gas.
 * @param flickerSpeed The intensity of the random flickering effect.
 */
@Composable
fun NeonText(
    text: String,
    style: TextStyle,
    color: Color,
    flickerSpeed: Float = 0.2f,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "NeonFlicker")
    val flickerNoise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "Noise"
    )

    val currentAlpha = remember(flickerNoise, flickerSpeed) {
        if (flickerSpeed > 0 && Math.random() < (flickerSpeed * 0.1)) {
            0.3f + (Math.random().toFloat() * 0.3f)
        } else {
            0.95f + (Math.random().toFloat() * 0.05f)
        }
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp + 40.dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp + 40.dp
        )
    ) {
        val centerOffset = Offset(20.dp.toPx(), 20.dp.toPx())
        val (fontName, androidStyle) = resolveFont(style)

        drawIntoCanvas { canvas ->
            val paint = Paint()
            paint.isAntiAlias = true
            paint.style = Paint.Style.FILL
            paint.color = color.copy(alpha = 0.4f * currentAlpha).toArgb()
            paint.maskFilter = BlurMaskFilter(40f, BlurMaskFilter.Blur.NORMAL)
            paint.typeface = Typeface.create(fontName, androidStyle)
            paint.textSize = style.fontSize.toPx()

            canvas.nativeCanvas.drawText(
                text,
                centerOffset.x,
                centerOffset.y + textLayoutResult.lastBaseline,
                paint
            )
        }

        drawIntoCanvas { canvas ->
            val nativePaint = Paint().apply {
                isAntiAlias = true
                textSize = style.fontSize.toPx()
                typeface = Typeface.create(fontName, androidStyle)
                textAlign = Paint.Align.LEFT
            }
            val baselineY = centerOffset.y + textLayoutResult.lastBaseline

            nativePaint.style = Paint.Style.STROKE
            nativePaint.strokeWidth = 12f
            nativePaint.color = color.copy(alpha = 0.8f * currentAlpha).toArgb()
            nativePaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
            nativePaint.strokeJoin = Paint.Join.ROUND
            nativePaint.strokeCap = Paint.Cap.ROUND
            canvas.nativeCanvas.drawText(text, centerOffset.x, baselineY, nativePaint)

            nativePaint.strokeWidth = 6f
            nativePaint.color = color.copy(alpha = 1f * currentAlpha).toArgb()
            nativePaint.maskFilter = BlurMaskFilter(4f, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawText(text, centerOffset.x, baselineY, nativePaint)

            nativePaint.strokeWidth = 3f
            nativePaint.color = Color.White.copy(alpha = 0.95f * currentAlpha).toArgb()
            nativePaint.maskFilter = null
            canvas.nativeCanvas.drawText(text, centerOffset.x, baselineY, nativePaint)
        }
    }
}

/**
 * Renders text with a simulated 3D extrusion effect.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param faceBrush The brush used for the front face of the text.
 * @param sideBrush The brush used for the extruded sides.
 * @param depthDp The depth of the extrusion.
 * @param angle The angle of the extrusion.
 */
@Composable
fun ThreeDText(
    text: String,
    style: TextStyle,
    faceBrush: Brush,
    sideBrush: Brush = SolidColor(Color.Black),
    depthDp: Dp = 8.dp,
    angle: Float = 45f,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val density = LocalDensity.current
    val depthPx = with(density) { depthDp.toPx() }

    val rad = Math.toRadians(angle.toDouble())
    val dx = (cos(rad) * 1.0).toFloat()
    val dy = (sin(rad) * 1.0).toFloat()

    Canvas(
        modifier = modifier.size(
            width = with(density) { textLayoutResult.size.width.toDp() + depthDp },
            height = with(density) { textLayoutResult.size.height.toDp() + depthDp }
        )
    ) {
        val layers = depthPx.toInt()
        for (i in layers downTo 1) {
            val offset = Offset(i * dx, i * dy)
            drawText(textLayoutResult, sideBrush, offset)
        }
        drawText(textLayoutResult, faceBrush, Offset.Zero)
    }
}

/**
 * Renders text with a burning fire effect.
 * Uses multiple layers of noise and color blending to simulate rising flames.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 */
@Composable
fun FireText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "FirePhysics")
    val noise1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Noise1"
    )
    val noise2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "Noise2"
    )

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp + 20.dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp + 60.dp
        )
    ) {
        val centerOffset = Offset(10.dp.toPx(), 40.dp.toPx())
        val (fontName, androidStyle) = resolveFont(style)

        drawIntoCanvas { canvas ->
            val paint = Paint()
            paint.isAntiAlias = true
            paint.textSize = style.fontSize.toPx()
            paint.typeface = Typeface.create(fontName, androidStyle)

            val x = centerOffset.x
            val y = centerOffset.y + textLayoutResult.lastBaseline

            val rise1 = noise1 * 25f
            paint.style = Paint.Style.FILL
            paint.color = Color(0xFF660000).toArgb()
            paint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
            canvas.nativeCanvas.drawText(text, x, y - rise1 - 10f, paint)

            val rise2 = noise2 * 10f
            paint.color = Color(0xFFFF4500).toArgb()
            paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
            val jitter = (noise2 - 0.5f) * 6f
            canvas.nativeCanvas.drawText(text, x + jitter, y - rise2, paint)

            paint.maskFilter = null
            val textHeight = textLayoutResult.size.height.toFloat()
            paint.shader = android.graphics.LinearGradient(
                0f, y - textHeight, 0f, y,
                intArrayOf(
                    Color(0xFFFF4500).toArgb(),
                    Color(0xFFFFD700).toArgb(),
                    Color(0xFFFFFFFF).toArgb()
                ),
                floatArrayOf(0.0f, 0.6f, 1.0f), Shader.TileMode.CLAMP
            )
            canvas.nativeCanvas.drawText(text, x, y, paint)
            paint.shader = null
        }
    }
}

/**
 * Renders text overlaid with a digital circuit board pattern.
 * Animates small data packets moving along the circuit lines.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param circuitColor The color of the circuit lines.
 */
@Composable
fun CircuitText(
    text: String,
    style: TextStyle,
    circuitColor: Color = Color(0xFF00E5FF),
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "CircuitData")
    val dataOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label = "DataFlow"
    )

    val circuits = remember {
        List(15) {
            val isHorizontal = Random.nextBoolean()
            CircuitLine(
                isHorizontal = isHorizontal,
                posPercent = Random.nextFloat(),
                lengthPercent = Random.nextFloat() * 0.5f + 0.2f,
                offsetPercent = Random.nextFloat(),
                speed = if (Random.nextBoolean()) 1f else -1f
            )
        }
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp
        )
    ) {
        val width = size.width
        val height = size.height

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val saveCount = canvas.nativeCanvas.saveLayer(0f, 0f, width, height, null)

            val (fontName, androidStyle) = resolveFont(style)
            paint.isAntiAlias = true
            paint.textSize = style.fontSize.toPx()
            paint.typeface = Typeface.create(fontName, androidStyle)
            paint.color = android.graphics.Color.BLACK

            canvas.nativeCanvas.drawText(text, 0f, textLayoutResult.lastBaseline, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            paint.color = Color(0xFF263238).toArgb()
            canvas.nativeCanvas.drawRect(0f, 0f, width, height, paint)

            val strokePaint = Paint().apply {
                this.style = Paint.Style.STROKE
                strokeWidth = 3f
                color = circuitColor.copy(alpha = 0.3f).toArgb()
                strokeCap = Paint.Cap.ROUND
            }
            val dotPaint = Paint().apply {
                this.style = Paint.Style.FILL
                color = android.graphics.Color.WHITE
                maskFilter = BlurMaskFilter(3f, BlurMaskFilter.Blur.NORMAL)
            }

            circuits.forEach { line ->
                val startX: Float
                val startY: Float
                val endX: Float
                val endY: Float

                if (line.isHorizontal) {
                    startX = line.offsetPercent * width
                    endX = startX + (line.lengthPercent * width)
                    startY = line.posPercent * height
                    endY = startY
                } else {
                    startY = line.offsetPercent * height
                    endY = startY + (line.lengthPercent * height)
                    startX = line.posPercent * width
                    endX = startX
                }

                canvas.nativeCanvas.drawLine(startX, startY, endX, endY, strokePaint)

                val travel = (dataOffset * line.speed).absoluteValue % 1f
                val dotX = startX + (endX - startX) * travel
                val dotY = startY + (endY - startY) * travel

                canvas.nativeCanvas.drawCircle(dotX, dotY, 3f, dotPaint)
                canvas.nativeCanvas.drawCircle(startX, startY, 2f, strokePaint)
                canvas.nativeCanvas.drawCircle(endX, endY, 2f, strokePaint)
            }

            paint.xfermode = null
            canvas.nativeCanvas.restoreToCount(saveCount)
        }
    }
}

/**
 * Data class representing a single line in the CircuitText effect.
 */
private data class CircuitLine(
    val isHorizontal: Boolean,
    val posPercent: Float,
    val lengthPercent: Float,
    val offsetPercent: Float,
    val speed: Float
)

/**
 * Renders text with a cyber-glitch effect.
 * Uses random offsets, color channel splitting, and slicing to simulate digital distortion.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param baseColor The main color of the text.
 * @param glitchColor1 The primary glitch offset color.
 * @param glitchColor2 The secondary glitch offset color.
 * @param intensity Controls the frequency and magnitude of the glitches.
 */
@Composable
fun GlitchText(
    text: String,
    style: TextStyle,
    baseColor: Color = Color.White,
    glitchColor1: Color = Color.Cyan,
    glitchColor2: Color = Color.Red,
    intensity: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "GlitchTicker")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(100, easing = LinearEasing), RepeatMode.Restart),
        label = "Time"
    )

    val randomState = remember(time) {
        object {
            val offsetX =
                if (Random.nextFloat() < 0.3f * intensity) Random.nextInt(-5, 5).toFloat() else 0f
            val offsetY =
                if (Random.nextFloat() < 0.3f * intensity) Random.nextInt(-2, 2).toFloat() else 0f
            val splitX =
                if (Random.nextFloat() < 0.4f * intensity) Random.nextInt(-8, 8).toFloat() else 0f
            val sliceY =
                if (Random.nextFloat() < 0.3f * intensity) Random.nextInt(0, 100).toFloat() else -1f
            val sliceHeight = Random.nextInt(2, 10).toFloat()
            val sliceOffset = Random.nextInt(-10, 10).toFloat()
        }
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp + 10.dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp + 10.dp
        )
    ) {
        val mainOffset = Offset(randomState.offsetX, randomState.offsetY)
        drawText(
            textLayoutResult,
            glitchColor2.copy(alpha = 0.5f),
            mainOffset + Offset(-randomState.splitX, 0f)
        )
        drawText(
            textLayoutResult,
            glitchColor1.copy(alpha = 0.5f),
            mainOffset + Offset(randomState.splitX, 0f)
        )
        drawText(textLayoutResult, baseColor, mainOffset)
        if (randomState.sliceY > 0) {
            val yPos = (randomState.sliceY / 100f) * size.height
            drawRect(
                glitchColor1.copy(alpha = 0.8f),
                Offset(randomState.sliceOffset, yPos),
                Size(size.width, randomState.sliceHeight)
            )
        }
    }
}

/**
 * Renders text filled with a liquid fluid effect.
 * The fluid level undulates with a wave pattern and bubbles rise to the surface.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 */
@Composable
fun LiquidText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "LiquidFlow")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "Phase"
    )

    val bubbles = remember {
        List(15) {
            BubbleData(
                Random.nextFloat(),
                Random.nextFloat(),
                Random.nextFloat() * 6f + 2f,
                Random.nextFloat() * 0.5f + 0.2f
            )
        }
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp
        )
    ) {
        val width = size.width
        val height = size.height

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val saveCount = canvas.nativeCanvas.saveLayer(0f, 0f, width, height, null)

            val (fontName, androidStyle) = resolveFont(style)
            paint.isAntiAlias = true
            paint.textSize = style.fontSize.toPx()
            paint.typeface = Typeface.create(fontName, androidStyle)
            paint.color = android.graphics.Color.BLACK

            canvas.nativeCanvas.drawText(text, 0f, textLayoutResult.lastBaseline, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            val wavePath = Path()
            val amplitude = 15f
            val frequency = 0.02f
            val waterLevel = height * 0.45f

            wavePath.moveTo(0f, height)
            wavePath.lineTo(0f, waterLevel)

            for (x in 0..width.toInt() step 5) {
                val y = waterLevel + sin(x * frequency + phase) * amplitude
                wavePath.lineTo(x.toFloat(), y)
            }

            wavePath.lineTo(width, height)
            wavePath.close()

            paint.shader = android.graphics.LinearGradient(
                0f, waterLevel - amplitude, 0f, height,
                intArrayOf(Color(0xFF00FFFF).toArgb(), Color(0xFF0000FF).toArgb()),
                null, Shader.TileMode.CLAMP
            )

            canvas.nativeCanvas.drawPath(wavePath.asAndroidPath(), paint)

            paint.shader = null
            paint.color = Color(0x88FFFFFF).toArgb()

            bubbles.forEach { bubble ->
                val currentY = (bubble.yPercent * height - (phase * 30 * bubble.speed)) % height
                val currentX = (bubble.xPercent * width) + sin(currentY * 0.05f) * 5f

                if (currentY > waterLevel) {
                    canvas.nativeCanvas.drawCircle(currentX, currentY, bubble.size, paint)
                }
            }

            paint.xfermode = null
            canvas.nativeCanvas.restoreToCount(saveCount)
        }
    }
}

/**
 * Data class representing a bubble in the LiquidText effect.
 */
private data class BubbleData(
    val xPercent: Float,
    val yPercent: Float,
    val size: Float,
    val speed: Float
)

/**
 * Renders text with a spotlight effect that illuminates the characters as it passes.
 * Uses a masking technique to reveal the gradient over the base text.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 */
@Composable
fun SpotlightText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "Spotlight")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Beam"
    )

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp
        )
    ) {
        val width = size.width
        val height = size.height

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val (fontName, androidStyle) = resolveFont(style)
            paint.typeface = Typeface.create(fontName, androidStyle)
            paint.textSize = style.fontSize.toPx()
            paint.isAntiAlias = true

            paint.color = Color(0xFF444444).toArgb()
            paint.style = Paint.Style.FILL
            canvas.nativeCanvas.drawText(text, 0f, textLayoutResult.lastBaseline, paint)

            val saveCount = canvas.nativeCanvas.saveLayer(0f, 0f, width, height, null)

            paint.color = android.graphics.Color.BLACK
            canvas.nativeCanvas.drawText(text, 0f, textLayoutResult.lastBaseline, paint)

            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

            val gradientWidth = width * 0.5f
            val startX = (width + gradientWidth) * progress - gradientWidth

            paint.shader = android.graphics.LinearGradient(
                startX, 0f, startX + gradientWidth, 0f,
                intArrayOf(
                    Color.Transparent.toArgb(),
                    Color(0xFFFFFFFF).toArgb(),
                    Color.Transparent.toArgb()
                ),
                floatArrayOf(0f, 0.5f, 1f),
                Shader.TileMode.CLAMP
            )

            canvas.nativeCanvas.drawRect(0f, 0f, width, height, paint)
            paint.xfermode = null
            canvas.nativeCanvas.restoreToCount(saveCount)
        }
    }
}

/**
 * Renders text with a chromatic aberration effect (RGB split).
 * The Red and Blue channels are offset from the base text with a random jitter.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 */
@Composable
fun ChromaticText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ChromaticJitter")
    val jitter by infiniteTransition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "Jitter"
    )

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp + 10.dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp + 5.dp
        )
    ) {
        val centerOffset = Offset(5.dp.toPx(), 2.dp.toPx())

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val (fontName, androidStyle) = resolveFont(style)
            paint.isAntiAlias = true
            paint.textSize = style.fontSize.toPx()
            paint.typeface = Typeface.create(fontName, androidStyle)

            val baseX = centerOffset.x
            val baseY = centerOffset.y + textLayoutResult.lastBaseline

            val offsetAmount = 6f + jitter * 2f

            paint.color = Color(0xFFFF0000).copy(alpha = 0.7f).toArgb()
            canvas.nativeCanvas.drawText(text, baseX - offsetAmount, baseY, paint)

            paint.color = Color(0xFF0000FF).copy(alpha = 0.7f).toArgb()
            canvas.nativeCanvas.drawText(text, baseX + offsetAmount, baseY, paint)

            paint.color = Color(0xFF212121).toArgb()
            canvas.nativeCanvas.drawText(text, baseX, baseY, paint)
        }
    }
}

/**
 * Renders text with a hand-drawn sketch animation.
 * The text appears to be written from left to right using a stroke paint.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 * @param strokeColor The color of the pen stroke.
 */
@Composable
fun SketchText(
    text: String,
    style: TextStyle,
    strokeColor: Color = Color(0xFF263238),
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val revealed = remember { Animatable(0f) }
    LaunchedEffect(text) {
        revealed.snapTo(0f)
        revealed.animateTo(1f, animationSpec = tween(2000, easing = LinearEasing))
    }

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp + 10.dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp + 10.dp
        )
    ) {
        val width = size.width
        val height = size.height
        val centerOffset = Offset(5.dp.toPx(), 5.dp.toPx())
        val (fontName, androidStyle) = resolveFont(style)

        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                isAntiAlias = true
                this.style = Paint.Style.STROKE
                strokeWidth = 3f
                color = strokeColor.toArgb()
                typeface = Typeface.create(fontName, androidStyle)
                textSize = style.fontSize.toPx()
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }

            val saveCount = canvas.nativeCanvas.save()
            val clipWidth = width * revealed.value
            canvas.nativeCanvas.clipRect(0f, 0f, clipWidth, height)

            val r = Random(text.hashCode())
            repeat(3) {
                val dx = r.nextFloat() * 3f - 1.5f
                val dy = r.nextFloat() * 3f - 1.5f
                canvas.nativeCanvas.drawText(
                    text,
                    centerOffset.x + dx,
                    centerOffset.y + textLayoutResult.lastBaseline + dy,
                    paint
                )
            }

            canvas.nativeCanvas.restoreToCount(saveCount)
        }
    }
}

/**
 * Renders text with a smooth, continuously flowing gradient.
 *
 * @param text The string to display.
 * @param style The text style configuration.
 */
@Composable
fun GradientFlowText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(text, style) {
        textMeasurer.measure(text, style)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "GradientFlow")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 4000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Restart),
        label = "Flow"
    )

    Canvas(
        modifier = modifier.size(
            width = (textLayoutResult.size.width / LocalDensity.current.density).dp,
            height = (textLayoutResult.size.height / LocalDensity.current.density).dp
        )
    ) {
        val width = size.width
        val height = size.height

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val (fontName, androidStyle) = resolveFont(style)
            paint.typeface = Typeface.create(fontName, androidStyle)
            paint.textSize = style.fontSize.toPx()
            paint.isAntiAlias = true

            val colors = intArrayOf(
                Color(0xFF8E24AA).toArgb(), // Purple
                Color(0xFFBA68C8).toArgb(), // Light Purple
                Color(0xFFFF4081).toArgb(), // Pink
                Color(0xFFFF80AB).toArgb(), // Light Pink
                Color(0xFFFFD740).toArgb(), // Orange/Gold
                Color(0xFF8E24AA).toArgb()  // Back to Purple
            )
            val positions = floatArrayOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f)

            val currentOffset = offset % (width * 2)
            paint.shader = android.graphics.LinearGradient(
                -width + currentOffset, 0f, width + currentOffset, height,
                colors, positions, Shader.TileMode.MIRROR
            )

            canvas.nativeCanvas.drawText(text, 0f, textLayoutResult.lastBaseline, paint)
        }
    }
}

/**
 * Custom ShaderBrush implementation for the Holographic effect.
 * Wraps an AGSL RuntimeShader and updates uniforms (time, angle, etc.) per frame.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class ConfigurableHoloBrush(
    time: Float,
    angle: Float,
    verticalOffset: Float,
    baseColor: Color,
    intensity: Float,
    beamWidth: Float,
    enableGradient: Boolean
) : ShaderBrush() {
    private val shader = RuntimeShader(ELEGANT_SHADER).apply {
        setFloatUniform("time", time)
        setFloatUniform("angle", Math.toRadians(angle.toDouble()).toFloat())
        setFloatUniform("yOffset", verticalOffset)
        setFloatUniform("intensity", intensity)
        setFloatUniform("beamWidth", beamWidth)
        setIntUniform("enableGradient", if (enableGradient) 1 else 0)
        setFloatUniform(
            "baseColor",
            baseColor.red,
            baseColor.green,
            baseColor.blue,
            baseColor.alpha
        )
    }

    override fun createShader(size: Size): Shader {
        shader.setFloatUniform("resolution", size.width, size.height)
        return shader
    }

    companion object {
        @Language("AGSL")
        val ELEGANT_SHADER = """
            uniform float2 resolution;
            uniform float time;
            uniform float angle;
            uniform float yOffset;
            uniform float intensity;
            uniform float beamWidth;
            uniform int enableGradient;
            uniform vec4 baseColor;
            vec3 softGradient(float t) {
                vec3 a = vec3(0.75, 0.75, 0.75); 
                vec3 b = vec3(0.30, 0.30, 0.30); 
                vec3 c = vec3(1.0, 1.0, 1.0);
                vec3 d = vec3(0.00, 0.15, 0.30); 
                return a + b * cos(6.28318 * (c * t + d));
            }
            vec4 main(float2 fragCoord) {
                float2 globalCoord = fragCoord;
                globalCoord.y += yOffset;
                float2 uv = globalCoord / 800.0; 
                vec3 finalColor = baseColor.rgb;
                if (enableGradient == 1) {
                    float gradientPos = uv.x * 0.5 + (uv.y * 0.2);
                    vec3 gradientColor = softGradient(gradientPos);
                    finalColor = mix(finalColor, gradientColor, 0.6);
                }
                float2 p = globalCoord;
                float s = sin(angle); float c = cos(angle);
                mat2 rot = mat2(c, -s, s, c);
                p = p * rot;
                float beamPos = (time * 2500.0) - 700.0; 
                float dist = abs(p.x - beamPos);
                float beamInt = 1.0 - smoothstep(0.0, beamWidth, dist);
                vec3 beamColor = vec3(0.95, 0.95, 1.0); 
                vec3 prism = softGradient(uv.x * 2.0 + time);
                beamColor = mix(beamColor, prism, 0.25);
                finalColor += beamColor * beamInt * intensity;
                return vec4(finalColor, 1.0);
            }
        """
    }
}

/**
 * Resolves the appropriate Android Typeface and font weight style from a Compose TextStyle.
 */
private fun resolveFont(style: TextStyle): Pair<String, Int> {
    val fontName = when (style.fontFamily) {
        FontFamily.Serif -> "serif"
        FontFamily.Monospace -> "monospace"
        FontFamily.Cursive -> "cursive"
        else -> "sans-serif"
    }
    val weight = style.fontWeight?.weight ?: 400
    val isBold = weight >= 600
    val androidStyle = if (isBold) Typeface.BOLD else Typeface.NORMAL
    return Pair(fontName, androidStyle)
}

/**
 * Enum defining the stages of the main demo animation sequence.
 */
enum class DemoStage { Intro, Loop, Outro }

/**
 * Main entry point for the Holographic Text Demo.
 * Orchestrates the transition between the Intro, the multi-language Loop, and the Outro.
 * Manages system bar coloring based on the current text effect.
 */
@Composable
fun HolographicScreen(navController: NavHostController) {
    var stage by remember { mutableStateOf(DemoStage.Intro) }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            if (stage != DemoStage.Loop) {
                insets.isAppearanceLightStatusBars = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = stage,
            transitionSpec = {
                when (targetState) {
                    DemoStage.Loop -> {
                        (fadeIn(tween(1000)) + scaleIn(initialScale = 0.8f)).togetherWith(
                            fadeOut(tween(1000)) + scaleOut(targetScale = 1.5f)
                        )
                    }

                    DemoStage.Outro -> {
                        (slideInVertically { h -> h } + fadeIn(tween(800))).togetherWith(
                            slideOutVertically { h -> -h } + fadeOut(tween(800))
                        )
                    }

                    else -> fadeIn() togetherWith fadeOut()
                }
            },
            label = "GlobalState"
        ) { currentStage ->
            when (currentStage) {
                DemoStage.Intro -> {
                    IntroView(onFinished = { stage = DemoStage.Loop })
                }

                DemoStage.Loop -> {
                    LoopView(onFinished = { stage = DemoStage.Outro })
                }

                DemoStage.Outro -> {
                    OutroView()
                }
            }
        }
    }
}

/**
 * Displays the initial "10,000" milestone animation.
 */
@Composable
fun IntroView(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE8EAED)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "10,000",
            style = TextStyle(
                brush = rememberHolographicBrush(
                    speed = 0.7f,
                    baseColor = Color(0xFF2C3E50),
                    intensity = 1.0f,
                    beamWidth = 100f
                ),
                fontSize = 110.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Black,
                letterSpacing = (-4).sp
            )
        )
    }
}

/**
 * Cycles through a list of "Thank You" messages in various languages.
 * Applies a different randomized transition direction for each word.
 */
@Composable
fun LoopView(onFinished: () -> Unit) {
    val words = remember {
        listOf(
            "Thank You", "Gracias", "Merci", "Danke", "شكراً",
            "ありがとう", "谢谢", "Спасибо", "Obrigado", "Grazie",
            "Ευχαριστώ", "Tack", "감사합니다", "Dziękuję", "Teşekkürler", "धन्यवाद",
            "Dank u", "Cảm ơn", "ขอบคุณ", "Terima Kasih", "תודה", "Salamat",
            "Děkuji", "Köszönöm", "Mulțumesc", "Takk", "Kiitos", "Tak",
            "Terima Kasih", "Дякую"
        )
    }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        for (i in words.indices) {
            currentIndex = i
            kotlinx.coroutines.delay(2000)
        }
        kotlinx.coroutines.delay(500)
        onFinished()
    }

    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            val direction = targetState % 4

            val enter = when (direction) {
                0 -> slideInVertically { h -> h } + fadeIn(tween(800, easing = LinearEasing))
                1 -> slideInVertically { h -> -h } + fadeIn(tween(800, easing = LinearEasing))
                2 -> slideInHorizontally { w -> w } + fadeIn(tween(800, easing = LinearEasing))
                else -> slideInHorizontally { w -> -w } + fadeIn(tween(800, easing = LinearEasing))
            }

            val exit = when (direction) {
                0 -> slideOutVertically { h -> -h } + fadeOut(tween(800, easing = LinearEasing))
                1 -> slideOutVertically { h -> h } + fadeOut(tween(800, easing = LinearEasing))
                2 -> slideOutHorizontally { w -> -w } + fadeOut(tween(800, easing = LinearEasing))
                else -> slideOutHorizontally { w -> w } + fadeOut(tween(800, easing = LinearEasing))
            }

            enter.togetherWith(exit)
        },
        label = "LoopAnimation"
    ) { index ->
        FullScreenMessage(text = words[index], seed = index)
    }
}

/**
 * Displays the final "More will come" message.
 */
@Composable
fun OutroView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F4F7)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "More will come",
            style = TextStyle(
                brush = rememberHolographicBrush(
                    speed = 0.3f,
                    baseColor = Color(0xFF0077B5),
                    intensity = 1.0f,
                    beamWidth = 80f
                ),
                fontSize = 55.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        )
    }
}

/**
 * Renders a full-screen message with a specific text effect based on the provided seed.
 * Also handles background colors and status bar styling.
 *
 * @param text The text to display.
 * @param seed The integer seed used to deterministically select the visual effect.
 */
@Composable
fun FullScreenMessage(text: String, seed: Int) {
    val random = Random(seed)
    val effectType = seed % 11

    val isSpotlight = (effectType == 7)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !isSpotlight
        }
    }

    val bgBrush = if (isSpotlight) {
        Brush.radialGradient(colors = listOf(Color(0xFF212121), Color(0xFF000000)), radius = 1800f)
    } else {
        when (effectType) {
            0 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFCFD8DC)))
            1 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFF3E5F5)))
            2 -> Brush.radialGradient(listOf(Color(0xFFFFFDE7), Color(0xFFFFE0B2)))
            3 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFECEFF1)))
            4 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE3F2FD)))
            5 -> Brush.radialGradient(listOf(Color(0xFFFAFAFA), Color(0xFFE0F2F1)))
            6 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFE0F7FA)))
            8 -> Brush.radialGradient(listOf(Color(0xFFFFFFFF), Color(0xFFFAFAFA)))
            9 -> Brush.radialGradient(
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFDE7)
                )
            )
            10 -> Brush.radialGradient(
                listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFF3E5F5)
                )
            )
            else -> SolidColor(Color.White)
        }
    }

    val fontSize = if (text.length > 8) 55.sp else 75.sp

    val baseStyle = TextStyle(
        fontSize = fontSize,
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        textAlign = TextAlign.Center
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush),
        contentAlignment = Alignment.Center
    ) {
        when (effectType) {
            0 -> {
                LiquidText(text = text, style = baseStyle)
            }

            1 -> {
                val neonPalette = listOf(
                    Color(0xFFD500F9),
                    Color(0xFF2962FF),
                    Color(0xFFFF0048),
                    Color(0xFF0091EA)
                )
                NeonText(
                    text = text,
                    color = neonPalette[random.nextInt(neonPalette.size)],
                    style = baseStyle
                )
            }

            2 -> {
                FireText(text = text, style = baseStyle)
            }

            3 -> {
                val premiumColors = listOf(
                    Color(0xFF2E86C1),
                    Color(0xFFE74C3C),
                    Color(0xFF8E44AD),
                    Color(0xFF27AE60)
                )
                val faceColor = premiumColors[random.nextInt(premiumColors.size)]
                ThreeDText(
                    text = text,
                    style = baseStyle,
                    faceBrush = SolidColor(faceColor),
                    sideBrush = SolidColor(Color.Black),
                    depthDp = 15.dp,
                    angle = 30f
                )
            }

            4 -> {
                CircuitText(text = text, style = baseStyle.copy(fontWeight = FontWeight.ExtraBold))
            }

            5 -> {
                GlitchText(
                    text = text,
                    style = baseStyle.copy(fontFamily = FontFamily.Monospace),
                    baseColor = Color.Black,
                    glitchColor1 = Color.Magenta,
                    glitchColor2 = Color.Blue
                )
            }

            6 -> {
                val metalType = (seed / 11) % 3
                val metalColor = when (metalType) {
                    0 -> Color(0xFFFFD700)
                    1 -> Color(0xFFC0C0C0)
                    else -> Color(0xFFCD7F32)
                }
                MetallicText(
                    text = text,
                    baseColor = metalColor,
                    style = baseStyle.copy(fontFamily = FontFamily.Serif)
                )
            }

            7 -> {
                SpotlightText(text = text, style = baseStyle)
            }

            8 -> {
                ChromaticText(
                    text = text,
                    style = baseStyle.copy(fontWeight = FontWeight.ExtraBold)
                )
            }

            9 -> {
                SketchText(text = text, style = baseStyle.copy(fontFamily = FontFamily.Cursive))
            }

            10 -> {
                GradientFlowText(
                    text = text,
                    style = baseStyle.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
        }
    }
}