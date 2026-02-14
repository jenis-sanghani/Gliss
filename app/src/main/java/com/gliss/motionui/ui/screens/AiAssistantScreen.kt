package com.gliss.motionui.ui.screens

import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.gliss.motionui.ai.AiViewModel
import com.gliss.motionui.ai.ChatMessage
import com.gliss.motionui.billing.PremiumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    navController: NavController,
    aiViewModel: AiViewModel = viewModel(),
    premiumViewModel: PremiumViewModel = viewModel()
) {
    val isPremium by premiumViewModel.isPremium.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Assistant", 
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp,
                        letterSpacing = 1.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!isPremium) {
                PremiumLockOverlay(onUpgradeClick = { navController.navigate("premium") })
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    if (isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }
                    items(aiViewModel.messages.reversed()) { message ->
                        ChatBubble(message)
                    }
                }

                ChatInput(
                    text = inputText,
                    onTextChange = { inputText = it },
                    onSend = {
                        aiViewModel.sendMessage(inputText)
                        inputText = ""
                    }
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) Color(0xFF6200EE) else Color(0xFF1C1C1E)
    val textColor = Color.White

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(horizontalAlignment = alignment as Alignment.Horizontal) {
            Text(
                text = if (message.isUser) "USER //" else "AI //",
                color = Color(0xFF80DEEA).copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Box(
                modifier = Modifier
                    .border(0.5.dp, Color(0xFF80DEEA).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = message.text, color = Color.White, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Color.Black,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Ask about Gliss gestures...", color = Color.Gray) },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(0.5.dp, Color(0xFF80DEEA).copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .border(0.5.dp, Color(0xFF80DEEA).copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, top = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("AI is typing...", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun PremiumLockOverlay(onUpgradeClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Cinematic Background
        LockBackgroundVideo(videoPath = "videos/2.mp4")
        
        // Translucent Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AI ASSISTANT",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Elevated Guidance",
                color = Color(0xFF80DEEA),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Unlock smart gesture guides and personal assistance by upgrading to Premium.",
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(64.dp))
            Button(
                onClick = onUpgradeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .border(1.dp, Color(0xFFB39DDB).copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("UPGRADE TO PREMIUM", color = Color.White, fontWeight = FontWeight.Medium, letterSpacing = 1.sp)
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun LockBackgroundVideo(videoPath: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
            volume = 0f
        }
    }

    DisposableEffect(videoPath) {
        val mediaItem = MediaItem.fromUri(Uri.parse("asset:///$videoPath"))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        onDispose {
            exoPlayer.release()
        }
    }

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
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}