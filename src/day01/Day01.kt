package day01

import readInput

data class CalorieEntry(val elfId: Int, val calories: Int)

fun parseInput(inputLines: List<String>): Iterable<CalorieEntry> {
    val caloriesPerItem = mutableListOf<CalorieEntry>()
    var elfId = 0
    for (line in inputLines) {
        if (line.isEmpty()) {
            ++elfId
            continue
        }
        caloriesPerItem.add(CalorieEntry(elfId, line.toInt()))
    }

    val totalCaloriesPerElf = caloriesPerItem.groupBy { entry ->
        entry.elfId
    }.map {
        CalorieEntry(it.key, it.value.sumOf { calorieEntry -> calorieEntry.calories })
    }

    return totalCaloriesPerElf
}

fun main() {
    fun part1(input: List<String>): Int {
        val totalCaloriesPerElf = parseInput(input)

        val maxCalorieElf = totalCaloriesPerElf.maxBy { it.calories }
        return maxCalorieElf.calories
    }

    fun part2(input: List<String>): Int {
        val totalCaloriesPerElf = parseInput(input)

        return totalCaloriesPerElf.sortedByDescending { it.calories }.take(3).sumOf { it.calories }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("sample_data", 1)
    check(part1(testInput) == 24000)

    val input = readInput("main_data", 1)
    println(part1(input))
    println(part2(input))
}
