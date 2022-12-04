package day04

import readInput

private fun String.toIntRange(): IntRange =
    IntRange(this.substringBefore('-').toInt(), this.substringAfter('-').toInt())

private fun String.toIntRangePair(): Pair<IntRange, IntRange> =
    Pair(this.substringBefore(',').toIntRange(), this.substringAfter(',').toIntRange())

private infix fun IntRange.fullyContain(other: IntRange): Boolean =
    other.first >= this.first && other.last <= this.last

private infix fun IntRange.overlaps(other: IntRange): Boolean =
    this.contains(other.first) || this.contains(other.last)


fun main() {
    fun part1(input: List<String>): Int {
        var fullyContainedRangesCount = 0
        for (line in input) {
            val (leftRange, rightRange) = line.toIntRangePair()
            if (leftRange fullyContain rightRange || rightRange fullyContain leftRange)
                ++fullyContainedRangesCount
        }
        return fullyContainedRangesCount
    }

    fun part2(input: List<String>): Int {
        var overlappingPairs = 0
        for (line in input) {
            val (leftRange, rightRange) = line.toIntRangePair()
            if (leftRange overlaps rightRange || rightRange overlaps leftRange)
                ++overlappingPairs
        }
        return overlappingPairs
    }

    val testInput = readInput("sample_data", 4)
    println(part1(testInput))
    check(part1(testInput) == 2)

    val mainInput = readInput("main_data", 4)
    println(part1(mainInput))
    check(part1(mainInput) == 538)

    println(part2(testInput))
    check(part2(testInput) == 4)
    println(part2(mainInput))
    check(part2(mainInput) == 792)
}
