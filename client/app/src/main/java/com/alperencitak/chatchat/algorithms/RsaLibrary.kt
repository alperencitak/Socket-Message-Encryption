package com.alperencitak.chatchat.algorithms

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class RsaLibrary : Algorithm {

    private val keySize = 2048
    private lateinit var publicKey: PublicKey
    private lateinit var privateKey: PrivateKey

    init {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(keySize)
        val pair = keyGen.generateKeyPair()
        publicKey = pair.public
        privateKey = pair.private
    }

    override fun encrypt(text: String, shift: Int): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val bytes = text.toByteArray(Charsets.UTF_8)
        val maxBlockSize = (keySize / 8) - 11
        val sb = StringBuilder()
        var offset = 0

        while (offset < bytes.size) {
            val end = (offset + maxBlockSize).coerceAtMost(bytes.size)
            val block = bytes.copyOfRange(offset, end)
            val encryptedBytes = cipher.doFinal(block)
            val cBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)

            if (sb.isNotEmpty()) sb.append("|")
            sb.append(cBase64)

            offset = end
        }
        return sb.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val parts = text.split("|")
        val outputStream = ByteArrayOutputStream()

        for (part in parts) {
            if (part.isEmpty()) continue
            try {
                val cBytes = Base64.decode(part, Base64.NO_WRAP)
                val decryptedBytes = cipher.doFinal(cBytes)
                outputStream.write(decryptedBytes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return outputStream.toString("UTF-8")
    }
}