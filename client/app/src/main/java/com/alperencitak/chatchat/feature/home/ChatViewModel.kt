package com.alperencitak.chatchat.feature.home

import android.util.Log
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
import javax.crypto.KeyAgreement

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
    private var sessionDesKey: ByteArray? = null
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
                    val type = json.optString("type")

                    when (type) {
                        "handshake" -> {
                            val rsaPubKey = json.getString("rsa_public_key")
                            val eccPubKey = json.getString("ecc_public_key")
                            completeDoubleHandshake(rsaPubKey, eccPubKey)
                        }
                        "file" -> {
                            val fileName = json.getString("file_name")
                            val sender = json.getString("sender")
                            val encryptedBase64 = json.getString("message")
                            val encryptedBytes = android.util.Base64.decode(encryptedBase64, android.util.Base64.DEFAULT)

                            val currentAlgo = _algorithm.value
                            if (currentAlgo is AesLibrary) {
                                val decryptedBytes = currentAlgo.decryptBytes(encryptedBytes)
                                _messages.value += ChatMessage("[Dosya Alındı: $fileName]", sender)
                            }
                        }
                        "chat", "" -> {
                            val sender = json.getString("sender")
                            val encrypted = json.getString("message")
                            val decrypted = _algorithm.value?.decrypt(encrypted, shift) ?: encrypted
                            _messages.value += ChatMessage(decrypted, sender)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("ChatViewModel", "Bağlantı başarıyla kuruldu!")
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
                DesManual(sessionAesKey ?: aesKey)
            }
            "DES Library" -> {
                DesLibrary(sessionDesKey ?: desKey)
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

    fun sendFile(fileBytes: ByteArray, fileName: String) {
        val currentAlgo = _algorithm.value

        if (currentAlgo is AesLibrary) {
            val encryptedFile = currentAlgo.encryptBytes(fileBytes)
            val base64File = android.util.Base64.encodeToString(encryptedFile, android.util.Base64.NO_WRAP)

            val json = JSONObject().apply {
                put("sender", username)
                put("message", base64File)
                put("type", "file")
                put("file_name", fileName)
            }

            webSocket.send(json.toString())
            _messages.value += ChatMessage("[Dosya Gönderildi: $fileName]", username)
        }
    }

    private fun completeDoubleHandshake(rsaPubKeyStr: String, eccPubKeyStr: String) {
        try {
            val newAesKey = ByteArray(16).apply { java.security.SecureRandom().nextBytes(this) }
            this.sessionAesKey = newAesKey

            val encryptedAesKey = rsaProcessor.encryptWithExternalKey(newAesKey, rsaPubKeyStr)
            val rsaJson = JSONObject().apply {
                put("type", "rsa_key_exchange")
                put("encrypted_key", android.util.Base64.encodeToString(encryptedAesKey, android.util.Base64.NO_WRAP))
            }
            webSocket.send(rsaJson.toString())

            val eccResult = generateEccKeyAndSecret(eccPubKeyStr)
            this.sessionDesKey = eccResult.second

            val eccJson = JSONObject().apply {
                put("type", "ecc_key_exchange")
                put("public_key", eccResult.first)
            }
            webSocket.send(eccJson.toString())

            updateAllSessionAlgorithms()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateEccKeyAndSecret(serverPubKeyStr: String): Pair<String, ByteArray> {
        val cleanKey = serverPubKeyStr.replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "").replace("\n", "").trim()
        val serverKeyBytes = android.util.Base64.decode(cleanKey, android.util.Base64.DEFAULT)

        val keyFactory = java.security.KeyFactory.getInstance("EC")
        val serverPubKey = keyFactory.generatePublic(java.security.spec.X509EncodedKeySpec(serverKeyBytes))

        val kpg = java.security.KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        val myKeyPair = kpg.generateKeyPair()

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(myKeyPair.private)
        keyAgreement.doPhase(serverPubKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        val derivedDesKey = sharedSecret.copyOfRange(0, 8)
        val myPubKeyBase64 = android.util.Base64.encodeToString(myKeyPair.public.encoded, android.util.Base64.NO_WRAP)

        return Pair(myPubKeyBase64, derivedDesKey)
    }

    private fun updateAllSessionAlgorithms() {
        val current = _algorithm.value
        if (current is AesLibrary || current is AesManual) {
            _algorithm.value = if (current is AesLibrary) AesLibrary(sessionAesKey ?: aesKey) else AesManual(sessionAesKey ?: aesKey)
        }
        if (current is DesLibrary || current is DesManual) {
            _algorithm.value = if (current is DesLibrary) DesLibrary(sessionDesKey ?: desKey) else DesManual(sessionDesKey ?: desKey)
        }
    }
}
