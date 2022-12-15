package day14

import Position
import log
import logEnabled
import logln
import readInput
import kotlin.math.max

private const val DAY_NUMBER = 14

data class Line(val start: Position, val end: Position) {
    init {
        if (start >= end)
            throw IllegalArgumentException("Start cannot be greater than end")
        if (start.x != end.x && start.y != end.y)
            throw IllegalArgumentException("Segment is not straight")
    }

    override fun toString(): String {
        return "$start - $end"
    }

    fun isVertical(): Boolean {
        return start.x == end.x
    }
}

fun parseFile(rawInput: List<String>): List<List<Line>> {
    val allSegments = mutableListOf<List<Line>>()
    for (line in rawInput) {
        val segmentsInLine = parseSegments(line)
        allSegments.add(segmentsInLine)
    }
    return allSegments
}

fun parseSegments(line: String): List<Line> {
    val pointsInRockLine = line.split(" -> ").map { coords ->
        val x = coords.substringBefore(',')
        val y = coords.substringAfter(',')
        Position(x.toInt() - 1, y.toInt() - 1)
    }
    val rockSegments = mutableListOf<Line>()
    for (i in 1..pointsInRockLine.lastIndex) {
        if (pointsInRockLine[i - 1] < pointsInRockLine[i])
            rockSegments.add(Line(pointsInRockLine[i - 1], pointsInRockLine[i]))
        else
            rockSegments.add(Line(pointsInRockLine[i], pointsInRockLine[i - 1]))
    }
    return rockSegments
}

val sandStart = Position(499, -1)

enum class SpaceType(val symbol: Char) {
    Air('.'),
    Rock('#'),
    Sand('o'),
}

fun fillWithRocks(fallMap: Array<Array<SpaceType>>, rockLines: List<Line>) {
    for (line in rockLines) {
        if (line.isVertical()) {
            val x = line.start.x
            for (y in line.start.y..line.end.y)
                fallMap[y][x] = SpaceType.Rock
        } else {
            val y = line.start.y
            for (x in line.start.x..line.end.x)
                fallMap[y][x] = SpaceType.Rock
        }
    }
}

fun addFloor(fallMap: Array<Array<SpaceType>>) {
    val width = fallMap[0].size
    val y = fallMap.lastIndex
    for (x in 0 until width)
        fallMap[y][x] = SpaceType.Rock
}

private fun Array<Array<SpaceType>>.print() {
    logln("")
    for (y in 0..this.lastIndex) {
        for (x in 0..this[0].lastIndex)
            log(this[y][x].symbol)
        logln("")
    }
}

private fun Position.fallPositions(): List<Position> {
    return listOf(
        Position(this.x, this.y + 1),
        Position(this.x - 1, this.y + 1),
        Position(this.x + 1, this.y + 1)
    )
}

fun Array<Array<SpaceType>>.dropSandAt(start: Position): Position? {
    var current = start
    simulationLoop@ while (true) {
        for (candidatePos in current.fallPositions()) {
            if (!this.containsPosition(candidatePos))
                return null
            if (this[candidatePos.y][candidatePos.x] == SpaceType.Air) {
                current = candidatePos
                continue@simulationLoop
            }
        }
        break
    }
    return current
}

private fun Array<Array<SpaceType>>.containsPosition(position: Position): Boolean {
    return position.y < this.size && position.x < this[0].size && position.y >= 0 && position.x >= 0
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val rockLines = parseFile(rawInput)
        val maxX = rockLines.maxOf { lines -> lines.maxOf { line -> kotlin.math.max(line.start.x, line.end.x) } }
        val maxY = rockLines.maxOf { lines -> lines.maxOf { line -> kotlin.math.max(line.start.y, line.end.y) } }
        val fallMap = Array(maxY + 1) { Array(maxX + 1) { SpaceType.Air } }
        fillWithRocks(fallMap, rockLines.flatten())
//        fallMap.print()
        var fallenGrains = 0
        while (true) {
            val restPosition = fallMap.dropSandAt(sandStart)
            if (restPosition == null)
                break
            fallMap[restPosition.y][restPosition.x] = SpaceType.Sand
            ++fallenGrains
        }
        fallMap.print()
        return fallenGrains

    }

    fun part2(rawInput: List<String>): Int {
        val rockLines = parseFile(rawInput)
        val maxY = rockLines.maxOf { lines -> lines.maxOf { line -> kotlin.math.max(line.start.y, line.end.y) } }
        var maxX = rockLines.maxOf { lines -> lines.maxOf { line -> kotlin.math.max(line.start.x, line.end.x) } }
        maxX = max(maxX, 500 + maxY)
        val fallMap = Array(maxY + 1 + 2) { Array(maxX + 10) { SpaceType.Air } }
        fillWithRocks(fallMap, rockLines.flatten())
        addFloor(fallMap)
        fallMap.print()
        var fallenGrains = 0
        while (true) {
            val restPosition = fallMap.dropSandAt(sandStart)!!
            if (restPosition == sandStart)
                break
            fallMap[restPosition.y][restPosition.x] = SpaceType.Sand
            ++fallenGrains
//            fallMap.print()
        }
        fallMap.print()
        return fallenGrains + 1
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

//    val part1SampleResult = part1(sampleInput)
//    println(part1SampleResult)
//    check(part1SampleResult == 24)
//
//    val part1MainResult = part1(mainInput)
//    println(part1MainResult)
//    check(part1MainResult == 961)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
    check(part2SampleResult == 93)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
//    check(part2MainResult == 0)
}
