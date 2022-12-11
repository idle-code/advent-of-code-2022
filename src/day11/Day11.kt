package day11

import logEnabled
import readInput

private const val DAY_NUMBER = 11

typealias WorryLevel = Long
typealias MonkeyId = Int

class Monkey(
    val id: MonkeyId,
    startingItems: List<WorryLevel>,
    private val operation: Operation,
    val test: TestOperation,
    private val successTarget: MonkeyId,
    private val failureTarget: MonkeyId
) {
    val items: MutableList<WorryLevel> = startingItems.toMutableList()

    var inspectionCounter: Long = 0

    class Operation(private val left: String, private val op: String, private val right: String) {
        fun run(level: WorryLevel): WorryLevel {
            val leftValue = tokenToValue(left, level)
            val rightValue = tokenToValue(right, level)
            return when (op) {
                "+" -> leftValue + rightValue
                "*" -> leftValue * rightValue
                else -> throw IllegalArgumentException("Invalid operand $op")
            }
        }

        private fun tokenToValue(token: String, oldValue: WorryLevel): WorryLevel {
            return if (token == "old")
                oldValue
            else
                token.toLong()
        }

        override fun toString(): String = "new = $left $op $right"
    }

    class TestOperation(val number: Long) {
        fun run(testedValue: WorryLevel): Boolean {
            return testedValue % number == 0L
        }

        override fun toString(): String = "divisible by $number"
    }

    fun inspect(level: WorryLevel, worryLevelDecreaser: (WorryLevel) -> WorryLevel): Pair<WorryLevel, MonkeyId> {
        ++inspectionCounter

        var newLevel = operation.run(level)
        newLevel = worryLevelDecreaser(newLevel)
        return if (test.run(newLevel))
            newLevel to successTarget
        else
            newLevel to failureTarget
    }

    override fun toString(): String {
        return """Monkey $id:
  Items: ${items.joinToString(", ")}
  Operation: $operation
  Test: $test
    If true: throw to monkey $successTarget
    If false: throw to monkey $failureTarget"""
    }
}

fun main() {
    fun parseMonkey(iterator: Iterator<String>): Monkey {
        val monkeyId = iterator.next().substringAfter("Monkey ").substringBefore(':').toInt()
        val startingItems = iterator.next().substringAfter("  Starting items: ").split(", ").map { it.toLong() }

        val expr = iterator.next().substringAfter("  Operation: new = ")
        val (left, op, right) = expr.split(' ')
        val operation = Monkey.Operation(left, op, right)

        val testOperation = Monkey.TestOperation(iterator.next().substringAfter("  Test: divisible by ").toLong())
        val successTarget = iterator.next().substringAfter("    If true: throw to monkey ").toInt()
        val failureTarget = iterator.next().substringAfter("    If false: throw to monkey ").toInt()

        if (iterator.hasNext())
            iterator.next() // Consume separating empty line

        return Monkey(monkeyId, startingItems, operation, testOperation, successTarget, failureTarget)
    }

    fun parseInput(rawInput: List<String>): List<Monkey> {
        val iterator = rawInput.iterator()
        val monkeys = mutableListOf<Monkey>()
        while (iterator.hasNext()) {
            monkeys.add(parseMonkey(iterator))
        }
        return monkeys
    }

    fun simulateRound(monkeys: List<Monkey>, worryLevelDecreaser: (WorryLevel) -> WorryLevel) {
        for (monkey in monkeys) {
            while (monkey.items.isNotEmpty()) {
                val itemLevel = monkey.items.removeFirst()
                val (newItemLevel, targetMonkey) = monkey.inspect(itemLevel, worryLevelDecreaser)
                monkeys[targetMonkey].items.add(newItemLevel)
            }
        }
    }

    fun calculateMonkeyBusiness(monkeys: List<Monkey>): Long {
        val monkeyInspections = monkeys.map { it.inspectionCounter }.sortedDescending()
        return monkeyInspections[0] * monkeyInspections[1]
    }

    fun part1(rawInput: List<String>): Long {
        val monkeys = parseInput(rawInput)

        for (round in 1..20) {
            simulateRound(monkeys) { level -> level / 3 }
        }

        return calculateMonkeyBusiness(monkeys)
    }

    fun part2(rawInput: List<String>): Long {
        val monkeys = parseInput(rawInput)
        val leastCommonMultiplier = monkeys.map { it.test.number }.reduce { acc, l -> acc * l }
        for (round in 1..10000) {
            simulateRound(monkeys) { level -> level % leastCommonMultiplier }
        }

        return calculateMonkeyBusiness(monkeys)
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 10605L)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 66802L)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
    check(part2SampleResult == 2713310158)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
    check(part2MainResult == 21800916620)
}
