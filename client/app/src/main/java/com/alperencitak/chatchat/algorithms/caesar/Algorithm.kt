package com.alperencitak.chatchat.algorithms.caesar

interface Algorithm {

    fun encrypt(text: String, shift: Int): String

    fun decrypt(text: String, shift: Int): String

}