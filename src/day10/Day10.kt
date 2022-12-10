package day10

import logEnabled
import logln
import readInput

private const val DAY_NUMBER = 10

private const val SCREEN_WIDTH = 40

data class Operation(val opcode: String, val cost: Int, val arg: Int? = null)

class Simulator {
    private var registerX = 1

    private var currentCycle = 0

    val watchValues = ArrayList<Pair<Int, Int>>()

    private val watchPoints = HashSet<Int>()

    private val screen = Array(SCREEN_WIDTH * 6) { false }

    private val spriteRange: IntRange
        get() = IntRange(registerX - 1, registerX + 1)

    fun addWatch(cycle: Int) {
        watchPoints.add(cycle)
    }

    fun execute(operations: List<Operation>) {
        for (operation in operations) {
            execute(operation)
        }
    }

    private fun execute(operation: Operation) {
        updateScreen(operation.cost)
        updateWatch(operation.cost)
        currentCycle += operation.cost

        when (operation.opcode) {
            "noop" -> Unit
            "addx" -> registerX += operation.arg!!
            else -> throw IllegalArgumentException("Unknown opcode: ${operation.opcode}")
        }
    }

    private fun updateWatch(currentInstructionCycleCost: Int) {
        for (cycle in currentCycle+1 ..currentCycle + currentInstructionCycleCost) {
            if (cycle in watchPoints)
                watchValues.add(cycle to registerX)
        }
    }

    private fun updateScreen(currentInstructionCycleCost: Int) {
        for (offset in 0 until currentInstructionCycleCost) {
            if ((currentCycle + offset) % SCREEN_WIDTH in spriteRange)
                screen[currentCycle + offset] = true
        }
    }

    fun printScreen() {
        for (y in 0..5) {
            for (x in 0 until SCREEN_WIDTH)
                print(if (screen[y* SCREEN_WIDTH + x]) '#' else '.')
            println()
        }
        println()
    }
}

fun main() {
    fun parseProgram(rawInput: List<String>): List<Operation> {
        val program = ArrayList<Operation>()
        for (line in rawInput) {
            if (line.startsWith("noop"))
                program.add(Operation("noop", 1))
            else {
                val arg = line.substringAfter(' ')
                program.add(Operation("addx", 2, arg.toInt()))
            }
        }
        return program
    }

    fun part1(rawInput: List<String>): Int {
        val program = parseProgram(rawInput)
        val checkpoints = listOf(20, 60, 100, 140, 180, 220)
        val simulator = Simulator()
        for (point in checkpoints)
            simulator.addWatch(point)
        simulator.execute(program)
        for (cycleToValue in simulator.watchValues) {
            logln("Cycle ${cycleToValue.first}*${cycleToValue.second} = ${cycleToValue.first * cycleToValue.second}")
        }
        val signalStrength = simulator.watchValues.sumOf { it.first * it.second }
        return signalStrength
    }

    fun part2(rawInput: List<String>): Int {
        val program = parseProgram(rawInput)
        val simulator = Simulator()
        simulator.execute(program)
        simulator.printScreen()
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 13140)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 14320)

    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 0)

    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
