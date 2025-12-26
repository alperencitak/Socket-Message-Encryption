package com.alperencitak.chatchat.algorithms

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement

class Ecc {

    fun generateSharedSecret(serverPubKeyStr: String): Pair<String, ByteArray> {
        val cleanKey = serverPubKeyStr
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\n", "")
            .trim()

        val serverKeyBytes = Base64.decode(cleanKey, Base64.DEFAULT)
        val keyFactory = KeyFactory.getInstance("EC") // Elliptic Curve
        val serverPubKey = keyFactory.generatePublic(X509EncodedKeySpec(serverKeyBytes))

        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256)
        val myKeyPair = kpg.generateKeyPair()

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(myKeyPair.private)
        keyAgreement.doPhase(serverPubKey, true)

        val sharedSecret = keyAgreement.generateSecret()

        val desKey = sharedSecret.copyOfRange(0, 8)

        val myPubKeyBase64 = Base64.encodeToString(myKeyPair.public.encoded, Base64.NO_WRAP)

        return Pair(myPubKeyBase64, desKey)
    }
}