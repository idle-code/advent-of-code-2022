package day10

import logEnabled
import readInput

private const val DAY_NUMBER = 10


data class Operation(val opcode: String, val arg: Int? = null)

class Simulator {
    var registerX = 1

    var currentCycle = 0

    val breakpointValues = ArrayList<Pair<Int, Int>>()

    private val breakpoints = HashSet<Int>()

    fun addBreakpoint(cycle: Int) {
        breakpoints.add(cycle)
    }

    fun execute(operations: List<Operation>) {
        for (operation in operations) {
            if (currentCycle > 218)
                currentCycle += 0
            execute(operation)
        }
    }

    private fun breakpoint(currentInstructionCycleCost: Int) {
        for (cycle in currentCycle+1 ..currentCycle + currentInstructionCycleCost) {
            if (cycle in breakpoints)
                breakpointValues.add(Pair(cycle, registerX))
        }
    }

    fun execute(operation: Operation) {
        when (operation.opcode) {
            "noop" -> {
                breakpoint(1)
                currentCycle += 1
            }
            "addx" -> {
                breakpoint(2)
                registerX += operation.arg!!
                currentCycle += 2
            }
            else -> throw IllegalArgumentException("Unknown opcode: ${operation.opcode}")
        }
    }
}

fun main() {
    fun parseProgram(rawInput: List<String>): List<Operation> {
        val program = ArrayList<Operation>()
        for (line in rawInput) {
            if (line.startsWith("noop"))
                program.add(Operation(line))
            else {
                val operation = line.substringBefore(' ')
                val arg = line.substringAfter(' ')

                program.add(Operation(operation, arg.toInt()))
            }
        }
        return program
    }

    fun part1(rawInput: List<String>): Int {
        val program = parseProgram(rawInput)
        val checkpoints = listOf(20, 60, 100, 140, 180, 220)
        val simulator = Simulator()
        for (point in checkpoints)
            simulator.addBreakpoint(point)
        simulator.execute(program)
        for (kv in simulator.breakpointValues) {
            println("Cycle ${kv.first}*${kv.second} = ${kv.first * kv.second}")
        }
        val signalStrength = simulator.breakpointValues.map { it.first * it.second }.sum()
        return signalStrength
    }

    fun part2(rawInput: List<String>): Int {
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
