package day11

import logEnabled
import logln
import readInput

private const val DAY_NUMBER = 11

typealias WorryLevel = Int
typealias MonkeyId = Int

class Monkey(
    val id: MonkeyId,
    startingItems: List<WorryLevel>,
    private val operation: Operation,
    private val test: TestOperation,
    private val successTarget: MonkeyId,
    private val failureTarget: MonkeyId
) {
    val items: MutableList<WorryLevel> = startingItems.toMutableList()

    var inspectionCounter: Int = 0

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
                token.toInt()
        }

        override fun toString(): String = "new = $left $op $right"
    }

    class TestOperation(private val number: Int) {
        fun run(testedValue: Int): Boolean {
            return testedValue % number == 0
        }

        override fun toString(): String = "divisible by $number"
    }

    fun inspect(level: WorryLevel): Pair<WorryLevel, MonkeyId> {
        ++inspectionCounter

        var newLevel = operation.run(level)
//        logln("    Worry level increases to $newLevel.")
        newLevel /= 3
//        logln("    Monkey gets bored with item. Worry level is divided by 3 to $newLevel.")
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
        val startingItems = iterator.next().substringAfter("  Starting items: ").split(", ").map { it.toInt() }

        val expr = iterator.next().substringAfter("  Operation: new = ")
        val (left, op, right) = expr.split(' ')
        val operation = Monkey.Operation(left, op, right)

        val testOperation = Monkey.TestOperation(iterator.next().substringAfter("  Test: divisible by ").toInt())
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

    fun simulateRound(monkeys: List<Monkey>) {
        for (monkey in monkeys) {
//            logln("Monkey ${monkey.id}:")
            while (monkey.items.isNotEmpty()) {
                val itemLevel = monkey.items.removeFirst()
//                logln("  Monkey inspects an item with a worry level of $itemLevel.")
                val (newItemLevel, targetMonkey) = monkey.inspect(itemLevel)
//                logln("    Item with worry level $newItemLevel is thrown to monkey $targetMonkey.")
                monkeys[targetMonkey].items.add(newItemLevel)
            }
        }
    }

    fun part1(rawInput: List<String>): Int {
        val monkeys = parseInput(rawInput)

        for (round in 1..20) {
            logln("Round $round")
            simulateRound(monkeys)
        }

        for (monkey in monkeys) {
            logln("Monkey ${monkey.id} inspected items ${monkey.inspectionCounter} times.")
        }
        val monkeyInspections = monkeys.map { it.inspectionCounter }.sortedDescending()
        return monkeyInspections[0] * monkeyInspections[1]
    }

    fun part2(rawInput: List<String>): Int {
        val monkeys = parseInput(rawInput)

        val testRound = listOf(1, 20, 1000, 2000)
        for (round in 1..10000) {
//            logln("Round $round")
            simulateRound(monkeys)

            if (round in testRound) {
                logln("== After round $round ==")
                for (monkey in monkeys) {
                    logln("Monkey ${monkey.id} inspected items ${monkey.inspectionCounter} times.")
                }
            }
        }

        val monkeyInspections = monkeys.map { it.inspectionCounter }.sortedDescending()
        return monkeyInspections[0] * monkeyInspections[1]
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

//    val part1SampleResult = part1(sampleInput)
//    println(part1SampleResult)
//    check(part1SampleResult == 10605)
//
//    val part1MainResult = part1(mainInput)
//    println(part1MainResult)
//    check(part1MainResult == 66802)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
//    check(part2SampleResult == 0)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
