package com.alperencitak.chatchat.algorithms

class PolybiusCipher : Algorithm {

    private val square = listOf(
        "ABCDE",
        "FGHIJ",
        "KLMNO",
        "PQRST",
        "UVWXY",
        "Z"
    )

    private val table = arrayOf(
        "ABCDE",
        "FGHIK",
        "LMNOP",
        "QRSTU",
        "VWXYZ"
    )

    override fun encrypt(text: String, shift: Int): String {
        val result = StringBuilder()

        for (char in text.uppercase()) {
            when (char) {
                'J' -> {
                    appendCoordinates('I', result)
                }
                in 'A'..'Z' -> {
                    appendCoordinates(char, result)
                }
                else -> {
                    result.append(char)
                }
            }
        }

        return result.toString()
    }

    private fun appendCoordinates(char: Char, result: StringBuilder) {
        for (row in table.indices) {
            val col = table[row].indexOf(char)
            if (col != -1) {
                result.append(row + 1)
                result.append(col + 1)
                result.append(" ")
                return
            }
        }
    }

    override fun decrypt(text: String, shift: Int): String {
        val result = StringBuilder()
        val parts = text.trim().split(" ")

        for (part in parts) {
            if (part.length == 2 && part.all { it.isDigit() }) {
                val row = part[0].digitToInt() - 1
                val col = part[1].digitToInt() - 1

                if (row in 0..4 && col in 0..4) {
                    result.append(table[row][col])
                }
            } else {
                result.append(part)
            }
        }

        return result.toString()
    }
}
