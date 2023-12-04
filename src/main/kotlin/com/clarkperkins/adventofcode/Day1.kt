package com.clarkperkins.adventofcode

class Day1 : Day<List<String>, Int>() {

    override fun loadInputFromString(rawInput: String): List<String> {
        return rawInput.trim().split("\n")
    }

    override fun loadTestInput(): String {
        return """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 142

    override fun doPart1(input: List<String>): Int {
        return input.sumOf { line ->
            val digits = line.filter { it.isDigit() }
            val calibrationValue = (digits[0].digitToInt() * 10) + (digits[digits.count() - 1].digitToInt())
            calibrationValue
        }
    }

    override fun loadTestInputPart2(): String {
        return """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()
    }

    override val part2TestExpectedOutput = 281

    private val conversions = mapOf(
        "one" to 1,
        "two" to 2,
        "three" to 3,
        "four" to 4,
        "five" to 5,
        "six" to 6,
        "seven" to 7,
        "eight" to 8,
        "nine" to 9,
        "1" to 1,
        "2" to 2,
        "3" to 3,
        "4" to 4,
        "5" to 5,
        "6" to 6,
        "7" to 7,
        "8" to 8,
        "9" to 9,
    )

    override fun doPart2(input: List<String>): Int {
        return input.sumOf { line ->
            val (_, firstDigit) = line.findAnyOf(conversions.keys)!!
            val (_, secondDigit) = line.findLastAnyOf(conversions.keys)!!

            val firstDigitInt = conversions[firstDigit]!!
            val secondDigitInt = conversions[secondDigit]!!

            firstDigitInt * 10 + secondDigitInt
        }
    }
}

