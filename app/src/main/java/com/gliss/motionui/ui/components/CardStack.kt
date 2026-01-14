package com.gliss.motionui.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun CardStack() {
    val items = remember { mutableStateListOf("Premium UI", "Fluid Motion", "Haptic Touch", "Clean Code") }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        items.asReversed().forEachIndexed { index, item ->
            key(item) {
                SwipeableCard(
                    text = item,
                    onSwiped = {
                        items.remove(item)
                    }
                )
            }
        }
        
        if (items.isEmpty()) {
            Text("Stack Empty", color = Color.Gray, fontSize = 18.sp)
        }
    }
}

@Composable
fun SwipeableCard(
    text: String,
    onSwiped: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    
    Box(
        modifier = Modifier
            .size(280.dp, 400.dp)
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = offsetX.value / 20f
                alpha = 1f - abs(offsetX.value) / 1000f
            }
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6200EE),
                        Color(0xFF3700B3)
                    )
                )
            )
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (abs(offsetX.value) > 300f) {
                                offsetX.animateTo(
                                    targetValue = if (offsetX.value > 0) 1000f else -1000f,
                                    animationSpec = tween(300)
                                )
                                onSwiped()
                            } else {
                                offsetX.animateTo(0f, spring(dampingRatio = 0.6f))
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
