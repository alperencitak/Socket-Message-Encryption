package com.alperencitak.chatchat.algorithms

class AffineCipher(private val a: Int, private val b: Int) : Algorithm {

    private val alphabetLower = "abcdefghijklmnopqrstuvwxyz"
    private val alphabetUpper = alphabetLower.uppercase()

    private fun modInverse(a: Int, m: Int): Int {
        for (x in 1 until m) {
            if ((a * x) % m == 1) return x
        }
        throw IllegalArgumentException("a ($a) ve 26 aralarında asal olmalı! Tersi yok.")
    }

    override fun encrypt(text: String, shift: Int): String {
        val result = StringBuilder()

        for (ch in text) {
            when {
                ch.isLowerCase() -> {
                    val x = alphabetLower.indexOf(ch)
                    val encryptedIndex = (a * x + b) % 26
                    result.append(alphabetLower[encryptedIndex])
                }
                ch.isUpperCase() -> {
                    val x = alphabetUpper.indexOf(ch)
                    val encryptedIndex = (a * x + b) % 26
                    result.append(alphabetUpper[encryptedIndex])
                }
                else -> result.append(ch)
            }
        }
        return result.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        val aInverse = modInverse(a, 26)

        for (ch in text) {
            when {
                ch.isLowerCase() -> {
                    val y = alphabetLower.indexOf(ch)
                    val decryptedIndex = (aInverse * (y - b + 26)) % 26
                    result.append(alphabetLower[decryptedIndex])
                }
                ch.isUpperCase() -> {
                    val y = alphabetUpper.indexOf(ch)
                    val decryptedIndex = (aInverse * (y - b + 26)) % 26
                    result.append(alphabetUpper[decryptedIndex])
                }
                else -> result.append(ch)
            }
        }
        return result.toString()
    }
}
