package com.alperencitak.chatchat.feature.home

import androidx.lifecycle.ViewModel
import com.alperencitak.chatchat.algorithms.AesLibrary
import com.alperencitak.chatchat.algorithms.AesManual
import com.alperencitak.chatchat.algorithms.AffineCipher
import com.alperencitak.chatchat.algorithms.Algorithm
import com.alperencitak.chatchat.algorithms.Caesar
import com.alperencitak.chatchat.algorithms.ColumnarTranspositionCipher
import com.alperencitak.chatchat.algorithms.DesLibrary
import com.alperencitak.chatchat.algorithms.DesManual
import com.alperencitak.chatchat.algorithms.HillCipher
import com.alperencitak.chatchat.algorithms.OneTimePadCipher
import com.alperencitak.chatchat.algorithms.PigpenCipher
import com.alperencitak.chatchat.algorithms.PlayfairCipher
import com.alperencitak.chatchat.algorithms.PolybiusCipher
import com.alperencitak.chatchat.algorithms.RailFenceCipher
import com.alperencitak.chatchat.algorithms.RouteCipher
import com.alperencitak.chatchat.algorithms.Rsa
import com.alperencitak.chatchat.algorithms.SubstitutionCipher
import com.alperencitak.chatchat.algorithms.VigenereCipher
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

    private val aesKey = "1234567890123456".toByteArray(Charsets.UTF_8)

    private val desKey = "12345678".toByteArray(Charsets.UTF_8)

    private var sessionAesKey: ByteArray? = null
    private val rsaProcessor = Rsa()

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
                    val json = JSONObject(text)

                    if (!json.has("type") || json.optString("type") == "chat") {
                        val sender = json.getString("sender")
                        val encrypted = json.getString("message")
                        val decrypted = _algorithm.value!!.decrypt(encrypted, shift)
                        _messages.value += ChatMessage(decrypted, sender)
                    } else if (json.getString("type") == "handshake") {
                        completeHandshake(json.getString("public_key"))
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
            "Substitution" -> {
                SubstitutionCipher()
            }
            "Vigenere" -> {
                VigenereCipher(key = "key")
            }
            "Affine" -> {
                AffineCipher(a = 5, b = 8)
            }
            "Playfair" -> {
                PlayfairCipher(key = "keyword")
            }
            "RailFence" -> {
                RailFenceCipher(3)
            }
            "Route" -> {
                RouteCipher(3,4)
            }
            "Columnar Transposition" -> {
                ColumnarTranspositionCipher()
            }
            "Polybius" -> {
                PolybiusCipher()
            }
            "Pigpen" -> {
                PigpenCipher()
            }
            "One Time Pad" -> {
                OneTimePadCipher("key")
            }
            "AES Manual" -> {
                AesManual(sessionAesKey ?: aesKey)
            }
            "AES Library" -> {
                AesLibrary(sessionAesKey ?: aesKey)
            }
            "DES Manual" -> {
                DesManual(desKey)
            }
            "DES Library" -> {
                DesLibrary(desKey)
            }
            "RSA Manual" -> {
                Rsa()
            }
            "RSA Library" -> {
                Rsa()
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

    private fun completeHandshake(publicKeyStr: String) {
        try {
            val newAesKey = ByteArray(16)
            java.security.SecureRandom().nextBytes(newAesKey)
            this.sessionAesKey = newAesKey

            val encryptedKeyBytes = rsaProcessor.encryptWithExternalKey(newAesKey, publicKeyStr)

            val base64Key = android.util.Base64.encodeToString(encryptedKeyBytes, android.util.Base64.NO_WRAP)

            val json = JSONObject()
            json.put("type", "key_exchange")
            json.put("encrypted_key", base64Key)

            webSocket.send(json.toString())

            updateAlgorithmsWithSessionKey(newAesKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateAlgorithmsWithSessionKey(key: ByteArray) {
        val currentAlgo = _algorithm.value
        if (currentAlgo is AesManual) {
            _algorithm.value = AesManual(key)
        } else if (currentAlgo is AesLibrary) {
            _algorithm.value = AesLibrary(key)
        }
    }
}

