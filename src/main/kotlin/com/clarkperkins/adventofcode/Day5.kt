package com.clarkperkins.adventofcode

import kotlin.math.max
import kotlin.math.min

data class RangeSet(
    val source: LongRange,
    val dest: LongRange,
) {
    constructor(
        destStart: Long,
        sourceStart: Long,
        length: Long,
    ) : this(
        sourceStart..<sourceStart + length,
        destStart..<destStart + length,
    )

    fun convert(num: Long): Long? {
        return if (num in source) {
            dest.first + (num - source.first)
        } else {
            null
        }
    }

    fun convertRange(range: LongRange): LongRange? {
        return if (source.first <= range.last && range.first <= source.last) {
            val start = maxOf(range.first, source.first)
            val end = minOf(range.last, source.last)

            convert(start)!!..convert(end)!!
        } else {
            null
        }
    }
}

fun List<RangeSet>.convert(num: Long): Long {
    for (i in this) {
        val t = i.convert(num)
        if (t != null) {
            return t
        }
    }
    return num
}

fun List<RangeSet>.convertRange(range: LongRange): List<LongRange> {
    return mapNotNull { it.convertRange(range) }
}

fun List<RangeSet>.convertRanges(ranges: List<LongRange>): List<LongRange> {
    return ranges.flatMap { convertRange(it) }
}

data class Mappings(
    val seeds: List<Long>,
    val ranges: Map<String, List<RangeSet>>,
) {
    fun seedToLocation(seed: Long): Long {
        val soil = ranges["seed-to-soil"]!!.convert(seed)
        val fertilizer = ranges["soil-to-fertilizer"]!!.convert(soil)
        val water = ranges["fertilizer-to-water"]!!.convert(fertilizer)
        val light = ranges["water-to-light"]!!.convert(water)
        val temperature = ranges["light-to-temperature"]!!.convert(light)
        val humidity = ranges["temperature-to-humidity"]!!.convert(temperature)
        return ranges["humidity-to-location"]!!.convert(humidity)
    }

    fun seedRangeToLocationRanges(seedRanges: List<LongRange>): List<LongRange> {
        val soilRanges = ranges["seed-to-soil"]!!.convertRanges(seedRanges)
        val fertilizerRanges = ranges["soil-to-fertilizer"]!!.convertRanges(soilRanges)
        val waterRanges = ranges["fertilizer-to-water"]!!.convertRanges(fertilizerRanges)
        val lightRanges = ranges["water-to-light"]!!.convertRanges(waterRanges)
        val temperatureRanges = ranges["light-to-temperature"]!!.convertRanges(lightRanges)
        val humidityRanges = ranges["temperature-to-humidity"]!!.convertRanges(temperatureRanges)
        return ranges["humidity-to-location"]!!.convertRanges(humidityRanges)
    }
}

class Day5 : Day<Mappings, Long>() {
    override fun loadInputFromString(rawInput: String): Mappings {
        var seeds = emptyList<Long>()
        val ranges = mutableMapOf<String, MutableList<RangeSet>>()
        var currentRange = ""
        rawInput.trim().lineSequence().forEach { line ->
            if (line.startsWith("seeds: ")) {
                seeds = line.removePrefix("seeds: ").split(" ").map { it.toLong() }
            } else if (line.endsWith(" map:")) {
                currentRange = line.removeSuffix(" map:")
                ranges[currentRange] = mutableListOf()
            } else if (line.isNotEmpty()) {
                val nums = line.split(" ").map { it.toLong() }
                if (nums.size != 3) {
                    throw IllegalStateException("BAD")
                }
                ranges[currentRange]!!.add(
                    RangeSet(nums[0], nums[1], nums[2])
                )
            }
        }

        // determine the absolute min/max values across all the ranges to be used for filling gaps.
        val minVal = ranges.flatMap { rangeList -> rangeList.value.map { min(it.source.first, it.dest.first) } }.min()
        val maxVal = ranges.flatMap { rangeList -> rangeList.value.map { max(it.source.last, it.dest.last) } }.max()

        // fill gaps.
        // some of the X-to-Y ranges don't cover the entire range from min -> max, so we'll fill in those gaps here
        // with source range mapping to an identical dest range (per the problem statement)
        // I decided it was easier to add the missing ranges here rather than account for them when actually mapping the ranges.
        ranges.forEach { (name, rangeSet) ->
            var cur = minVal
            val sorted = rangeSet.sortedBy { it.source.first }

            val extra = mutableListOf<RangeSet>()

            sorted.forEach { toCheck ->
                if (cur < toCheck.source.first) {
                    val newRange = cur..<toCheck.source.first
                    extra.add(RangeSet(newRange, newRange))
                }
                cur = toCheck.source.last + 1
            }

            if (cur < maxVal) {
                val newRange = cur..maxVal
                extra.add(RangeSet(newRange, newRange))
            }


            ranges[name]!!.addAll(extra)
        }

        return Mappings(
            seeds = seeds,
            ranges = ranges.mapValues { (_, v) -> v.sortedBy { it.source.first } },
        )
    }

    override fun loadTestInput(): String {
        return """
            seeds: 79 14 55 13

            seed-to-soil map:
            50 98 2
            52 50 48

            soil-to-fertilizer map:
            0 15 37
            37 52 2
            39 0 15

            fertilizer-to-water map:
            49 53 8
            0 11 42
            42 0 7
            57 7 4

            water-to-light map:
            88 18 7
            18 25 70

            light-to-temperature map:
            45 77 23
            81 45 19
            68 64 13

            temperature-to-humidity map:
            0 69 1
            1 0 69

            humidity-to-location map:
            60 56 37
            56 93 4
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 35L

    override fun doPart1(input: Mappings): Long {
        return input.seeds.minOf { seed ->
            input.seedToLocation(seed)
        }
    }

    override val part2TestExpectedOutput = 46L

    override fun doPart2(input: Mappings): Long {
        val seedRanges = input.seeds.chunked(2).map { (start, length) ->
            start..<start + length
        }

        val f = input.seedRangeToLocationRanges(seedRanges)
        return f.minOf { it.first }
    }
}
