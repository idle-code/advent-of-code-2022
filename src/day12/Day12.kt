package day12

import log
import logEnabled
import logln
import readInput
import kotlin.collections.ArrayDeque

private const val DAY_NUMBER = 12

typealias Height = Char

data class Position(val x: Int, val y: Int)

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
                }
                else if (heightMap[y][x] == 'E') {
                    end = Position(x, y)
                    heightMap[y][x] = 'z'
                }
            }
        }
        return Triple(heightMap, start, end)
    }

    fun neighboursOf(stepsMap: Array<Array<Int>>, pos: Position): List<Position> {
        val positions = listOf(
            Position(pos.x - 1, pos.y),
            Position(pos.x + 1, pos.y),
            Position(pos.x, pos.y - 1),
            Position(pos.x, pos.y + 1),
        )
        val width = stepsMap[0].size
        val height = stepsMap.size
        return positions.filter { position ->
            position.x >= 0 && position.y >= 0
                &&
            position.x < width && position.y < height
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

    fun findWay(heightMap: Array<Array<Height>>, start: Position, end: Position): Int {
        val stepsMap = Array(heightMap.size) { Array(heightMap[0].size) { -1 } }
        stepsMap[start.y][start.x] = 0
        val visitQueue = ArrayDeque<Position>()
        visitQueue.addAll(neighboursOf(stepsMap, start))

        while (visitQueue.isNotEmpty()) {
            val current = visitQueue.removeFirst()
            var currentSteps = stepsMap[current.y][current.x]
            val currentHeight = heightMap[current.y][current.x]
            val neighbours = neighboursOf(stepsMap, current)
            val reachableNeighbours = neighbours
                .filter {
                    val neighbourHeight = heightMap[it.y][it.x]
                    currentHeight <= (neighbourHeight + 1)
                }
            val neighbourSteps = reachableNeighbours
                .filter { stepsMap[it.y][it.x] != -1 }
                .map { stepsMap[it.y][it.x] }
            if (neighbourSteps.isNotEmpty()) {
                val newMinSteps = neighbourSteps.min() + 1
                stepsMap[current.y][current.x] = newMinSteps// min(newMinSteps, currentSteps)
                currentSteps = stepsMap[current.y][current.x]
            }
            val unvisitedReachableNeighbours = reachableNeighbours
                .filter { (stepsMap[it.y][it.x] == -1 && !(visitQueue.contains(it))) }
            visitQueue.addAll(unvisitedReachableNeighbours)
            if (visitQueue.size % 100 == 0)
                printStepMap(stepsMap)
        }

        printStepMap(stepsMap)

        return stepsMap[end.y][end.x]
    }

    fun part1(rawInput: List<String>): Int {
        val (heightMap, start, end) = parseMap(rawInput)

        return findWay(heightMap, start, end)
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

//    val part1SampleResult = part1(sampleInput)
//    println(part1SampleResult)
//    check(part1SampleResult == 31)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
//    check(part1MainResult == 0)

//    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 0)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
