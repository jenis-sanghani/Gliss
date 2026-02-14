/*
 * Copyright 2026 Kyriakos Georgiopoulos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gliss.motionui.ui.screens

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.gliss.motionui.analytics.AnalyticsManager

// Sci-Fi AGSL Shader Source
private const val DISTORTION_SHADER = """
    uniform float2 iResolution;
    uniform float iTime;
    uniform float2 iMouse;
    uniform shader iContent;

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / iResolution.xy;
        float2 mouse = iMouse / iResolution.xy;
        
        // Liquid warp centered around touch
        float d = distance(uv, mouse);
        float strength = 0.05 * exp(-d * 10.0);
        float2 dist = (uv - mouse) * strength * sin(iTime * 2.0 - d * 20.0);
        
        // Apply distortion to content
        return iContent.eval(fragCoord + dist * iResolution.xy);
    }
"""

@Composable
fun InteractiveVisualScreen(navController: NavController) {
    val videos = remember { listOf("videos/1.mp4", "videos/2.mp4") }
    val pagerState = rememberPagerState(pageCount = { videos.size })
    var isFocusMode by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(pagerState.currentPage) {
        AnalyticsManager.logEvent("sci_fi_visual_view", mapOf("index" to pagerState.currentPage))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            key = { videos[it] }
        ) { page ->
            SciFiVisualItem(
                videoPath = videos[page],
                isFocusMode = isFocusMode,
                onToggleFocus = {
                    isFocusMode = !isFocusMode
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )
        }

        // Cyber HUD Overlay
        if (!isFocusMode) {
            SciFiHUD()
        }

        // Overlay UI
        AnimatedVisibility(
            visible = !isFocusMode,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(32.dp)
                        .navigationBarsPadding()
                ) {
                    Text(
                        text = "CORE ENGINE: ${pagerState.currentPage + 1}",
                        color = Color(0xFF80DEEA),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "SYNCING VISUAL BUFFER...",
                        color = Color(0xFF80DEEA).copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun SciFiVisualItem(
    videoPath: String,
    isFocusMode: Boolean,
    onToggleFocus: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var touchPoint by remember { mutableStateOf(Offset.Zero) }
    val time = rememberInfiniteTransition(label = "shader_time").animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    DisposableEffect(videoPath) {
        val mediaItem = MediaItem.fromUri(Uri.parse("asset:///$videoPath"))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { point ->
                        touchPoint = point
                        onToggleFocus()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                        touchPoint = change.position
                    },
                    onDragEnd = { dragOffset = Offset.Zero }
                )
            }
            .graphicsLayer {
                rotationY = (dragOffset.x / 20f).coerceIn(-15f, 15f)
                rotationX = (-dragOffset.y / 20f).coerceIn(-15f, 15f)
                cameraDistance = 12f
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // RuntimeShader Distortion (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val shader = remember { RuntimeShader(DISTORTION_SHADER) }
            shader.setFloatUniform("iTime", time.value)
            shader.setFloatUniform("iMouse", touchPoint.x, touchPoint.y)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        renderEffect = RenderEffect.createRuntimeShaderEffect(
                            shader, "iContent"
                        ).asComposeRenderEffect()
                    }
            )
        }

        // Parallax Cyber Glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = dragOffset.x * 0.1f
                    translationY = dragOffset.y * 0.1f
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF80DEEA).copy(alpha = 0.05f), Color.Transparent),
                        center = touchPoint,
                        radius = 1000f
                    )
                )
        )
    }
}

@Composable
fun SciFiHUD() {
    val infiniteTransition = rememberInfiniteTransition(label = "hud")
    val scanAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing), RepeatMode.Reverse)
    )
    val scanY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing))
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Digital Grid
        val gridSize = 60.dp.toPx()
        for (x in 0..(width / gridSize).toInt()) {
            drawLine(
                color = Color(0xFF80DEEA).copy(alpha = 0.05f),
                start = Offset(x * gridSize, 0f),
                end = Offset(x * gridSize, height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(height / gridSize).toInt()) {
            drawLine(
                color = Color(0xFF80DEEA).copy(alpha = 0.05f),
                start = Offset(0f, y * gridSize),
                end = Offset(width, y * gridSize),
                strokeWidth = 1f
            )
        }

        // Scanning Line
        drawLine(
            color = Color(0xFF80DEEA).copy(alpha = scanAlpha),
            start = Offset(0f, height * scanY),
            end = Offset(width, height * scanY),
            strokeWidth = 2f
        )
    }
}
