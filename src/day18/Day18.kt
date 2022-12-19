package day18

import logEnabled
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

    operator fun minus(other: Position3D): Position3D {
        return Position3D(x - other.x, y - other.y, z - other.z)
    }

    operator fun plus(other: Position3D): Position3D {
        return Position3D(x + other.x, y + other.y, z + other.z)
    }
}

class PointCloud {
    private val voxelSet = mutableSetOf<Position3D>()

    val positions: Sequence<Position3D> = voxelSet.asSequence()

    private val visitedSet = mutableSetOf<Position3D>()

    fun add(position: Position3D) {
        voxelSet.add(position)
    }

    fun remove(position: Position3D) {
        voxelSet.remove(position)
    }

    fun boundingBox(): Pair<Position3D, Position3D> {
        val minX = voxelSet.minOf { it.x }
        val minY = voxelSet.minOf { it.y }
        val minZ = voxelSet.minOf { it.z }
        val maxX = voxelSet.maxOf { it.x }
        val maxY = voxelSet.maxOf { it.y }
        val maxZ = voxelSet.maxOf { it.z }
        return Pair(Position3D(minX, minY, minZ), Position3D(maxX, maxY, maxZ))
    }

    fun calculateSurfaceArea(): Int {
        var totalFreeSides = 0
        for (voxel in voxelSet) {
            for (neighbour in voxel.neighbours) {
                if (neighbour !in voxelSet)
                    ++totalFreeSides
            }
        }
        return totalFreeSides
    }

    fun visitConnectedTo(position: Position3D) {
        val toVisit = ArrayDeque(listOf(position))
        while (toVisit.isNotEmpty()) {
            val visited = toVisit.removeFirst()
            if (visitedSet.add(visited))
                toVisit.addAll(visited.neighbours.filter { n -> n in voxelSet && n !in visitedSet })
        }
    }

    fun removeUnvisited() {
        val unvisitedPositions = positions.filter { it !in visitedSet }.toList()
        voxelSet.removeAll(unvisitedPositions.toSet())
    }
}


private fun Pair<Position3D, Position3D>.calculateSurfaceArea(): Int {
    val min = this.first
    val max = this.second + Position3D(1, 1, 1)
    check(max.x > min.x)
    check(max.y > min.y)
    check(max.z > min.z)
    val xSide = (max.z - min.z) * (max.y - min.y)
    val ySide = (max.z - min.z) * (max.x - min.x)
    val zSide = (max.x - min.x) * (max.y - min.y)
    return 2 * (xSide + ySide + zSide)
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val pointCloud = PointCloud()
        val points = rawInput.map { Position3D.parse(it) }
        for (point in points)
            pointCloud.add(point)
        return pointCloud.calculateSurfaceArea()
    }

    fun part2(rawInput: List<String>): Int {
        val positivePointCloud = PointCloud()
        val points = rawInput.map { Position3D.parse(it) }
        for (point in points)
            positivePointCloud.add(point)

        // Calculate bounding box
        var (minPosition, maxPosition) = positivePointCloud.boundingBox()
        val margin = 1
        val unitVector = Position3D(margin, margin, margin)
        minPosition -= unitVector
        maxPosition += unitVector

        // Fill negative cloud
        val negativePointCloud = PointCloud()
        for (z in minPosition.z..maxPosition.z) {
            for (y in minPosition.y..maxPosition.y) {
                for (x in minPosition.x..maxPosition.x) {
                    negativePointCloud.add(Position3D(x, y, z))
                }
            }
        }

        // Subtract real cloud from the negative
        for (point in positivePointCloud.positions)
            negativePointCloud.remove(point)

        // Flood fill from the outside and remove
        negativePointCloud.visitConnectedTo(minPosition)
        negativePointCloud.removeUnvisited()
        val negativeArea = negativePointCloud.calculateSurfaceArea()
        check(Pair(Position3D(-1, -1, -1), Position3D(1, 1, 1)).calculateSurfaceArea() == 9 * 6)
        val boxArea = Pair(minPosition, maxPosition).calculateSurfaceArea()
        return negativeArea - boxArea
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
    check(part2SampleResult == 58)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
    check(part2MainResult == 2468)
}
