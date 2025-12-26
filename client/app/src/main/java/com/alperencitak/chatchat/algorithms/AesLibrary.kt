package com.alperencitak.chatchat.algorithms

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class AesLibrary(
    private val keyBytes: ByteArray
) : Algorithm {

    init {
        require(keyBytes.size == 16 || keyBytes.size == 24 || keyBytes.size == 32)
    }

    private val transformation = "AES/ECB/PKCS5Padding"
    private val algorithmName = "AES"

    override fun encrypt(text: String, shift: Int): String {
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(keyBytes, algorithmName)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        
        val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    }

    override fun decrypt(text: String, shift: Int): String {
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(keyBytes, algorithmName)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        
        val decodedBytes = Base64.decode(text, Base64.NO_WRAP)
        val decryptedBytes = cipher.doFinal(decodedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }

    fun encryptBytes(data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(keyBytes, algorithmName)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        return cipher.doFinal(data)
    }

    fun decryptBytes(encryptedData: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(transformation)
        val keySpec = SecretKeySpec(keyBytes, algorithmName)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        return cipher.doFinal(encryptedData)
    }

}