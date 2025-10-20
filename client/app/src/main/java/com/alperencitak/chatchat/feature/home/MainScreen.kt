package com.alperencitak.chatchat.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(hostIp: String) {
    val chatViewModel: ChatViewModel = hiltViewModel()
    var username by remember { mutableStateOf("") }
    var joined by remember { mutableStateOf(false) }

    if (!joined) {
        LoginView(
            chatViewModel = chatViewModel,
            username = username,
            onLoginClick = {
                if (username.isNotBlank()) {
                    chatViewModel.connectWebSocket(hostIp, username)
                    joined = true
                }
            },
            onChangeUsername = {
                username = it
            }
        )
    } else {
        ChatView(
            chatViewModel = chatViewModel,
            username = username
        )
    }
}
