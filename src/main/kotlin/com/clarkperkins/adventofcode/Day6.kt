package com.clarkperkins.adventofcode

data class Race(
    val allowedTime: Long,
    val distanceRecord: Long,
) {
    /**
     * I don't remember the proper mathematical term for this but the distances are symmetric with time t=0 and t=time both having 0 distance.
     * Time t and (allowedTime - t) always result in the same distance, and all times between t and (allowedTime - t) result in an equal or higher distance as time t.
     * So once we find the first time that beats the distance record, we can quickly compute the total wins based on the above logic.
     */
    fun winCount(): Long {
        for (t in 0..allowedTime) {
            val distance = t * (allowedTime - t)

            if (distance > distanceRecord) {
                return allowedTime - t - t + 1
            }
        }

        return 0
    }
}

class Day6 : Day<List<Race>, Long>() {
    override fun loadInputFromString(rawInput: String): List<Race> {
        val lines = rawInput.trim().lines()
        val times = lines[0]
            .removePrefix("Time: ")
            .split(" ")
            .filter { it.isNotEmpty() }
            .map { it.toLong() }
        val distances = lines[1]
            .removePrefix("Distance:")
            .split(" ")
            .filter { it.isNotEmpty() }
            .map { it.toLong() }

        return times.zip(distances).map { Race(it.first, it.second) }
    }

    override fun loadTestInput(): String {
        return """
            Time:      7  15   30
            Distance:  9  40  200
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 288L

    override fun doPart1(input: List<Race>): Long {
        return input.map(Race::winCount).reduce { a, b -> a * b }
    }

    override val part2TestExpectedOutput = 71503L

    override fun doPart2(input: List<Race>): Long {
        // probably coulda parsed this different/better, but eh
        val allowedTime = input.joinToString("") { it.allowedTime.toString() }.toLong()
        val distanceRecord = input.joinToString("") { it.distanceRecord.toString() }.toLong()

        return Race(allowedTime, distanceRecord).winCount()
    }
}
