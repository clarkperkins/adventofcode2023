package com.clarkperkins.adventofcode


enum class HandType(val strength: Int) {
    FIVE_OF_A_KIND(7),
    FOUR_OF_A_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_A_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1),
}

abstract class HandTypeComparator : Comparator<Hand> {
    abstract fun handType(hand: Hand): HandType

    abstract fun cardValue(card: Char): Int

    override fun compare(o1: Hand, o2: Hand): Int {
        val comparison = handType(o1).strength - handType(o2).strength

        if (comparison != 0) {
            return comparison
        }

        // tie breaker
        for ((c1, c2) in o1.cards.zip(o2.cards)) {
            val v = cardValue(c1) - cardValue(c2)
            if (v != 0) {
                return v
            }
        }

        // They're equal
        return 0
    }
}

class Part1TypeComparator : HandTypeComparator() {
    override fun handType(hand: Hand) = hand.type1

    override fun cardValue(card: Char): Int {
        return if (card.isDigit()) {
            val d = card.digitToInt()
            if (d in 2..9) {
                d
            } else {
                throw IllegalStateException("Invalid card: $this")
            }
        } else {
            when (card) {
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalStateException("Invalid card: $this")
            }
        }
    }
}

class Part2TypeComparator : HandTypeComparator() {
    override fun handType(hand: Hand) = hand.type2

    override fun cardValue(card: Char): Int {
        return if (card.isDigit()) {
            val d = card.digitToInt()
            if (d in 2..9) {
                d
            } else {
                throw IllegalStateException("Invalid card: $this")
            }
        } else {
            when (card) {
                'T' -> 10
                'J' -> 1
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalStateException("Invalid card: $this")
            }
        }
    }
}


data class Hand(
    val cards: String,
    val bid: Long,
) {
    private val countMap by lazy {
        val countMap = mutableMapOf<Char, Int>()
        for (c in cards) {
            countMap.compute(c) { _, count ->
                (count ?: 0) + 1
            }
        }

        countMap
    }

    val type1 by lazy {
        when (countMap.size) {
            1 -> HandType.FIVE_OF_A_KIND
            2 -> {
                if (4 in countMap.values) {
                    HandType.FOUR_OF_A_KIND
                } else {
                    HandType.FULL_HOUSE
                }
            }

            3 -> {
                if (3 in countMap.values) {
                    HandType.THREE_OF_A_KIND
                } else {
                    HandType.TWO_PAIR
                }
            }

            4 -> HandType.ONE_PAIR
            5 -> HandType.HIGH_CARD
            else -> throw IllegalStateException("weird count map: $countMap")
        }
    }

    val type2 by lazy {
        when (countMap.size) {
            1 -> HandType.FIVE_OF_A_KIND
            2 -> {
                if ('J' in countMap.keys) {
                    // wild card
                    HandType.FIVE_OF_A_KIND
                } else {
                    // normal
                    if (4 in countMap.values) {
                        HandType.FOUR_OF_A_KIND
                    } else {
                        HandType.FULL_HOUSE
                    }
                }
            }

            3 -> {
                if ('J' in countMap.keys) {
                    // wild card
                    when (countMap['J']) {
                        3 -> HandType.FOUR_OF_A_KIND
                        2 -> HandType.FOUR_OF_A_KIND
                        1 -> {
                            if (3 in countMap.values) {
                                HandType.FOUR_OF_A_KIND
                            } else {
                                HandType.FULL_HOUSE
                            }
                        }

                        else -> throw IllegalStateException("Bad card count for 3 diff with wild: $countMap")
                    }
                } else {
                    // Normal
                    if (3 in countMap.values) {
                        HandType.THREE_OF_A_KIND
                    } else {
                        HandType.TWO_PAIR
                    }
                }
            }

            4 -> {
                if ('J' in countMap.keys) {
                    // wild card
                    HandType.THREE_OF_A_KIND
                } else {
                    // normal
                    HandType.ONE_PAIR
                }
            }

            5 -> {
                if ('J' in countMap.keys) {
                    // wild card
                    HandType.ONE_PAIR
                } else {
                    // normal
                    HandType.HIGH_CARD
                }
            }

            else -> throw IllegalStateException("weird count map: $countMap")
        }
    }
}

class Day7 : Day<List<Hand>, Long>() {
    override fun loadInputFromString(rawInput: String): List<Hand> {
        return rawInput.trim().lines().map { line ->
            val (cards, bid) = line.split(" ")
            if (cards.length != 5) {
                throw IllegalStateException("Invalid hand: $line")
            }
            Hand(
                cards = cards,
                bid = bid.toLong(),
            )
        }
    }

    override fun loadTestInput(): String {
        return """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
        """.trimIndent()
    }

    override val part1TestExpectedOutput = 6440L

    override fun doPart1(input: List<Hand>): Long {
        return input.sortedWith(Part1TypeComparator()).reversed().mapIndexed { index, hand ->
            hand.bid * (input.size - index)
        }.sum()
    }

    override val part2TestExpectedOutput = 5905L

    override fun doPart2(input: List<Hand>): Long {
        return input.sortedWith(Part2TypeComparator()).reversed().mapIndexed { index, hand ->
            hand.bid * (input.size - index)
        }.sum()
    }
}
