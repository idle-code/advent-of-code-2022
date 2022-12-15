package day15

import Position
import logln
import logEnabled
import readInput
import java.lang.IllegalStateException
import java.lang.Math.abs

private const val DAY_NUMBER = 15

data class Sensor(val sensorPosition: Position, val beaconPosition: Position) {
    private val distanceToBeacon: Int = sensorPosition.manhattanDistanceTo(beaconPosition)

    val xSensingRange: IntRange = IntRange(
        sensorPosition.x - distanceToBeacon,
        sensorPosition.x + distanceToBeacon
    )

    fun couldSenseAt(position: Position): Boolean = sensorPosition.manhattanDistanceTo(position) <= distanceToBeacon

    fun senseRangeAt(y: Int): IntRange? {
        val yOffset = kotlin.math.abs(sensorPosition.y - y)
        if (yOffset > distanceToBeacon)
            return null
        return IntRange(
            sensorPosition.x - (distanceToBeacon - yOffset),
            sensorPosition.x + (distanceToBeacon - yOffset)
        )
    }
}

val sensorLineRegex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()

fun parseSensor(line: String): Sensor {
    val (sensorX, sensorY, beaconX, beaconY) = sensorLineRegex.matchEntire(line)?.destructured
        ?: throw IllegalArgumentException("Invalid input line: $line")
    return Sensor(Position(sensorX.toInt(), sensorY.toInt()), Position(beaconX.toInt(), beaconY.toInt()))
}

private fun List<IntRange>.isSingleRange(): Boolean {
    val sortedRanges = this.sortedBy { it.first }
    var lastRange = sortedRanges.first()
    for (range in sortedRanges.drop(1)) {
        if (range.first !in lastRange)
            return false
        if (range.last !in lastRange)
            lastRange = range
    }
    return true
}


fun main() {
    fun part1(rawInput: List<String>, y: Int): Int {
        val sensors = rawInput.map { parseSensor(it) }
        val minX = sensors.minOfOrNull { s -> s.xSensingRange.first }!! - 1
        val maxX = sensors.maxOfOrNull { s -> s.xSensingRange.last }!! + 1

        val emptyPositions = hashSetOf<Position>()
        for (sensor in sensors) {
            for (x in minX..maxX) {
                val candidate = Position(x, y)
                if (sensor.couldSenseAt(candidate) && candidate != sensor.beaconPosition)
                    emptyPositions.add(candidate)
            }
        }
        return emptyPositions.size
    }

    fun part2(rawInput: List<String>, maxCoordinate: Int): Long {
        val sensors = rawInput.map { parseSensor(it) }
        for (y in 0..maxCoordinate) {
            val isRowCovered = sensors.mapNotNull { s -> s.senseRangeAt(y) }.isSingleRange()
            if (isRowCovered)
                continue
            // Scan row for actual signal
            nextXCoordinate@ for (x in 0..maxCoordinate) {
                for (sensor in sensors) {
                    val candidate = Position(x, y)
                    if (sensor.couldSenseAt(candidate)) {
                        continue@nextXCoordinate
                    }
                }
                return x * 4000000L + y
            }
        }
        throw IllegalStateException("Distress signal tuning not found")
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

    val part1SampleResult = part1(sampleInput, 10)
    println(part1SampleResult)
    check(part1SampleResult == 26)

    val part1MainResult = part1(mainInput, 2000000)
    println(part1MainResult)
    check(part1MainResult == 4748135)

    val part2SampleResult = part2(sampleInput, 20)
    println(part2SampleResult)
    check(part2SampleResult == 56000011L)

    val part2MainResult = part2(mainInput, 4000000)
    println(part2MainResult)
    check(part2MainResult == 13743542639657)
}


