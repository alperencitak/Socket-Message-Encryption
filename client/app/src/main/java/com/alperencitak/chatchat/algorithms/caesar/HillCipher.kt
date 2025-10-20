package com.alperencitak.chatchat.algorithms.caesar

class HillCipher(
    private val key: Array<IntArray>
) : Algorithm {

    private val modulus = 26

    private fun charToInt(c: Char): Int = if (c.isUpperCase()) c - 'A' else c - 'a'

    private fun intToChar(i: Int, isUpper: Boolean): Char =
        if (isUpper) ('A'.code + (i % modulus + modulus) % modulus).toChar()
        else ('a'.code + (i % modulus + modulus) % modulus).toChar()

    private fun textToPairs(text: String): List<String> {
        val cleanText = text.replace("\\s".toRegex(), "")
        val padded = if (cleanText.length % 2 != 0) cleanText + "X" else cleanText
        return padded.chunked(2)
    }

    override fun encrypt(text: String, shift: Int): String {
        val pairs = textToPairs(text)
        val encrypted = StringBuilder()

        for (pair in pairs) {
            val isUpper0 = pair[0].isUpperCase()
            val isUpper1 = pair[1].isUpperCase()

            val vector = intArrayOf(charToInt(pair[0]), charToInt(pair[1]))
            val result = IntArray(2)

            for (i in 0..1) {
                result[i] = (key[i][0] * vector[0] + key[i][1] * vector[1]) % modulus
            }

            encrypted.append(intToChar(result[0], isUpper0))
            encrypted.append(intToChar(result[1], isUpper1))
        }

        return encrypted.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val pairs = textToPairs(text)
        val decrypted = StringBuilder()

        val inverseKey = invertKeyMatrix()

        for (pair in pairs) {
            val isUpper0 = pair[0].isUpperCase()
            val isUpper1 = pair[1].isUpperCase()

            val vector = intArrayOf(charToInt(pair[0]), charToInt(pair[1]))
            val result = IntArray(2)

            for (i in 0..1) {
                result[i] = (inverseKey[i][0] * vector[0] + inverseKey[i][1] * vector[1]) % modulus
            }

            decrypted.append(intToChar(result[0], isUpper0))
            decrypted.append(intToChar(result[1], isUpper1))
        }

        return decrypted.toString()
    }

    private fun invertKeyMatrix(): Array<IntArray> {
        val det = (key[0][0] * key[1][1] - key[0][1] * key[1][0]) % modulus
        val detInv = modInverse(det, modulus)

        val inverse = Array(2) { IntArray(2) }
        inverse[0][0] = (key[1][1] * detInv) % modulus
        inverse[0][1] = (-key[0][1] * detInv) % modulus
        inverse[1][0] = (-key[1][0] * detInv) % modulus
        inverse[1][1] = (key[0][0] * detInv) % modulus

        for (i in 0..1) {
            for (j in 0..1) {
                inverse[i][j] = (inverse[i][j] + modulus) % modulus
            }
        }

        return inverse
    }

    private fun modInverse(a: Int, m: Int): Int {
        for (x in 1 until m) {
            if ((a * x) % m == 1) return x
        }
        throw IllegalArgumentException("tersi yok")
    }
}
