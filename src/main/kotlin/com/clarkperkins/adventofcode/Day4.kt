package com.clarkperkins.adventofcode

import kotlin.math.pow

data class ScratchCard(
    val winningNumbers: Set<Int>,
    val myNumbers: Set<Int>,
) {
    fun score(): Long {
        val how = myNumbers.intersect(winningNumbers).size
        return if (how > 0) {
            // gotta be a better way?
            2f.pow(how - 1).toLong()
        } else {
            0
        }
    }
}

class Day4 : Day<List<ScratchCard>, Long>() {
    override fun loadInputFromString(rawInput: String): List<ScratchCard> {
        return rawInput.trim().lines().map { line ->
            val (_, nums) = line.split(": ")
            val (winnings, mine) = nums.split(" | ")
            ScratchCard(
                winningNumbers = winnings.split(" ").filterNot { it.isEmpty() }.map { it.toInt() }.toSet(),
                myNumbers = mine.split(" ").filterNot { it.isEmpty() }.map { it.toInt() }.toSet(),
            )
        }
    }

    override fun loadTestInput(): String {
        return """
            Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
            Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
            Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
            Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
            Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
            Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 13L

    override fun doPart1(input: List<ScratchCard>): Long {
        return input.sumOf { it.score() }
    }

    override val part2TestExpectedOutput = 30L
    override fun doPart2(input: List<ScratchCard>): Long {
        val cardCounts = MutableList(input.size) { 1L }

        input.forEachIndexed { index, scratchCard ->
            scratchCard.winningNumbers.intersect(scratchCard.myNumbers).forEachIndexed { mIdx, _ ->
                // Increment this card count by the number of this card we have
                cardCounts[index + mIdx + 1] += cardCounts[index]
            }
        }

        return cardCounts.sum()
    }
}
