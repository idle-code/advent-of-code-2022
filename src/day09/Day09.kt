package day09

import readInput
import kotlin.math.abs

private const val DAY_NUMBER = 9

data class Knot(val x: Int, val y: Int) {
    infix fun touches(other: Knot): Boolean =
        abs(this.x - other.x) <= 1 && abs(this.y - other.y) <= 1

    val up: Knot get() = Knot(x, y + 1)

    val down: Knot get() = Knot(x, y - 1)

    val left: Knot get() = Knot(x - 1, y)

    val right: Knot get() = Knot(x + 1, y)

    val upRight: Knot get() = Knot(x + 1, y + 1)
    val upLeft: Knot get() = Knot(x - 1, y + 1)
    val downRight: Knot get() = Knot(x + 1, y - 1)
    val downLeft: Knot get() = Knot(x - 1, y - 1)

//    private fun follow(next: Knot): Knot {
//        if (this touches next)
//            return this
//
//        val xDistance = next.x - x
//        val yDistance = next.y - y
//
//        val newTailPosition = when {
//            xDistance == 0 && yDistance > 0 ->
//                up
//
//            xDistance == 0 && yDistance < 0 ->
//                down
//
//            xDistance > 0 && yDistance == 0 ->
//                right
//
//            xDistance < 0 && yDistance == 0 ->
//                left
//
//
//            xDistance < 0 && yDistance < 0 ->
//                downLeft
//
//            xDistance > 0 && yDistance < 0 ->
//                downRight
//
//            xDistance < 0 && yDistance > 0 ->
//                upLeft
//
//            xDistance > 0 && yDistance > 0 ->
//                upRight
//
//            else -> throw IllegalStateException("???")
//        }
//
//        return newTailPosition
//    }

    override fun toString(): String = "($x, $y)"
}

enum class Direction(val representation: String) {
    UP("U"),
    DOWN("D"),
    LEFT("L"),
    RIGHT("R"),
    UP_RIGHT("UR"),
    UP_LEFT("UL"),
    DOWN_RIGHT("DR"),
    LEFT_LEFT("DL"),
}

data class Move(val direction: Direction, val amount: Int)

class Grid {
    private var headPosition = Knot(0, 0)

    private var tailPosition = Knot(0, 0)

    private val uniqueTailPositions = HashSet<Knot>()

    val uniqueTailPositionsCount: Int
        get() = uniqueTailPositions.size

    init {
        uniqueTailPositions.add(tailPosition)
    }

    fun moveHead(direction: Direction, amount: Int) {
        for (step in 0 until amount) {
            print("Moving from $headPosition to ")

            headPosition = moveHead(direction)

            print(headPosition)
            print(" tail is at $tailPosition")

            tailPosition = moveTail()
            uniqueTailPositions.add(tailPosition)

            println(" and moved to $tailPosition")
        }
    }

    fun moveHead(direction: Direction): Knot {
        val newHeadPosition = when (direction) {
            Direction.UP -> headPosition.up
            Direction.DOWN -> headPosition.down
            Direction.LEFT -> headPosition.left
            Direction.RIGHT -> headPosition.right
            else -> throw IllegalArgumentException("Invalid input direction: $direction")
        }

        return newHeadPosition
    }

    private fun moveTail(): Knot {
        if (tailPosition touches headPosition)
            return tailPosition

        val xDistance = headPosition.x - tailPosition.x
        val yDistance = headPosition.y - tailPosition.y

        val newTailPosition = when {
            xDistance == 0 && yDistance > 0 ->
                tailPosition.up

            xDistance == 0 && yDistance < 0 ->
                tailPosition.down

            xDistance > 0 && yDistance == 0 ->
                tailPosition.right

            xDistance < 0 && yDistance == 0 ->
                tailPosition.left


            xDistance < 0 && yDistance < 0 ->
                tailPosition.downLeft

            xDistance > 0 && yDistance < 0 ->
                tailPosition.downRight

            xDistance < 0 && yDistance > 0 ->
                tailPosition.upLeft

            xDistance > 0 && yDistance > 0 ->
                tailPosition.upRight

            else -> throw IllegalStateException("???")
        }

        return newTailPosition
    }
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val grid = Grid()
        for (line in rawInput) {
            val direction = line.substringBefore(' ')
            val amount = line.substringAfter(' ')

            val typedDirection = when (direction) {
                "U" -> Direction.UP
                "D" -> Direction.DOWN
                "L" -> Direction.LEFT
                "R" -> Direction.RIGHT
                else -> throw IllegalArgumentException("Invalid input direction: $direction")
            }

            grid.moveHead(typedDirection, amount.toInt())
        }

        return grid.uniqueTailPositionsCount
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 13)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 6503)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
//    check(part2SampleResult == 1)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
