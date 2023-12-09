package com.clarkperkins.adventofcode

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult

sealed class Day<I, O> : CliktCommand() {
    private var internalTestMode = true

    val testMode get() = internalTestMode

    override fun run() {
        internalTestMode = true
        val testInputPart1 = loadInputFromString(loadTestInput())

        val part1TestActualOutput = doPart1(testInputPart1)
        if (part1TestActualOutput == part1TestExpectedOutput) {
            println("Part 1 test output matches")
            println()
        } else {
            println("Part 1 test output failed")
            println("  Expected: $part1TestExpectedOutput")
            println("  Actual:   $part1TestActualOutput")
            throw ProgramResult(1)
        }

        internalTestMode = false
        val realInput = loadInputFromString(loadRealInput())
        val part1RealOutput = doPart1(realInput)
        println("Part 1 Answer: $part1RealOutput")

        if (part2TestExpectedOutput == null) {
            return
        }

        println()

        internalTestMode = true
        val testInputPart2 = loadInputFromString(loadTestInputPart2())

        val part2TestActualOutput = doPart2(testInputPart2)
        if (part2TestActualOutput == part2TestExpectedOutput) {
            println("Part 2 test output matches")
            println()
        } else {
            println("Part 2 test output failed")
            println("  Expected: $part2TestExpectedOutput")
            println("  Actual:   $part2TestActualOutput")
            throw ProgramResult(1)
        }

        internalTestMode = false
        val realOutputPart2 = doPart2(realInput)
        println("Part 2 Answer: $realOutputPart2")
    }

    abstract fun loadInputFromString(rawInput: String): I

    abstract fun loadTestInput(): String

    open fun loadRealInput(): String {
        val inputFileName = "${this::class.simpleName?.lowercase()}.txt"

        val url = this::class.java.classLoader.getResource(inputFileName)
            ?: throw IllegalStateException("Cannot locate input file: $inputFileName")
        return url.readText().trimEnd()
    }


    abstract val part1TestExpectedOutput: O

    abstract fun doPart1(input: I): O

    open fun loadTestInputPart2() = loadTestInput()


    open val part2TestExpectedOutput: O? = null

    abstract fun doPart2(input: I): O
}
