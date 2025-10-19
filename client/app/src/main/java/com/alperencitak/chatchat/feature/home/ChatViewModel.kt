package com.alperencitak.chatchat.feature.home

import androidx.lifecycle.ViewModel
import com.alperencitak.chatchat.feature.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val client: OkHttpClient
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val shift = 3
    private lateinit var webSocket: WebSocket
    private lateinit var username: String

    fun connectWebSocket(hostIp: String, username: String) {
        this.username = username
        val request = Request.Builder()
            .url("ws://$hostIp:12345/ws/chat")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val sender = json.getString("sender")
                    val encrypted = json.getString("message")
                    val decrypted = caesarDecrypt(encrypted)
                    _messages.value += ChatMessage(decrypted, sender)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        })
    }

    fun sendMessage(text: String) {
        val encrypted = caesarEncrypt(text)
        val json = JSONObject()
        json.put("sender", username)
        json.put("message", encrypted)

        webSocket.send(json.toString())
        _messages.value += ChatMessage(text, username)
    }

    private fun caesarEncrypt(text: String) = text.map {
        when {
            it.isUpperCase() -> 'A' + (it - 'A' + shift) % 26
            it.isLowerCase() -> 'a' + (it - 'a' + shift) % 26
            else -> it
        }
    }.joinToString("")

    private fun caesarDecrypt(text: String) = text.map {
        when {
            it.isUpperCase() -> 'A' + (it - 'A' + (26 - shift)) % 26
            it.isLowerCase() -> 'a' + (it - 'a' + (26 - shift)) % 26
            else -> it
        }
    }.joinToString("")
}

