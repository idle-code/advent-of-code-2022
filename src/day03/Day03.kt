package day03

import readInput

fun main() {
    fun priorityOf(itemType: Char): Int {
        return when (itemType) {
            in 'a'..'z' -> itemType - 'a' + 1
            in 'A'..'Z' -> itemType - 'A' + 27
            else -> throw IllegalArgumentException("Invalid item type: $itemType")
        }
    }

    fun part1(input: List<String>): Int {
        var totalPriority = 0
        for (rucksack in input) {
            check(rucksack.length % 2 == 0)
            val left = rucksack.substring(0, rucksack.length / 2).toSet()
            val right = rucksack.substring(rucksack.length / 2).toSet()

            val commonItemSet = left intersect right
            check(commonItemSet.size == 1)
            val commonItem = commonItemSet.first()
            totalPriority += priorityOf(commonItem)
        }
        return totalPriority
    }

    fun part2(input: List<String>): Int {
        var totalPriority = 0
        for (groupRucksacks in input.windowed(3, 3)) {
            val (elf1, elf2, elf3) = groupRucksacks.map { it.toSet() }
            val badgeItemSet = elf1 intersect elf2 intersect elf3
            check(badgeItemSet.size == 1)
            val badgeItem = badgeItemSet.first()
            totalPriority += priorityOf(badgeItem)
        }
        return totalPriority
    }

    val testInput = readInput("sample_data", 3)
    println(part1(testInput))
    check(part1(testInput) == 157)

    val mainInput = readInput("main_data", 3)
    println(part1(mainInput))
    check(part1(mainInput) == 8018)

    println(part2(testInput))
    check(part2(testInput) == 70)
    println(part2(mainInput))
    check(part2(mainInput) == 2518)
}
