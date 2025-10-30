package com.alperencitak.chatchat.algorithms

class PlayfairCipher(private val key: String) : Algorithm {

    private val table = Array(5) { CharArray(5) }
    private val alphabet = "ABCDEFGHIKLMNOPQRSTUVWXYZ"

    init {
        buildTable()
    }

    private fun buildTable() {
        val used = mutableSetOf<Char>()
        val keyUpper = key.uppercase().replace("J", "I")
        var row = 0
        var col = 0

        fun addChar(c: Char) {
            if (c !in used && c in alphabet) {
                table[row][col] = c
                used.add(c)
                col++
                if (col == 5) { col = 0; row++ }
            }
        }

        for (c in keyUpper) addChar(c)
        for (c in alphabet) addChar(c)
    }

    private fun findChar(c: Char): Pair<Int, Int> {
        for (i in 0..4) {
            for (j in 0..4) {
                if (table[i][j] == c) return i to j
            }
        }
        throw Exception("Char not in table")
    }

    private fun prepareText(text: String): List<Pair<Char, Char>> {
        val clean = text.uppercase()
            .replace("J", "I")
            .filter { it in 'A'..'Z' }

        val pairs = mutableListOf<Pair<Char, Char>>()
        var i = 0

        while (i < clean.length) {
            val a = clean[i]
            val b = if (i+1 < clean.length) clean[i+1] else 'X'

            if (a == b) {
                pairs.add(a to 'X')
                i++
            } else {
                pairs.add(a to b)
                i += 2
            }
        }

        return pairs
    }

    override fun encrypt(text: String, shift: Int): String {
        val pairs = prepareText(text)
        val result = StringBuilder()

        for ((a, b) in pairs) {
            val (r1, c1) = findChar(a)
            val (r2, c2) = findChar(b)

            when {
                r1 == r2 -> {
                    result.append(table[r1][(c1 + 1) % 5])
                    result.append(table[r2][(c2 + 1) % 5])
                }
                c1 == c2 -> {
                    result.append(table[(r1 + 1) % 5][c1])
                    result.append(table[(r2 + 1) % 5][c2])
                }
                else -> {
                    result.append(table[r1][c2])
                    result.append(table[r2][c1])
                }
            }
        }

        return result.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val clean = text.uppercase().filter { it in 'A'..'Z' }
        val result = StringBuilder()

        for (i in clean.indices step 2) {
            val a = clean[i]
            val b = clean[i + 1]
            val (r1, c1) = findChar(a)
            val (r2, c2) = findChar(b)

            when {
                r1 == r2 -> {
                    result.append(table[r1][(c1 + 4) % 5])
                    result.append(table[r2][(c2 + 4) % 5])
                }
                c1 == c2 -> {
                    result.append(table[(r1 + 4) % 5][c1])
                    result.append(table[(r2 + 4) % 5][c2])
                }
                else -> {
                    result.append(table[r1][c2])
                    result.append(table[r2][c1])
                }
            }
        }

        return result.toString()
    }
}
