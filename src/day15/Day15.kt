package day15

import Position
import logEnabled
import readInput

private const val DAY_NUMBER = 15

data class Sensor(val sensorPosition: Position, val beaconPosition: Position) {
    private val distanceToBeacon: Int get() = sensorPosition.manhattanDistanceTo(beaconPosition)

    val xSensingRange: IntRange
        get() = IntRange(
            sensorPosition.x - distanceToBeacon,
            sensorPosition.x + distanceToBeacon
        )

    fun couldSense(position: Position): Boolean = sensorPosition.manhattanDistanceTo(position) <= distanceToBeacon
}

val sensorLineRegex = """Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""".toRegex()

fun parseSensor(line: String): Sensor {
    val (sensorX, sensorY, beaconX, beaconY) = sensorLineRegex.matchEntire(line)?.destructured
        ?: throw IllegalArgumentException("Invalid input line: $line")
    return Sensor(Position(sensorX.toInt(), sensorY.toInt()), Position(beaconX.toInt(), beaconY.toInt()))
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
                if (sensor.couldSense(candidate) && candidate != sensor.beaconPosition)
                    emptyPositions.add(candidate)
            }
        }
        return emptyPositions.size
    }

    fun part2(rawInput: List<String>): Int {
        return 0
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

//    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 93)
//
//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}


