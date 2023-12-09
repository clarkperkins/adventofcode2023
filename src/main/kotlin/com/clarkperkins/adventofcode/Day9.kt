package com.clarkperkins.adventofcode

class Day9 : Day<List<List<Int>>, Long>() {
    override fun loadInputFromString(rawInput: String): List<List<Int>> {
        return rawInput.trim().lines().map { line -> line.split(" ").map { it.toInt() } }
    }

    override fun loadTestInput(): String {
        return """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 114L

    private fun fillZeros(seq: List<Int>): List<List<Int>> {
        var curSeq = seq
        val allSeqs = mutableListOf(seq)

        while (!curSeq.all { it == 0 }) {
            val nextSeq = (1 until curSeq.size).map { curSeq[it] - curSeq[it - 1] }
            allSeqs.add(nextSeq)
            curSeq = nextSeq
        }

        return allSeqs
    }

    private fun findNextNumber(seq: List<Int>): Int {
        return fillZeros(seq).sumOf { it.last() }
    }

    override fun doPart1(input: List<List<Int>>): Long {
        return input.sumOf { findNextNumber(it).toLong() }
    }

    override val part2TestExpectedOutput = 2L

    private fun findPreviousNumber(seq: List<Int>): Int {
        var curFirst = 0

        fillZeros(seq).reversed().forEach { s ->
            curFirst = s.first() - curFirst
        }

        return curFirst
    }

    override fun doPart2(input: List<List<Int>>): Long {
        return input.sumOf { findPreviousNumber(it).toLong() }
    }
}
