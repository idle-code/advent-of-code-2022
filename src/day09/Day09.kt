package day09

import log
import logln
import logEnabled
import readInput
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

private const val DAY_NUMBER = 9

data class Offset(val x: Int, val y: Int)


enum class Direction(val offset: Offset) {
    UP(Offset(0, 1)),
    DOWN(Offset(0, -1)),
    LEFT(Offset(-1, 0)),
    RIGHT(Offset(1, 0)),
}


data class KnotPosition(val x: Int, val y: Int) {
    fun follow(target: KnotPosition): KnotPosition {
        if (this touches target)
            return this

        var xOffset = target.x - x
        var yOffset = target.y - y

        // Clamp to [-1, 1] range
        xOffset = max(-1, min(xOffset, 1))
        yOffset = max(-1, min(yOffset, 1))

        return KnotPosition(x + xOffset, y + yOffset)
    }

    fun move(direction: Direction): KnotPosition = this + direction.offset

    operator fun plus(offset: Offset): KnotPosition = KnotPosition(x + offset.x, y + offset.y)

    infix fun touches(other: KnotPosition): Boolean =
        abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1

    override fun toString(): String = "($x, $y)"
}


class Simulation(ropeKnots: Int) {
    private val rope: Array<KnotPosition> = Array(ropeKnots) { KnotPosition(0, 0) }

    private var head: KnotPosition
        get() = rope.first()
        set(newHead) {
            rope[0] = newHead
        }

    private val tail: KnotPosition get() = rope.last()

    private val uniqueTailPositions = HashSet<KnotPosition>()

    val uniqueTailPositionsCount: Int
        get() = uniqueTailPositions.size

    init {
        uniqueTailPositions.add(tail)
    }

    fun run(movements: List<Pair<Direction, Int>>) {
        for (move in movements)
            moveRope(move.first, move.second)
    }

    private fun moveRope(direction: Direction, steps: Int) {
        for (step in 0 until steps) {
            log("Moving from $head to ")
            head = head.move(direction)
            log(head)

            log(" tail is at $tail")
            for (knotId in 1..rope.lastIndex) {
                rope[knotId] = rope[knotId].follow(rope[knotId - 1])
            }
            logln(" and moved to $tail")
            uniqueTailPositions.add(tail)
        }
    }
}

private fun String.toDirection(): Direction {
    return when (this) {
        "U" -> Direction.UP
        "D" -> Direction.DOWN
        "L" -> Direction.LEFT
        "R" -> Direction.RIGHT
        else -> throw IllegalArgumentException("Invalid input direction: '$this'")
    }
}

fun main() {
    fun toMovementList(rawInput: List<String>) = rawInput.map {
        Pair(it.substringBefore(' ').toDirection(), it.substringAfter(' ').toInt())
    }

    fun part1(rawInput: List<String>): Int {
        val movements = toMovementList(rawInput)

        val simulation = Simulation(2)
        simulation.run(movements)

        return simulation.uniqueTailPositionsCount
    }

    fun part2(rawInput: List<String>): Int {
        val movements = toMovementList(rawInput)

        val simulation = Simulation(10)
        simulation.run(movements)

        return simulation.uniqueTailPositionsCount
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 13)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 6503)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
    check(part2SampleResult == 1)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
    check(part2MainResult == 2724)
}
