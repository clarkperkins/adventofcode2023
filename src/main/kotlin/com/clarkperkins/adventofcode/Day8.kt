package com.clarkperkins.adventofcode

data class Node(
    val id: String,
    val left: String,
    val right: String,
)

data class DesertMap(
    val directions: String,
    val nodes: Map<String, Node>,
) {
    fun nextNode(cur: String, dir: Char): String {
        return when (dir) {
            'L' -> nodes[cur]!!.left
            'R' -> nodes[cur]!!.right
            else -> throw IllegalStateException("Invalid direction: $dir")
        }
    }
}

private val nodeRegex = "([A-Z0-9]+) = \\(([A-Z0-9]+), ([A-Z0-9]+)\\)".toRegex()

class Day8 : Day<DesertMap, Long>() {
    override fun loadInputFromString(rawInput: String): DesertMap {
        val lines = rawInput.trim().lines()

        val directions = lines[0]

        val nodes = lines.subList(2, lines.size).map { line ->
            val m = nodeRegex.matchEntire(line)
            Node(
                id = m!!.groupValues[1],
                left = m.groupValues[2],
                right = m.groupValues[3],
            )
        }

        return DesertMap(
            directions,
            nodes.associateBy { it.id },
        )
    }

    override fun loadTestInput(): String {
        return """
            RL

            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 2L


    override fun doPart1(input: DesertMap): Long {
        var location = "AAA"
        var stepCount = 0L
        var dirIndex = 0

        while (location != "ZZZ") {
            val dir = input.directions[dirIndex]
            location = input.nextNode(location, dir)

            stepCount++
            dirIndex = (dirIndex + 1) % input.directions.length
        }

        return stepCount
    }

    override fun loadTestInputPart2(): String {
        return """
            LR

            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
        """.trimIndent()
    }

    override val part2TestExpectedOutput = 6L

    private fun findLCM(a: Long, b: Long): Long {
        val larger = if (a > b) a else b
        val maxLcm = a * b
        var lcm = larger
        while (lcm <= maxLcm) {
            if (lcm % a == 0L && lcm % b == 0L) {
                return lcm
            }
            lcm += larger
        }
        return maxLcm
    }

    /**
     * Assumptions / observations:
     *
     * In my input there are 6 starting locations. Each of the locations in the desert can be reached from
     * exactly one of the starting locations, creating 6 independent areas of the desert.
     * The directions lead through each section of desert in circles, landing on the ending point after every N steps.
     * Therefore, at 2N, 3N, 4N, 5N ... steps the ghost in that desert arrives at the same ending point again.
     *
     * Knowing this, we can figure out how many steps it takes to get to the end in each of the 6 deserts,
     * and find the LCM of those 6 numbers to arrive at the answer.
     */
    override fun doPart2(input: DesertMap): Long {
        return input.nodes.keys.filter { it.endsWith("A") }.map {
            var location = it
            var stepCount = 0L
            var dirIndex = 0

            while (!location.endsWith("Z")) {
                val dir = input.directions[dirIndex]
                location = input.nextNode(location, dir)

                stepCount++
                dirIndex = (dirIndex + 1) % input.directions.length
            }

            stepCount
        }.reduce { a, b -> findLCM(a, b) }
    }
}
