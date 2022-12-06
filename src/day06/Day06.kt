package day06

import readInput

fun main() {
    fun findUniqueSequenceOffset(stream: String, sequenceLength: Int): Int {
        for (i in 0..stream.lastIndex) {
            val windowSet = stream.drop(i).take(sequenceLength).toSet()
            if (windowSet.size == sequenceLength)
                return i + sequenceLength
        }
        throw IllegalArgumentException("Could not find unique sequence of characters")
    }

    fun part1(input: List<String>): Int = findUniqueSequenceOffset(input[0], 4)

    fun part2(input: List<String>): Int = findUniqueSequenceOffset(input[0], 14)

    val testInput = readInput("sample_data", 6)
    println(part1(testInput))
    check(part1(testInput) == 5)

    val mainInput = readInput("main_data", 6)
    println(part1(mainInput))
    check(part1(mainInput) == 1723)

    println(part2(testInput))
    check(part2(testInput) == 23)
    println(part2(mainInput))
    check(part2(mainInput) == 3708)
}
