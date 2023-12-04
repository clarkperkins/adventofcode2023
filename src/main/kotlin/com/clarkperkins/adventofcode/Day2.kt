package com.clarkperkins.adventofcode

import java.lang.IllegalArgumentException

data class Game(
    val id: Int,
    val turns: List<Turn>,
) {
    fun redMax() = turns.maxOf { it.red }
    fun greenMax() = turns.maxOf { it.green }
    fun blueMax() = turns.maxOf { it.blue }

    data class Turn(
        val red: Int,
        val green: Int,
        val blue: Int,
    )
}

class Day2 : Day<List<Game>, Long>() {
    override fun loadInputFromString(rawInput: String): List<Game> {
        return rawInput.trim().lineSequence().map {
            val (gameId, turnsString) = it.split(": ")
            val turns = turnsString.split("; ")
            Game(
                id = gameId.split(" ")[1].toInt(),
                turns = turns.map { turn ->
                    var red = 0
                    var green = 0
                    var blue = 0
                    turn.split(", ").forEach { c ->
                        val (count, color) = c.split(" ")
                        when (color) {
                            "red" -> red = count.toInt()
                            "green" -> green = count.toInt()
                            "blue" -> blue = count.toInt()
                            else -> throw IllegalArgumentException("unknown color $color")
                        }
                    }
                    Game.Turn(
                        red = red,
                        green = green,
                        blue = blue,
                    )
                }
            )
        }.toList()
    }

    override fun loadTestInput(): String {
        return """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 8L

    override fun doPart1(input: List<Game>): Long {
        return input.sumOf { game ->
            if (game.redMax() <= 12 && game.greenMax() <= 13 && game.blueMax() <= 14) {
                game.id.toLong()
            } else {
                0
            }
        }
    }

    override val part2TestExpectedOutput = 2286L

    override fun doPart2(input: List<Game>): Long {
        return input.sumOf { game ->
            (game.redMax() * game.greenMax() * game.blueMax()).toLong()
        }
    }
}
