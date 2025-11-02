package com.alperencitak.chatchat.algorithms

class RouteCipher(private val rows: Int, private val cols: Int) : Algorithm {

    override fun encrypt(text: String, shift: Int): String {
        val clean = text.replace(" ", "").lowercase()
        val padded = clean.padEnd(rows * cols, 'x')
        val matrix = Array(rows) { CharArray(cols) }
        var index = 0

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                matrix[r][c] = padded[index]
                index++
            }
        }

        val result = StringBuilder()
        var top = 0
        var bottom = rows - 1
        var left = 0
        var right = cols - 1

        while (top <= bottom && left <= right) {
            for (c in left..right) result.append(matrix[top][c])
            top++

            for (r in top..bottom) result.append(matrix[r][right])
            right--

            if (top <= bottom) {
                for (c in right downTo left) result.append(matrix[bottom][c])
                bottom--
            }

            if (left <= right) {
                for (r in bottom downTo top) result.append(matrix[r][left])
                left++
            }
        }

        return result.toString()
    }

    override fun decrypt(text: String, shift: Int): String {
        val clean = text.replace(" ", "").lowercase()
        val matrix = Array(rows) { CharArray(cols) { ' ' } }
        var top = 0
        var bottom = rows - 1
        var left = 0
        var right = cols - 1
        var index = 0

        while (top <= bottom && left <= right) {

            for (c in left..right) {
                matrix[top][c] = clean[index++]
            }
            top++

            for (r in top..bottom) {
                matrix[r][right] = clean[index++]
            }
            right--

            if (top <= bottom) {
                for (c in right downTo left) {
                    matrix[bottom][c] = clean[index++]
                }
                bottom--
            }

            if (left <= right) {
                for (r in bottom downTo top) {
                    matrix[r][left] = clean[index++]
                }
                left++
            }
        }

        val original = StringBuilder()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                original.append(matrix[r][c])
            }
        }

        return original.toString().replace("x", "")
    }
}
