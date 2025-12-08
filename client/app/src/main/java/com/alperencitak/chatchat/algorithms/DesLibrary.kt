package com.alperencitak.chatchat.algorithms

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class DesLibrary(
    private val keyBytes: ByteArray
) : Algorithm {

    init {
        require(keyBytes.size == 8)
    }

    private val transformation = "DES/ECB/PKCS5Padding"
    private val algorithmName = "DES"

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
}