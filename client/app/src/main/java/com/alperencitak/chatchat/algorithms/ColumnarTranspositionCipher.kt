package com.alperencitak.chatchat.algorithms

class ColumnarTranspositionCipher : Algorithm {

    override fun encrypt(text: String, shift: Int): String {
        if (shift <= 1) return text

        val rows = (text.length + shift - 1) / shift
        val matrix = CharArray(rows * shift) { ' ' }

        for (i in text.indices) {
            matrix[i] = text[i]
        }

        val encrypted = StringBuilder()
        for (col in 0 until shift) {
            for (row in 0 until rows) {
                encrypted.append(matrix[row * shift + col])
            }
        }

        return encrypted.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        if (shift <= 1) return text

        val rows = (text.length + shift - 1) / shift
        val matrix = CharArray(rows * shift) { ' ' }

        var index = 0

        for (col in 0 until shift) {
            for (row in 0 until rows) {
                if (index < text.length) {
                    matrix[row * shift + col] = text[index++]
                }
            }
        }

        val decrypted = StringBuilder()
        for (i in text.indices) {
            decrypted.append(matrix[i])
        }

        return decrypted.toString()
    }
}
