package day05

import readInput

typealias Crate = Char

val commandLineRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

fun main() {
    fun part1(input: List<String>): String {
        // Parse state representation
        val stateRepresentation = input.takeWhile { it.isNotEmpty() }
        val indexLine = stateRepresentation.last()
        val stackCount = indexLine[indexLine.length - 2].digitToInt()
        val stacks = Array(stackCount) { ArrayDeque<Crate>() }
        for (level in (stateRepresentation.size - 2) downTo 0) {
            for (stackId in 0 until stackCount) {
                val crate = stateRepresentation[level][1 + stackId * 4]
                if (crate != ' ')
                    stacks[stackId].addLast(crate)
            }
        }

        // Parse rearrangement procedure
        for (commandLine in input.drop(stateRepresentation.size + 1)) {
            val (cratesCount, sourceStackIndex, destinationStackIndex) =
                commandLineRegex.matchEntire(commandLine)?.groupValues?.drop(1)?.map { it.toInt() }
                    ?: throw IllegalArgumentException("Incorrect command line $commandLine")
            for (i in 0 until cratesCount) {
                val movedCrate = stacks[sourceStackIndex - 1].removeLast()
                stacks[destinationStackIndex - 1].addLast(movedCrate)
            }
        }

        // Find top of the stacks
        val stacksTop = StringBuilder()
        for (stack in stacks)
            stacksTop.append(stack.last())
        return stacksTop.toString()
    }

    fun part2(input: List<String>): String {
        // Parse state representation
        val stateRepresentation = input.takeWhile { it.isNotEmpty() }
        val indexLine = stateRepresentation.last()
        val stackCount = indexLine[indexLine.length - 2].digitToInt()
        val stacks = Array(stackCount) { ArrayDeque<Crate>() }
        for (level in (stateRepresentation.size - 2) downTo 0) {
            for (stackId in 0 until stackCount) {
                val crate = stateRepresentation[level][1 + stackId * 4]
                if (crate != ' ')
                    stacks[stackId].addLast(crate)
            }
        }

        // Parse rearrangement procedure
        for (commandLine in input.drop(stateRepresentation.size + 1)) {
            val (cratesCount, sourceStackIndex, destinationStackIndex) =
                commandLineRegex.matchEntire(commandLine)?.groupValues?.drop(1)?.map { it.toInt() }
                    ?: throw IllegalArgumentException("Incorrect command line $commandLine")

            val tempStack = ArrayList<Crate>()
            for (i in 0 until cratesCount) {
                val movedCrate = stacks[sourceStackIndex - 1].removeLast()
                tempStack.add(movedCrate)
            }
            stacks[destinationStackIndex - 1].addAll(tempStack.reversed())
        }

        // Find top of the stacks
        val stacksTop = StringBuilder()
        for (stack in stacks)
            stacksTop.append(stack.last())
        return stacksTop.toString()
    }

    val testInput = readInput("sample_data", 5)
    println(part1(testInput))
    check(part1(testInput) == "CMZ")

    val mainInput = readInput("main_data", 5)
    println(part1(mainInput))
    check(part1(mainInput) == "WSFTMRHPP")

    println(part2(testInput))
    check(part2(testInput) == "MCD")
    println(part2(mainInput))
    check(part2(mainInput) == "GSLCMFBRP")
}
