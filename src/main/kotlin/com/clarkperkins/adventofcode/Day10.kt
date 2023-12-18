package com.clarkperkins.adventofcode

data class Point(
    val x: Int,
    val y: Int,
)

data class LoopInfo(
    val loop: Set<Point>,
    val right: Set<Point>,
)

enum class Square(
    val inputChar: Char,
    val printChar: Char,
) {
    PIPE_HORIZONTAL('-', '─'),
    PIPE_VERTICAL('|', '│'),
    PIPE_NE('L', '└'),
    PIPE_NW('J', '┘'),
    PIPE_SW('7', '┐'),
    PIPE_SE('F', '┌'),
    GROUND('.', '.'),
    START('S', 'S');

    /**
     * Compute the next point given the prev/cur points
     */
    fun nextPoint(prev: Point, cur: Point): Point {
        return when (this) {
            PIPE_HORIZONTAL -> {
                assert(prev.x == cur.x)
                when (prev.y) {
                    cur.y - 1 -> Point(cur.x, cur.y + 1)
                    cur.y + 1 -> Point(cur.x, cur.y - 1)
                    else -> throw IllegalStateException("Invalid horizontal pipe")
                }
            }

            PIPE_VERTICAL -> {
                assert(prev.y == cur.y)
                when (prev.x) {
                    cur.x - 1 -> Point(cur.x + 1, cur.y)
                    cur.x + 1 -> Point(cur.x - 1, cur.y)
                    else -> throw IllegalStateException("Invalid vertical pipe")
                }
            }

            PIPE_NE -> {
                when {
                    prev.x == cur.x -> Point(cur.x - 1, cur.y)
                    prev.y == cur.y -> Point(cur.x, cur.y + 1)
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_NW -> {
                when {
                    prev.x == cur.x -> Point(cur.x - 1, cur.y)
                    prev.y == cur.y -> Point(cur.x, cur.y - 1)
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_SW -> {
                when {
                    prev.x == cur.x -> Point(cur.x + 1, cur.y)
                    prev.y == cur.y -> Point(cur.x, cur.y - 1)
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_SE -> {
                when {
                    prev.x == cur.x -> Point(cur.x + 1, cur.y)
                    prev.y == cur.y -> Point(cur.x, cur.y + 1)
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            GROUND -> throw IllegalStateException("should never arrive at ground")
            START -> throw IllegalStateException("should never arrive at start")
        }
    }

    /**
     * Collect the points that we pass "on the right" as we move from prev -> cur
     */
    fun rightPoints(prev: Point, cur: Point): Set<Point> {
        return when (this) {
            PIPE_HORIZONTAL -> {
                assert(prev.x == cur.x)
                when (prev.y) {
                    cur.y - 1 -> setOf(Point(cur.x + 1, cur.y))
                    cur.y + 1 -> setOf(Point(cur.x - 1, cur.y))
                    else -> throw IllegalStateException("Invalid horizontal pipe")
                }
            }

            PIPE_VERTICAL -> {
                assert(prev.y == cur.y)
                when (prev.x) {
                    cur.x - 1 -> setOf(Point(cur.x, cur.y - 1))
                    cur.x + 1 -> setOf(Point(cur.x, cur.y + 1))
                    else -> throw IllegalStateException("Invalid vertical pipe")
                }
            }

            PIPE_NE -> {
                when {
                    prev.x == cur.x -> emptySet()
                    prev.y == cur.y -> setOf(
                        Point(cur.x, cur.y - 1),
                        Point(cur.x + 1, cur.y - 1),
                        Point(cur.x + 1, cur.y),
                    )

                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_NW -> {
                when {
                    prev.x == cur.x -> setOf(
                        Point(cur.x + 1, cur.y),
                        Point(cur.x + 1, cur.y + 1),
                        Point(cur.x, cur.y + 1),
                    )

                    prev.y == cur.y -> emptySet()
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_SW -> {
                when {
                    prev.x == cur.x -> emptySet()
                    prev.y == cur.y -> setOf(
                        Point(cur.x, cur.y + 1),
                        Point(cur.x - 1, cur.y + 1),
                        Point(cur.x - 1, cur.y),
                    )

                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            PIPE_SE -> {
                when {
                    prev.x == cur.x -> setOf(
                        Point(cur.x - 1, cur.y),
                        Point(cur.x - 1, cur.y - 1),
                        Point(cur.x, cur.y - 1),
                    )

                    prev.y == cur.y -> emptySet()
                    else -> throw IllegalStateException("$inputChar out of line")
                }
            }

            GROUND -> throw IllegalStateException("should never arrive at ground")
            START -> throw IllegalStateException("should never arrive at start")
        }
    }
}


fun Array<Array<Square>>.asPrintableString(info: LoopInfo): String {
    joinToString("\n") { line -> line.joinToString("") { it.printChar.toString() } }

    var printStr = ""

    forEachIndexed { row, line ->
        line.forEachIndexed { col, square ->
            val p = Point(row, col)
            if (p in info.loop) {
                printStr += "\u001B[32m${square.printChar}\u001B[0m"
            } else if (p in info.right) {
                printStr += "\u001B[31m.\u001B[0m"
            } else {
                printStr += '.'
            }
        }
        printStr += "\n"
    }

    return printStr
}


fun Array<Array<Square>>.startPoint(): Point {
    forEachIndexed { x, line ->
        line.forEachIndexed { y, sq ->
            if (sq == Square.START) {
                return Point(x, y)
            }
        }
    }

    throw IllegalStateException("No start point")
}


class Day10 : Day<Array<Array<Square>>, Long>() {
    override fun loadInputFromString(rawInput: String): Array<Array<Square>> {
        return rawInput.trim().lines().map { line ->
            line.map { sq ->
                Square.entries.find { it.inputChar == sq } ?: throw IllegalStateException("Bad char: $sq")
            }.toTypedArray()
        }.toTypedArray()
    }

    override fun loadTestInput(): String {
        return """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 8L


    private fun findLoop(input: Array<Array<Square>>): LoopInfo {
        val start = input.startPoint()

        val toTry = listOf(
            Point(start.x, start.y + 1),
            Point(start.x + 1, start.y),
            Point(start.x - 1, start.y),
            Point(start.x, start.y - 1),
        )

        toTry.forEach { p ->
            val loop = mutableSetOf(start, p)
            val right = mutableSetOf<Point>()

            try {
                right.addAll(input[p.x][p.y].rightPoints(start, p))

                var prev = start
                var cur = p

                while (cur != start) {
                    val next = input[cur.x][cur.y].nextPoint(prev, cur)

                    prev = cur
                    cur = next
                    loop.add(next)
                    try {
                        right.addAll(input[cur.x][cur.y].rightPoints(prev, cur))
                    } catch (e: Exception) {
                    }
                }

                return LoopInfo(loop, right - loop)
            } catch (e: Exception) {
                // try the next one
            }
        }

        throw IllegalStateException("No solution")
    }

    override fun doPart1(input: Array<Array<Square>>): Long {
        return findLoop(input).loop.size.toLong() / 2
    }

    override fun loadTestInputPart2(): String {
        return """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent()
    }

    override val part2TestExpectedOutput = 4L


    override fun doPart2(input: Array<Array<Square>>): Long {
        val info = findLoop(input)

        println(input.asPrintableString(info))

        // This DOES NOT get the right answer...
        // but using the pretty printed graph above I was able to count the number of white dots
        // in the middle of the blob and add them to this output to get the right answer
        // (yes this is sketchy, but it got the right answer)
        return info.right.size.toLong()
    }
}
