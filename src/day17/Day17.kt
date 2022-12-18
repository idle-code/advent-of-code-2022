package day17

import Position
import logEnabled
import logln
import log
import readInput

private const val DAY_NUMBER = 17

private const val BOARD_WIDTH = 7

typealias BoardRow = Array<Boolean>
typealias Board = ArrayDeque<BoardRow>

private val Board.height: Int get() = this.size

private val Board.width: Int get() = BOARD_WIDTH

private fun Board.addTopRows(rowCount: Int) {
    for (row in 0 until rowCount)
        this.addFirst(Array(BOARD_WIDTH) { false })
}

private fun Board.freeRowsFromTop(): Int {
    for (row in 0 until this.height) {
        if (this[row].any { it })
            return row
    }
    return this.height
}

private fun Board.bulkheadHeight(): Int? {
    for (y in this.lastIndex downTo 0) {
        if (this[y].all { it })
            return this.height - y
    }
    return null
}

private val Board.occupiedHeight: Int
    get() {
        return this.height - this.freeRowsFromTop()
    }

private fun Board.print() {
    if (!logEnabled)
        return
    for (y in 0 until this.height) {
        for (x in 0 until this.width) {
            if (this[y][x])
                log("#")
            else
                log(".")
        }
        logln("")
    }
    logln("$$$$$$$$$$$$$$")
}

private fun Board.printWith(block: Block) {
    if (!logEnabled)
        return
    for (y in 0 until this.height) {
        for (x in 0 until this.width) {
            val positionInBlock = Position(x, y) - block.position
            if (positionInBlock.x >= 0 && positionInBlock.x < block.width && positionInBlock.y >= 0 && positionInBlock.y < block.height) {
                if (block.mask[positionInBlock.y][positionInBlock.x])
                    log("@")
                else if (this[y][x])
                    log("#")
                else
                    log(".")
            } else if (this[y][x])
                log("#")
            else
                log(".")
        }
        logln("")
    }
    logln("-----------------")
}

private fun <T> Sequence<T>.repeat(): Sequence<T> = sequence {
    while (true) {
        yieldAll(this@repeat)
    }
}

class Block(val mask: Array<Array<Boolean>>) {
    var position = Position(0, 0)

    val width: Int get() = mask[0].size

    val height: Int get() = mask.size

    fun moveLeft(board: Board) = move(board, Position(position.x - 1, position.y))

    fun moveRight(board: Board) = move(board, Position(position.x + 1, position.y))

    fun moveDown(board: Board) = move(board, Position(position.x, position.y + 1))

    private fun move(board: Board, newPosition: Position): Boolean {
        if (!canMove(board, newPosition))
            return false
        position = newPosition
        return true
    }

    private fun canMove(board: Board, newPosition: Position): Boolean {
        if (newPosition.x < 0 || newPosition.x + width > board.width)
            return false
        if (newPosition.y + height > board.height)
            return false
        return !overlapsWithAnythingAt(board, newPosition)
    }

    private fun overlapsWithAnythingAt(board: Board, newPosition: Position): Boolean {
        for (y in 0..mask.lastIndex) {
            for (x in 0..mask[0].lastIndex) {
                if (board[newPosition.y + y][newPosition.x + x] && mask[y][x])
                    return true
            }
        }
        return false
    }

    fun settleOn(board: Board) {
        for (y in 0..mask.lastIndex) {
            for (x in 0..mask[0].lastIndex) {
                board[position.y + y][position.x + x] = board[position.y + y][position.x + x] || mask[y][x]
            }
        }
    }
}

private fun String.toMask(): Array<Array<Boolean>> {
    val lines = this.lines()
    val height = lines.size
    val width = lines.first().length
    val mask = Array(height) { Array(width) { false } }
    for (y in 0..mask.lastIndex) {
        for (x in 0..mask[0].lastIndex) {
            val symbol = lines[y][x]
            mask[y][x] = when (symbol) {
                '#' -> true
                else -> false
            }
        }
    }
    return mask
}

val horizontalBlock = Block("####".toMask())
val crossBlock = Block(".#.\n###\n.#.".toMask())
val angleBlock = Block("..#\n..#\n###".toMask())
val verticalBlock = Block("#\n#\n#\n#".toMask())

val squareBlock = Block("##\n##".toMask())

val availableBlocks = sequenceOf(horizontalBlock, crossBlock, angleBlock, verticalBlock, squareBlock)


fun main() {
    fun simulate(steamDirections: Sequence<Char>, iterationCount: Long): Int {
        val board = Board()
        board.addTopRows(4)
        check(board.occupiedHeight == 0)

        val steamGusts = steamDirections.repeat().iterator()
        val blocks = availableBlocks.repeat().iterator()

//        val heights = Array(iterationCount.toInt()) { 0 }

        for (round in 1..iterationCount) {
            // Place block at the top
            val block = blocks.next()
            val requiredHeight = block.height + 3 + board.occupiedHeight
            if (requiredHeight >= board.height)
                board.addTopRows(requiredHeight - board.height)
            block.position = Position(2, board.height - (block.height + 3 + board.occupiedHeight))

            while (true) {
                board.printWith(block)

                // Use steam gust to move it
                logln("Steam action")
                when (steamGusts.next()) {
                    '<' -> block.moveLeft(board)
                    '>' -> block.moveRight(board)
                }
                board.printWith(block)

                // Move it down
                logln("Moving down")
                if (!block.moveDown(board)) {
                    logln("Setting block")
                    block.settleOn(board)
                    board.print()
                    break
                }
            }

//            heights[round.toInt() - 1] = board.occupiedHeight
        }

//        val differences = heights.drop(1).mapIndexed { index, h -> h - heights[index] }
//        differences.forEach { print("$it ") }

        return board.occupiedHeight
    }

    fun part1(rawInput: List<String>): Int {
        val steamDirections = rawInput.first().asSequence()
        return simulate(steamDirections, 2022)
    }

    fun part2(rawInput: List<String>, repeatingSubsequenceLength: Int): Long {
        val maxIterations = 1_000_000_000_000
        val loopDifference =
            simulate(rawInput.first().asSequence(), (repeatingSubsequenceLength * 6).toLong()) - simulate(
                rawInput.first().asSequence(), (repeatingSubsequenceLength * 5).toLong()
            )

        val requiredEndIterations = maxIterations % repeatingSubsequenceLength + repeatingSubsequenceLength
        return simulate(
            rawInput.first().asSequence(),
            requiredEndIterations
        ) + (maxIterations / repeatingSubsequenceLength - 1) * loopDifference
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 3068)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 3175)

    // FIXME: Find a way to calculate repeatingSubsequenceLength (signal processing?)
    val part2SampleResult = part2(sampleInput, 35)
    println(part2SampleResult)
    check(part2SampleResult == 1514285714288)

    val part2MainResult = part2(mainInput, 1760)
    println(part2MainResult)
    check(part2MainResult == 1555113636385)
}
