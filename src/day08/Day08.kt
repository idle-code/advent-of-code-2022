package day08

import readInput

fun main() {
    fun visibilityHorizontally(input: List<List<Int>>, xRange: IntProgression, startVisibility: Int = -1): Array<BooleanArray> {
        val mask = Array(input.size) { BooleanArray(input[0].size) { false } }

        for (y in 0..input.lastIndex) {
            var previousTreeHeight = startVisibility
            for (x in xRange) {
                val currentHeight = input[y][x]
                if (currentHeight > previousTreeHeight) {
                    mask[y][x] = true
                    previousTreeHeight = currentHeight
                }
            }
        }

        return mask
    }

    fun visibilityVertically(input: List<List<Int>>, yRange: IntProgression, startVisibility: Int = -1): Array<BooleanArray> {
        val mask = Array(input.size) { BooleanArray(input[0].size) { false } }

        for (x in 0..input[0].lastIndex) {
            var previousTreeHeight = startVisibility
            for (y in yRange) {
                val currentHeight = input[y][x]
                if (currentHeight > previousTreeHeight) {
                    mask[y][x] = true
                    previousTreeHeight = currentHeight
                }
            }
        }

        return mask
    }

    fun mergeVisibilityMasks(vararg masks: Array<BooleanArray>): Array<BooleanArray> {
        val height = masks[0].lastIndex
        val width = masks[0][0].lastIndex
        val mergedMask = Array(height + 1) { BooleanArray(width + 1) { false } }
        for (m in masks) {
            for (y in 0..height) {
                for (x in 0..width) {
                    mergedMask[y][x] = mergedMask[y][x] || m[y][x]
                }
            }
        }
        return mergedMask
    }

    fun countAllVisible(mask: Array<BooleanArray>): Int {
        val height = mask.lastIndex
        var visibleCount = 0
        for (y in 0..height) {
            visibleCount += mask[y].count { it }
        }
        return visibleCount
    }

    fun calculateLineOfSightVertically(
        input: List<List<Int>>,
        x: Int,
        yRange: IntProgression,
        currentTreeHeight: Int
    ): Int {
        var visibleTreeCount = 0
        for (yy in yRange) {
            if (input[yy][x] >= currentTreeHeight) {
                ++visibleTreeCount
                break
            }
            ++visibleTreeCount
        }
        return visibleTreeCount
    }

    fun calculateLineOfSightHorizontally(
        input: List<List<Int>>,
        xRange: IntProgression,
        y: Int,
        currentTreeHeight: Int
    ): Int {
        var visibleTreeCount = 0
        for (xx in xRange) {
            if (input[y][xx] >= currentTreeHeight) {
                ++visibleTreeCount
                break
            }
            ++visibleTreeCount
        }
        return visibleTreeCount
    }

    fun calculateScenicScore(input: List<List<Int>>, x: Int, y: Int): Int {
        val currentTreeHeight = input[y][x]

        val visibleToLeft = calculateLineOfSightHorizontally(input, x-1 downTo 0, y, currentTreeHeight)
        val visibleToRight = calculateLineOfSightHorizontally(input, x+1..input[y].lastIndex, y, currentTreeHeight)

        val visibleToTop = calculateLineOfSightVertically(input, x, y - 1 downTo 0, currentTreeHeight)
        val visibleToBottom = calculateLineOfSightVertically(input, x, y+1 ..input.lastIndex, currentTreeHeight)

        return visibleToRight * visibleToLeft * visibleToBottom * visibleToTop
    }

    fun maxScenicScore(input: List<List<Int>>): Int {
        var maxScore = 0
        for (y in 0..input.lastIndex) {
            for (x in 0..input[y].lastIndex) {
                val scenicScore = calculateScenicScore(input, y, x)
                if (scenicScore > maxScore)
                    maxScore = scenicScore
            }
        }
        return maxScore
    }

    fun part1(rawInput: List<String>): Int {
        val input = rawInput.map { it.map { char -> char.digitToInt() } }
        val maskFromLeft = visibilityHorizontally(input, 0..input[0].lastIndex)
        val maskFromRight = visibilityHorizontally(input, input[0].lastIndex downTo 0)
        val maskFromTop = visibilityVertically(input, 0..input.lastIndex)
        val maskFromBottom = visibilityVertically(input, input.lastIndex downTo 0)
        val mergedMask = mergeVisibilityMasks(maskFromLeft, maskFromRight, maskFromTop, maskFromBottom)

        return countAllVisible(mergedMask)
    }

    fun part2(rawInput: List<String>): Int {
        val input = rawInput.map { it.map { char -> char.digitToInt() } }
        return maxScenicScore(input)
    }

    val testInput = readInput("sample_data", 8)
    println(part1(testInput))
    check(part1(testInput) == 21)

    val mainInput = readInput("main_data", 8)
    println(part1(mainInput))
    check(part1(mainInput) == 1798)

    println(part2(testInput))
    check(part2(testInput) == 8)
    println(part2(mainInput))
    check(part2(mainInput) == 259308)
}
