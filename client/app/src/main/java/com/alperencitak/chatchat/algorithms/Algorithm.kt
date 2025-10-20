package com.alperencitak.chatchat.algorithms

interface Algorithm {

    fun encrypt(text: String, shift: Int): String

    fun decrypt(text: String, shift: Int): String

}