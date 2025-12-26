package com.alperencitak.chatchat.algorithms

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.SecureRandom

class Rsa : Algorithm {

    private val bitLength = 2048
    private var n: BigInteger
    private var e: BigInteger
    private var d: BigInteger

    init {
        val random = SecureRandom()
        val p = BigInteger.probablePrime(bitLength / 2, random)
        val q = BigInteger.probablePrime(bitLength / 2, random)
        n = p.multiply(q)
        val phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE))
        e = BigInteger("65537")
        d = e.modInverse(phi)
    }

    override fun encrypt(text: String, shift: Int): String {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val maxBlockSize = (bitLength / 8) - 11
        val sb = StringBuilder()
        var offset = 0

        while (offset < bytes.size) {
            val end = (offset + maxBlockSize).coerceAtMost(bytes.size)
            val block = bytes.copyOfRange(offset, end)
            val m = BigInteger(1, block)
            val c = m.modPow(e, n)
            val cBase64 = Base64.encodeToString(c.toByteArray(), Base64.NO_WRAP)
            
            if (sb.isNotEmpty()) sb.append("|")
            sb.append(cBase64)
            
            offset = end
        }
        return sb.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val parts = text.split("|")
        val outputStream = ByteArrayOutputStream()

        for (part in parts) {
            if (part.isEmpty()) continue
            try {
                val cBytes = Base64.decode(part, Base64.NO_WRAP)
                val c = BigInteger(1, cBytes)
                val m = c.modPow(d, n)
                var mBytes = m.toByteArray()
                
                if (mBytes.isNotEmpty() && mBytes[0] == 0.toByte()) {
                    mBytes = mBytes.copyOfRange(1, mBytes.size)
                }
                
                outputStream.write(mBytes)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return outputStream.toString("UTF-8")
    }

    fun encryptWithExternalKey(data: ByteArray, publicKeyStr: String): ByteArray {
        val cleanKey = publicKeyStr
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")
            .trim()

        val keyBytes = Base64.decode(cleanKey, Base64.DEFAULT)
        val x509KeySpec = java.security.spec.X509EncodedKeySpec(keyBytes)
        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        val pubKey = keyFactory.generatePublic(x509KeySpec)

        val cipher = javax.crypto.Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, pubKey)
        return cipher.doFinal(data)
    }
}