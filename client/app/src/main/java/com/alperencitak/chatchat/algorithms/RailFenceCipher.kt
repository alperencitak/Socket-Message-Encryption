package com.alperencitak.chatchat.algorithms

class RailFenceCipher(private val rails: Int) : Algorithm {

    override fun encrypt(text: String, shift: Int): String {
        if (rails <= 1) return text

        val fence = Array(rails) { StringBuilder() }
        var rail = 0
        var directionDown = true

        for (char in text.replace(" ", "").lowercase()) {
            fence[rail].append(char)

            rail += if (directionDown) 1 else -1

            if (rail == 0 || rail == rails - 1)
                directionDown = !directionDown
        }

        return fence.joinToString("") { it.toString() }
    }

    override fun decrypt(text: String, shift: Int): String {
        if (rails <= 1) return text

        val len = text.length
        val fence = Array(rails) { CharArray(len) { '\n' } }
        var rail = 0
        var directionDown = true

        for (i in 0 until len) {
            fence[rail][i] = '*'

            rail += if (directionDown) 1 else -1
            if (rail == 0 || rail == rails - 1)
                directionDown = !directionDown
        }

        var index = 0
        for (r in 0 until rails) {
            for (c in 0 until len) {
                if (fence[r][c] == '*' && index < len) {
                    fence[r][c] = text[index]
                    index++
                }
            }
        }

        val result = StringBuilder()
        rail = 0
        directionDown = true

        for (i in 0 until len) {
            result.append(fence[rail][i])

            rail += if (directionDown) 1 else -1
            if (rail == 0 || rail == rails - 1)
                directionDown = !directionDown
        }

        return result.toString()
    }
}
