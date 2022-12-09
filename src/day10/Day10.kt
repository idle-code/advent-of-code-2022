package day10

import logEnabled
import readInput

private const val DAY_NUMBER = 10

fun main() {
    fun part1(rawInput: List<String>): Int {
        return 0
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 0)

//    val part1MainResult = part1(mainInput)
//    println(part1MainResult)
//    check(part1MainResult == 0)
//
//    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 0)
//
//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
