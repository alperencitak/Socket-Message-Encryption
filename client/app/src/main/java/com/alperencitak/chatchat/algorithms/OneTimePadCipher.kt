package com.alperencitak.chatchat.algorithms

class OneTimePadCipher(private val key: String) : Algorithm {

    override fun encrypt(text: String, shift: Int): String {
        val cleanText = text.uppercase()
        val cleanKey = key.uppercase()

        if (cleanKey.length < cleanText.length) {
            throw IllegalArgumentException("key metinden kısa olamaz")
        }

        val result = StringBuilder()

        for (i in cleanText.indices) {
            val c = cleanText[i]

            if (c !in 'A'..'Z') {
                result.append(c)
                continue
            }

            val tVal = c - 'A'
            val kVal = cleanKey[i] - 'A'

            val encrypted = (tVal + kVal) % 26
            result.append((encrypted + 'A'.code).toChar())
        }

        return result.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val cleanText = text.uppercase()
        val cleanKey = key.uppercase()

        if (cleanKey.length < cleanText.length) {
            throw IllegalArgumentException("key metinden kısa olamaz")
        }

        val result = StringBuilder()

        for (i in cleanText.indices) {
            val c = cleanText[i]

            if (c !in 'A'..'Z') {
                result.append(c)
                continue
            }

            val tVal = c - 'A'
            val kVal = cleanKey[i] - 'A'

            val decrypted = (tVal - kVal + 26) % 26
            result.append((decrypted + 'A'.code).toChar())
        }

        return result.toString()
    }
}
