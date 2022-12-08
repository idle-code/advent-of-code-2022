package day08

import readInput
import kotlin.math.max

fun main() {
    fun visibilityHorizontally(input: List<String>, xRange: IntProgression, startVisibility: Int = -1): Array<BooleanArray> {
        val mask = Array(input.size) { BooleanArray(input[0].length) { false } }

        for (y in 0..input.lastIndex) {
            var previousTreeHeight = startVisibility
            for (x in xRange) {
                val currentHeight = input[y][x].digitToInt()
                if (currentHeight > previousTreeHeight) {
                    mask[y][x] = true
                    previousTreeHeight = currentHeight
                }
            }
        }

        return mask
    }

    fun visibilityVertically(input: List<String>, yRange: IntProgression, startVisibility: Int = -1): Array<BooleanArray> {
        val mask = Array(input.size) { BooleanArray(input[0].length) { false } }

        for (x in 0..input[0].lastIndex) {
            var previousTreeHeight = startVisibility
            for (y in yRange) {
                val currentHeight = input[y][x].digitToInt()
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

    fun countVisible(mask: Array<BooleanArray>): Int {
        val height = mask.lastIndex
        var visibleCount = 0
        for (y in 0..height) {
            visibleCount += mask[y].count { it }
        }
        return visibleCount
    }

    fun printMask(mask: Array<BooleanArray>) {
        for (y in 0..mask.lastIndex) {
            for (x in 0..mask[y].lastIndex) {
                print(if (mask[y][x]) "Y" else "." )
            }
            println()
        }
    }

    fun countScenicScore(input: List<String>, x: Int, y: Int): Int {
        val currentTreeHeight = input[y][x].digitToInt()
        var visibleToRight = 0
        for (xx in x+1..input[y].lastIndex) {
            if (input[y][xx].digitToInt() >= currentTreeHeight) {
                ++visibleToRight
                break
            }
            ++visibleToRight
        }

        var visibleToLeft = 0
        for (xx in x-1 downTo 0) {
            if (input[y][xx].digitToInt() >= currentTreeHeight) {
                ++visibleToLeft
                break
            }
            ++visibleToLeft
        }

        var visibleToBottom = 0
        for (yy in y+1 ..input.lastIndex) {
            if (input[yy][x].digitToInt() >= currentTreeHeight) {
                ++visibleToBottom
                break
            }
            ++visibleToBottom
        }

        var visibleToTop = 0
        for (yy in y-1 downTo 0) {
            if (input[yy][x].digitToInt() >= currentTreeHeight) {
                ++visibleToTop
                break
            }
            ++visibleToTop
        }

        return listOf(visibleToRight, visibleToLeft, visibleToBottom, visibleToTop)
            .foldRight(1) { current, acc -> current * acc }
    }

    fun maxScenicScore(input: List<String>): Int {
        var maxScore = 0
        for (y in 0..input.lastIndex) {
            for (x in 0..input[y].lastIndex) {
                val scenicScore = countScenicScore(input, y, x)
                if (scenicScore > maxScore)
                    maxScore = scenicScore
            }
        }
        return maxScore
    }

    fun part1(input: List<String>): Int {
        val maskFromLeft = visibilityHorizontally(input, 0..input[0].lastIndex)
        val maskFromRight = visibilityHorizontally(input, input[0].lastIndex downTo 0)
        val maskFromTop = visibilityVertically(input, 0..input.lastIndex)
        val maskFromBottom = visibilityVertically(input, input.lastIndex downTo 0)
        val mergedMask = mergeVisibilityMasks(maskFromLeft, maskFromRight, maskFromTop, maskFromBottom)
        //printMask(maskFromBottom)

        return countVisible(mergedMask)
    }


    fun part2(input: List<String>): Int {
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
