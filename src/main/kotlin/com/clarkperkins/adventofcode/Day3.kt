package com.clarkperkins.adventofcode

import java.lang.IllegalStateException
import kotlin.math.max
import kotlin.math.min

class Day3 : Day<List<String>, Long>() {
    override fun loadInputFromString(rawInput: String): List<String> {
        return rawInput.trim().lines()
    }

    override fun loadTestInput(): String {
        return """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...$.*....
            .664.598..
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 4361L

    override fun doPart1(input: List<String>): Long {
        var sum = 0L

        input.forEachIndexed { rowIdx, line ->
            var curNum = 0
            var shouldCount = false

            line.forEachIndexed { colIdx, char ->
                if (char.isDigit()) {
                    if (!shouldCount) {
                        // figure out if we should be counting this number or not
                        (max(0, rowIdx - 1 ) .. min(input.size - 1, rowIdx + 1)).forEach { fRowIdx ->
                            (max(0, colIdx - 1 ) .. min(line.length - 1, colIdx + 1)).forEach { fColIdx ->
                                val inQuestion = input[fRowIdx][fColIdx]
                                if (!inQuestion.isDigit() && inQuestion != '.') {
                                    shouldCount = true
                                }
                            }
                        }
                    }
                    curNum *= 10
                    curNum += char.digitToInt()
                } else {
                    // reset
                    if (shouldCount) {
                        sum += curNum
                        shouldCount = false
                    }
                    curNum = 0
                }
            }

            if (shouldCount) {
                sum += curNum
            }
        }

        return sum
    }

    override val part2TestExpectedOutput = 467835L

    private fun findPartNumber(line: String, idx: Int): Int {
        if (!line[idx].isDigit()) {
            throw IllegalStateException("${line[idx]} is not a digit")
        }

        var baseNum = line[idx].toString()

        val left = line.substring(0, idx).reversed()

        var leftIdx = 0

        while (leftIdx < left.length) {
            if (left[leftIdx].isDigit()) {
                baseNum = "${left[leftIdx]}$baseNum"
                leftIdx++
            } else {
                break
            }
        }

        val right = line.substring(idx + 1, line.length)

        var rightIdx = 0

        while (rightIdx < right.length) {
            if (right[rightIdx].isDigit()) {
                baseNum = "$baseNum${right[rightIdx]}"
                rightIdx++
            } else {
                break
            }
        }

        return baseNum.toInt()
    }


    override fun doPart2(input: List<String>): Long {
        var sum = 0L

        input.forEachIndexed { rowIdx, line ->
            line.forEachIndexed { colIdx, char ->
                if (char == '*') {
                    // We have a potential gear
                    val partNumbers = mutableListOf<Int>()

                    if (rowIdx > 0) {
                        val prevLine = input[rowIdx - 1]
                        if (prevLine[colIdx].isDigit()) {
                            // There's only 1 adjacent PN on the prev line
                            partNumbers.add(findPartNumber(prevLine, colIdx))
                        } else {
                            // there might be 2
                            if (colIdx > 0 && prevLine[colIdx - 1].isDigit()) {
                                partNumbers.add(findPartNumber(prevLine, colIdx - 1))
                            }
                            if (colIdx < prevLine.lastIndex && prevLine[colIdx + 1].isDigit()) {
                                partNumbers.add(findPartNumber(prevLine, colIdx + 1))
                            }
                        }
                    }

                    if (colIdx > 0 && line[colIdx - 1].isDigit()) {
                        partNumbers.add(findPartNumber(line, colIdx - 1))
                    }

                    if (colIdx < line.lastIndex && line[colIdx + 1].isDigit()) {
                        partNumbers.add(findPartNumber(line, colIdx + 1))
                    }

                    if (rowIdx < input.size - 1) {
                        val nextLine = input[rowIdx + 1]
                        if (nextLine[colIdx].isDigit()) {
                            // There's only 1 adjacent PN on the next line
                            partNumbers.add(findPartNumber(nextLine, colIdx))
                        } else {
                            // there might be 2
                            if (colIdx > 0 && nextLine[colIdx - 1].isDigit()) {
                                partNumbers.add(findPartNumber(nextLine, colIdx - 1))
                            }
                            if (colIdx < nextLine.lastIndex && nextLine[colIdx + 1].isDigit()) {
                                partNumbers.add(findPartNumber(nextLine, colIdx + 1))
                            }
                        }
                    }

                    if (partNumbers.size == 2) {
                        sum += partNumbers[0] * partNumbers[1]
                    }
                }
            }
        }

        return sum
    }
}
