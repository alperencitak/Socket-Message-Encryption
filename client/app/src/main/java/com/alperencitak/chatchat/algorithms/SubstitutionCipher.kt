package com.alperencitak.chatchat.algorithms

class SubstitutionCipher : Algorithm {

    private val plainTextAlphabet = "abcdefghijklmnopqrstuvwxyz"

    private fun getCipherChar(plainChar: Char, isUpper: Boolean): Char {
        val alphabet = if (isUpper) plainTextAlphabet.uppercase() else plainTextAlphabet
        val index = alphabet.indexOf(plainChar)
        if (index == -1) return plainChar

        return alphabet.reversed()[index]
    }

    override fun encrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        for (ch in text) {
            result.append(getCipherChar(ch, ch.isUpperCase()))
        }
        return result.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        for (ch in text) {
            result.append(getCipherChar(ch, ch.isUpperCase()))
        }
        return result.toString()
    }
}
