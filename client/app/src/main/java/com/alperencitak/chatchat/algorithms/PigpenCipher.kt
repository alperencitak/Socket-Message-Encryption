package com.alperencitak.chatchat.algorithms

class PigpenCipher : Algorithm {

    private val pigpenMap = mapOf(
        'A' to "┌┐", 'B' to "┐┘", 'C' to "└┘", 'D' to "└┐", 'E' to "┌┘", 'F' to "┼┼", 'G' to "•┌┐",
        'H' to "•┐┘", 'I' to "•└┘", 'K' to "•└┐", 'L' to "•┌┘", 'M' to "•┼┼", 'N' to "< >",
        'O' to "> <", 'P' to "< <", 'Q' to "> >", 'R' to "<>", 'S' to "><", 'T' to "•< >",
        'U' to "•> <", 'V' to "•< <", 'W' to "•> >", 'X' to "•<>", 'Y' to "•><", 'Z' to "O"
    )

    private val reversePigpenMap = pigpenMap.entries.associate { it.value to it.key }

    override fun encrypt(text: String, shift: Int): String {
        val result = StringBuilder()

        for (char in text.uppercase()) {
            if (char in pigpenMap) {
                result.append(pigpenMap[char])
                result.append(" ")
            } else {
                result.append(char)
            }
        }

        return result.toString().trim()
    }

    override fun decrypt(text: String, shift: Int): String {
        val parts = text.split(" ")
        val result = StringBuilder()

        for (symbol in parts) {
            result.append(reversePigpenMap[symbol] ?: symbol)
        }

        return result.toString()
    }
}
