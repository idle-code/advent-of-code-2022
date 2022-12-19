package day18

import logEnabled
import logln
import log
import readInput

private const val DAY_NUMBER = 18

data class Position3D(val x: Int, val y: Int, val z: Int) {
    val neighbours: Sequence<Position3D>
        get() {
            return sequenceOf(
                Position3D(x + 1, y, z),
                Position3D(x - 1, y, z),
                Position3D(x, y + 1, z),
                Position3D(x, y - 1, z),
                Position3D(x, y, z + 1),
                Position3D(x, y, z - 1),
            )
        }

    companion object {
        fun parse(line: String): Position3D {
            val (x, y, z) = line.split(',')
            return Position3D(x.toInt(), y.toInt(), z.toInt())
        }
    }
}

class PointCloud {
    private val voxelMap = mutableSetOf<Position3D>()

    fun add(voxelPosition: Position3D) {
        voxelMap.add(voxelPosition)
    }

    fun calculateSurfaceArea(): Int {
        var totalFreeSides = 0
        for (voxel in voxelMap) {
            for (neighbour in voxel.neighbours) {
                if (neighbour !in voxelMap)
                    ++totalFreeSides
            }
        }
        return totalFreeSides
    }
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val pointCloud = PointCloud()
        for (line in rawInput)
            pointCloud.add(Position3D.parse(line))
        return pointCloud.calculateSurfaceArea()
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = false

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 64)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 4340)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
//    check(part2SampleResult == 58)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
