package com.alperencitak.chatchat.algorithms

class VigenereCipher(private val key: String): Algorithm {

    private val alphabet = "abcdefghijklmnopqrstuvwxyz"
    private val alphabetUpper = alphabet.uppercase()

    override fun encrypt(text: String, shift: Int): String {
        val cipherText = StringBuilder()

        for (i in text.indices) {
            val ch = text[i]

            when {
                ch.isLowerCase() -> {
                    val plainIndex = alphabet.indexOf(ch)
                    val keyIndex = alphabet.indexOf(key[i % key.length].lowercaseChar())
                    cipherText.append(alphabet[(plainIndex + keyIndex) % 26])
                }
                ch.isUpperCase() -> {
                    val plainIndex = alphabetUpper.indexOf(ch)
                    val keyIndex = alphabetUpper.indexOf(key[i % key.length].uppercaseChar())
                    cipherText.append(alphabetUpper[(plainIndex + keyIndex) % 26])
                }
                else -> cipherText.append(ch)
            }
        }

        return cipherText.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val plainText = StringBuilder()

        for (i in text.indices) {
            val ch = text[i]

            when {
                ch.isLowerCase() -> {
                    val cipherIndex = alphabet.indexOf(ch)
                    val keyIndex = alphabet.indexOf(key[i % key.length].lowercaseChar())
                    plainText.append(alphabet[(cipherIndex - keyIndex + 26) % 26])
                }
                ch.isUpperCase() -> {
                    val cipherIndex = alphabetUpper.indexOf(ch)
                    val keyIndex = alphabetUpper.indexOf(key[i % key.length].uppercaseChar())
                    plainText.append(alphabetUpper[(cipherIndex - keyIndex + 26) % 26])
                }
                else -> plainText.append(ch)
            }
        }

        return plainText.toString()
    }
}
