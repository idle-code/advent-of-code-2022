package day19

import logEnabled
import logln
import readInput
import java.util.PriorityQueue
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

private const val DAY_NUMBER = 19

val blueprintRegex =
    """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()


data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
    operator fun plus(other: Resources): Resources {
        return Resources(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)
    }

    operator fun minus(other: Resources): Resources {
        return Resources(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)
    }

}

infix fun Resources.canBuy(robot: RobotBlueprint): Boolean {
    return ore >= robot.cost.ore && clay >= robot.cost.clay && obsidian >= robot.cost.obsidian && geode >= robot.cost.geode
}

data class RobotBlueprint(val name: String, val cost: Resources, val yield: Resources) {
    override fun toString(): String = "$name robot"
}

data class SimulationState(val resources: Resources, val yield: Resources, val minutesLeft: Int) : Comparable<SimulationState> {
    override fun compareTo(other: SimulationState): Int {
        return minutesLeft.compareTo(other.minutesLeft)
    }
}

data class FactoryBlueprint(
    val id: Int,
    val oreRobotBlueprint: RobotBlueprint,
    val clayRobotBlueprint: RobotBlueprint,
    val obsidianRobotBlueprint: RobotBlueprint,
    val geodeRobotBlueprint: RobotBlueprint
) {
    val maxYield: Resources

    init {
        val robots = listOf(oreRobotBlueprint, clayRobotBlueprint, obsidianRobotBlueprint, geodeRobotBlueprint)
        val maxOreYield = robots.maxOf { it.cost.ore }
        val maxClayYield = robots.maxOf { it.cost.clay }
        val maxObsidianYield = robots.maxOf { it.cost.obsidian }
        maxYield = Resources(maxOreYield, maxClayYield, maxObsidianYield)
    }
}

fun parseFactoryBlueprint(line: String): FactoryBlueprint {
    val (id, oreRobotOreCost, clayRobotOreCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost) =
        blueprintRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException("Invalid input line")

    return FactoryBlueprint(
        id.toInt(),
        RobotBlueprint(
            "Ore",
            cost = Resources(ore = oreRobotOreCost.toInt()),
            yield = Resources(ore = 1)
        ),
        RobotBlueprint(
            "Clay",
            cost = Resources(ore = clayRobotOreCost.toInt()),
            yield = Resources(clay = 1)
        ),
        RobotBlueprint(
            "Obsidian",
            cost = Resources(ore = obsidianRobotOreCost.toInt(), clay = obsidianRobotClayCost.toInt()),
            yield = Resources(obsidian = 1)
        ),
        RobotBlueprint(
            "Geode",
            cost = Resources(ore = geodeRobotOreCost.toInt(), obsidian = geodeRobotObsidianCost.toInt()),
            yield = Resources(geode = 1)
        )
    )
}

private const val MINUTES = 24

fun simulate(factoryBlueprint: FactoryBlueprint): SimulationState {
    val startState = SimulationState(Resources(), Resources(ore = 1), MINUTES)
    //val queue = ArrayDeque<SimulationState>().apply { add(startState) }
    val queue = PriorityQueue<SimulationState>().apply { add(startState) }
    var bestState = startState
    while (queue.isNotEmpty()) {
        //val state = queue.removeFirst()
        val state = queue.remove()
        if (state.minutesLeft == 0) {
            if (bestState.resources.geode < state.resources.geode) {
                logln("Found better state with ${state.resources.geode} output")
                bestState = state
            }
        }
        else
            queue.addAll(possibleStatesFrom(factoryBlueprint, state))
    }
    return bestState
}

fun possibleStatesFrom(factoryBlueprint: FactoryBlueprint, simulationState: SimulationState): List<SimulationState> {
    if (simulationState.minutesLeft <= 0)
        return listOf() // No more time to simulate

    val possibleStates = mutableListOf(simulationState) // Do not buy anything
    if (simulationState.resources canBuy factoryBlueprint.geodeRobotBlueprint) {
        possibleStates.clear()
        possibleStates.add(buyRobot(simulationState, factoryBlueprint.geodeRobotBlueprint))
    }
    else {
        if (simulationState.resources canBuy factoryBlueprint.obsidianRobotBlueprint && simulationState.yield.obsidian < factoryBlueprint.maxYield.obsidian)
            possibleStates.add(buyRobot(simulationState, factoryBlueprint.obsidianRobotBlueprint))

        if (simulationState.resources canBuy factoryBlueprint.clayRobotBlueprint && simulationState.yield.clay < factoryBlueprint.maxYield.clay)
            possibleStates.add(buyRobot(simulationState, factoryBlueprint.clayRobotBlueprint))

        if (simulationState.resources canBuy factoryBlueprint.oreRobotBlueprint && simulationState.yield.ore < factoryBlueprint.maxYield.ore)
            possibleStates.add(buyRobot(simulationState, factoryBlueprint.oreRobotBlueprint))
    }

    return possibleStates.map { state -> SimulationState(state.resources + simulationState.yield, state.yield, simulationState.minutesLeft - 1) }
}

fun buyRobot(simulationState: SimulationState, robotToBuy: RobotBlueprint): SimulationState {
    return SimulationState(simulationState.resources - robotToBuy.cost, simulationState.yield + robotToBuy.yield, simulationState.minutesLeft)
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        var totalQuality = 0
        for (blueprint in rawInput.map { parseFactoryBlueprint(it) }) {
            val bestState = simulate(blueprint)
            val blueprintQuality = blueprint.id * bestState.resources.geode
            if (blueprint.id == 1)
                check(blueprintQuality == 9)
            else if (blueprint.id == 2)
                check(blueprintQuality == 2 * 12)
            totalQuality += blueprintQuality
        }
        return totalQuality
    }

    fun part2(rawInput: List<String>): Int {
        return 0
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 33)

//    val part1MainResult = part1(mainInput)
//    println(part1MainResult)
//    check(part1MainResult == 0)

//    val part2SampleResult = part2(sampleInput)
//    println(part2SampleResult)
//    check(part2SampleResult == 0)

//    val part2MainResult = part2(mainInput)
//    println(part2MainResult)
//    check(part2MainResult == 0)
}
