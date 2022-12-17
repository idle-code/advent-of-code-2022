package day16

import logEnabled
import logln
import readInput

private const val DAY_NUMBER = 16

val valveRegex = """Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ([A-Z, ]+)""".toRegex()

typealias NodeId = String

fun parseFile(rawInput: List<String>): Map<NodeId, ValveNode> {
    val flowMap = mutableMapOf<NodeId, Int>()
    val neighbourMap = mutableMapOf<NodeId, List<NodeId>>()
    for (line in rawInput) {
        val (valveId, flowRate, rawNeighbours) = valveRegex.matchEntire(line)?.destructured
            ?: throw IllegalArgumentException("Invalid input line: $line")
        flowMap[valveId] = flowRate.toInt()
        val neighbours = rawNeighbours.split(", ")
        neighbourMap[valveId] = neighbours
    }

    val nodeMap = flowMap.map { (nodeId, flowRate) -> ValveNode(nodeId, flowRate) }.associateBy { it.id }
    neighbourMap.forEach { (nodeId, neighbours) ->
        nodeMap[nodeId]?.neighbours = neighbours.map { nodeMap[it]!! }
    }

    return nodeMap
}

data class ValveNode(val id: NodeId, val flowRate: Int) {
    var neighbours: List<ValveNode> = listOf()

    fun shortestPathTo(target: ValveNode): List<ValveNode>? {
        if (target == this)
            throw IllegalArgumentException("Target node is current node")
        val queue = ArrayDeque(listOf(listOf<ValveNode>() to this))
        val visitedNodes = mutableSetOf<ValveNode>()
        while (queue.isNotEmpty()) {
            val (path, node) = queue.removeFirst()
            if (node == target)
                return (path + node).drop(1) // Skip source node

            visitedNodes.add(node)
            queue.addAll(node.neighbours.filter { it !in visitedNodes }.map { path + node to it })
        }

        return null
    }

    override fun toString(): String {
        return "$id(flow=$flowRate)"
    }
}

data class CandidateMove(val path: List<ValveNode>, val cost: Int, val resultingFlow: Int, val benefit: Float)

fun calculateMove(source: ValveNode, destination: ValveNode, timeLeft: Int): CandidateMove? {
    val shortestPath = source.shortestPathTo(destination)!!
    val cost = shortestPath.size + 1 // Cost of getting to candidate valve + opening time
    val benefit = destination.flowRate.toFloat() / cost + destination.neighbours.size
    if (benefit > 0) {
        logln("  Benefit of moving $source -> ${shortestPath.joinToString(" -> ")}: $benefit with cost $cost")
        return CandidateMove(shortestPath, cost, destination.flowRate * (timeLeft - cost), benefit)
    }
    return null
}

fun visitValve(node: ValveNode, timeLeft: Int, openValves: Set<ValveNode>): Int {
    if (timeLeft == 0) {
        logln("Time out @ ${node.id}")
        return 0
    }

    logln("$timeLeft minutes left")

    // Find best valve to open
    val availableMoves = openValves
        .filter { it != node }
        .mapNotNull { candidate ->
            calculateMove(node, candidate, timeLeft)
        }
        .filter { it.cost < timeLeft }

    if (availableMoves.isEmpty()) {
        logln("No moves left with $timeLeft time left")
        return 0
    }

    val bestMove = availableMoves.maxBy { it.benefit }

    // Perform move
    logln("Moving to ${bestMove.path.joinToString(" -> ")}} for ${bestMove.benefit} of benefit and ${bestMove.cost} time cost")
    return bestMove.resultingFlow + visitValve(bestMove.path.last(), timeLeft - bestMove.cost, openValves - node)
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val valveMap = parseFile(rawInput)
//        valveMap.values.forEach { node ->
//            if (node.flowRate > 0)
//                println("  ${node.id} [label=\"${node.id} ${node.flowRate}\"];")
//            else
//                println("  ${node.id} [label=\"${node.id}\"];")
//            node.neighbours.forEach { neighbour ->
//                println("  ${node.id} -- ${neighbour.id};")
//            }
//        }
        val startValve = valveMap["AA"]!!

        val openValves = valveMap.values.filter { it.flowRate != 0 }.toSet()
        return visitValve(startValve, 30, openValves)
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 1651)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
//    check(part1MainResult == 0) // Greater than 1444

//    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 0)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}


