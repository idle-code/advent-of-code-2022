package day05

import readInput

typealias Crate = Char

val commandLineRegex = """move (\d+) from (\d+) to (\d+)""".toRegex()

data class CraneCommand(
    val cratesCount: Int,
    val sourceStack: ArrayDeque<Crate>,
    val destinationStack: ArrayDeque<Crate>
)

fun main() {
    fun parseInput(input: List<String>): Pair<Array<ArrayDeque<Crate>>, List<CraneCommand>> {
        // Find out stack count
        val stateRepresentation = input.takeWhile { it.isNotEmpty() }
        val indexLine = stateRepresentation.last()
        val stackCount = indexLine[indexLine.length - 2].digitToInt()
        val stacks = Array(stackCount) { ArrayDeque<Crate>() }

        // Parse state representation
        for (level in (stateRepresentation.size - 2) downTo 0) {
            for (stackId in 0 until stackCount) {
                val crate = stateRepresentation[level][1 + stackId * 4]
                if (crate != ' ')
                    stacks[stackId].addLast(crate)
            }
        }

        // Parse crane commands
        val craneCommands = ArrayList<CraneCommand>()
        for (commandLine in input.drop(stateRepresentation.size + 1)) {
            val (cratesCount, sourceStackIndex, destinationStackIndex) =
                commandLineRegex.matchEntire(commandLine)?.groupValues?.drop(1)?.map { it.toInt() }
                    ?: throw IllegalArgumentException("Incorrect command line $commandLine")

            craneCommands.add(
                CraneCommand(
                    cratesCount,
                    stacks[sourceStackIndex - 1],
                    stacks[destinationStackIndex - 1]
                )
            )
        }

        return Pair(stacks, craneCommands)
    }

    fun listTopCreates(stacks: Array<ArrayDeque<Crate>>): String {
        val stacksTop = StringBuilder()
        for (stack in stacks)
            stacksTop.append(stack.last())
        return stacksTop.toString()
    }

    fun part1(input: List<String>): String {
        val (stacks, craneCommands) = parseInput(input)

        for (command in craneCommands) {
            for (i in 0 until command.cratesCount) {
                val movedCrate = command.sourceStack.removeLast()
                command.destinationStack.addLast(movedCrate)
            }
        }

        // Find top of the stacks
        return listTopCreates(stacks)
    }

    fun part2(input: List<String>): String {
        val (stacks, craneCommands) = parseInput(input)

        for (command in craneCommands) {
            val tempStack = ArrayList<Crate>()
            for (i in 0 until command.cratesCount) {
                val movedCrate = command.sourceStack.removeLast()
                tempStack.add(movedCrate)
            }
            command.destinationStack.addAll(tempStack.reversed())
        }

        // Find top of the stacks
        return listTopCreates(stacks)
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
