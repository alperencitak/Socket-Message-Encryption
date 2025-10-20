package com.alperencitak.chatchat.feature.home

import androidx.lifecycle.ViewModel
import com.alperencitak.chatchat.algorithms.Algorithm
import com.alperencitak.chatchat.algorithms.Caesar
import com.alperencitak.chatchat.algorithms.HillCipher
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

    private val _algorithm = MutableStateFlow<Algorithm?>(null)
    val algorithm: StateFlow<Algorithm?> = _algorithm

    private val shift = 3
    private lateinit var webSocket: WebSocket
    private lateinit var username: String

    init {
        _algorithm.value = Caesar()
    }

    fun connectWebSocket(hostIp: String, username: String) {
        this.username = username
        val request = Request.Builder()
            .url("ws://$hostIp:12345/ws/chat")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    if(_algorithm.value != null){
                        val json = JSONObject(text)
                        val sender = json.getString("sender")
                        val encrypted = json.getString("message")
                        val decrypted = _algorithm.value!!.decrypt(encrypted, shift)
                        _messages.value += ChatMessage(decrypted, sender)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        })
    }

    fun changeAlgorithm(selectedAlgorithm: String){
        _algorithm.value = when(selectedAlgorithm){
            "Caesar" -> {
                Caesar()
            }
            "HillCipher" -> {
                val key = arrayOf(intArrayOf(3, 3), intArrayOf(2, 5))
                HillCipher(key = key)
            }
            else -> {
                Caesar()
            }
        }
    }

    fun sendMessage(text: String) {
        if(_algorithm.value != null){
            val encrypted = _algorithm.value!!.encrypt(text, shift)
            val json = JSONObject()
            json.put("sender", username)
            json.put("message", encrypted)

            webSocket.send(json.toString())
            _messages.value += ChatMessage(text, username)
        }
    }
}

