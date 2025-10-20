package com.alperencitak.chatchat.algorithms.caesar

class Caesar: Algorithm {
    override fun encrypt(text: String, shift: Int): String {
        val encryptedText = text.map {
            when {
                it.isUpperCase() -> 'A' + (it - 'A' + shift) % 26
                it.isLowerCase() -> 'a' + (it - 'a' + shift) % 26
                else -> it
            }
        }.joinToString("")
        return encryptedText
    }

    override fun decrypt(text: String, shift: Int): String {
        val decryptedText = text.map {
            when {
                it.isUpperCase() -> 'A' + (it - 'A' + (26 - shift)) % 26
                it.isLowerCase() -> 'a' + (it - 'a' + (26 - shift)) % 26
                else -> it
            }
        }.joinToString("")
        return decryptedText
    }
}