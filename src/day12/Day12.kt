package day12

import log
import logEnabled
import logln
import readInput
import kotlin.collections.ArrayDeque

private const val DAY_NUMBER = 12

typealias Height = Char

data class Position(val x: Int, val y: Int)

private const val NOT_VISITED = -1

fun main() {
    fun parseMap(rawInput: List<String>): Triple<Array<Array<Height>>, Position, Position> {
        val heightMap = Array(rawInput.size) { Array(rawInput[0].length) { 'a' } }
        var start = Position(0, 0)
        var end = Position(0, 0)
        for (y in 0..rawInput.lastIndex) {
            heightMap[y] = rawInput[y].toCharArray().toTypedArray()
            for (x in 0..heightMap[y].lastIndex) {
                if (heightMap[y][x] == 'S') {
                    start = Position(x, y)
                    heightMap[y][x] = 'a'
                } else if (heightMap[y][x] == 'E') {
                    end = Position(x, y)
                    heightMap[y][x] = 'z'
                }
            }
        }
        return Triple(heightMap, start, end)
    }

    fun allNeighboursOf(stepsMap: Array<Array<Int>>, pos: Position): List<Position> {
        val positions = listOf(
            Position(pos.x - 1, pos.y),
            Position(pos.x + 1, pos.y),
            Position(pos.x, pos.y - 1),
            Position(pos.x, pos.y + 1),
        )
        val width = stepsMap[0].size
        val height = stepsMap.size
        return positions.filter { pos ->
            pos.x >= 0 && pos.y >= 0 && pos.x < width && pos.y < height
        }
    }

    fun printStepMap(stepsMap: Array<Array<Int>>) {
        val width = stepsMap[0].size
        val height = stepsMap.size
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (stepsMap[y][x] < 0)
                    log(".")
                else
                    log("${stepsMap[y][x] % 10}")
            }
            logln("")
        }
        logln("--------------------------------------------------")
    }

    fun findWay(
        heightMap: Array<Array<Height>>,
        start: Position,
        pathExists: (Height, Height) -> Boolean
    ): Array<Array<Int>> {
        val stepsMap = Array(heightMap.size) { Array(heightMap[0].size) { NOT_VISITED } }
        stepsMap[start.y][start.x] = 0
        val candidates = ArrayDeque<Position>()
        candidates.addAll(allNeighboursOf(stepsMap, start))

        while (candidates.isNotEmpty()) {
            val current = candidates.removeFirst()

            val currentSteps = stepsMap[current.y][current.x]
            if (currentSteps != NOT_VISITED)
                continue

            val currentHeight = heightMap[current.y][current.x]

            // Add neighbours for further processing
            val allNeighbours = allNeighboursOf(stepsMap, current)
            val neighboursReachableFromCurrent = allNeighbours.filter {
                val neighbourHeight = heightMap[it.y][it.x]
                pathExists(currentHeight, neighbourHeight)
            }.filter {
                val neighbourSteps = stepsMap[it.y][it.x]
                neighbourSteps == NOT_VISITED
            }.filter {
                it !in candidates
            }
            candidates.addAll(neighboursReachableFromCurrent)

            // Update current distance
            val sourceNeighbours = allNeighbours.filter {
                val neighbourHeight = heightMap[it.y][it.x]
                pathExists(neighbourHeight, currentHeight)
            }.filter {
                val neighbourSteps = stepsMap[it.y][it.x]
                neighbourSteps != NOT_VISITED
            }

            val minNeighbourSteps = sourceNeighbours.minOfOrNull { stepsMap[it.y][it.x] }
            if (minNeighbourSteps != null)
                stepsMap[current.y][current.x] = minNeighbourSteps + 1
        }

        printStepMap(stepsMap)

        return stepsMap
    }

    fun part1(rawInput: List<String>): Int {
        val (heightMap, start, end) = parseMap(rawInput)
        val stepMap = findWay(heightMap, start) { from, to -> (from + 1) >= to }
        return stepMap[end.y][end.x]
    }

    fun part2(rawInput: List<String>): Int {
        val (heightMap, start, end) = parseMap(rawInput)

        val stepMap = findWay(heightMap, end) { from, to -> from <= (to + 1) }
        var minStepsToA = stepMap[start.y][start.x]
        for (y in 0..heightMap.lastIndex) {
            for (x in 0..heightMap[0].lastIndex) {
                if (heightMap[y][x] != 'a' || stepMap[y][x] == NOT_VISITED)
                    continue
                minStepsToA = kotlin.math.min(minStepsToA, stepMap[y][x])
            }
        }
        return minStepsToA
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 31)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 370)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
    check(part2SampleResult == 29)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
    check(part2MainResult == 363)
}
