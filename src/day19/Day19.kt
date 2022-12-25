package day19

import logEnabled
import logln
import readInput
import kotlin.math.ceil
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

data class SimulationState(var resources: Resources, var yield: Resources)

data class FactoryBlueprint(
    val id: Int,
    val oreRobotBlueprint: RobotBlueprint,
    val clayRobotBlueprint: RobotBlueprint,
    val obsidianRobotBlueprint: RobotBlueprint,
    val geodeRobotBlueprint: RobotBlueprint
)

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

fun calculateGeodeOutputAfter24min(factoryBlueprint: FactoryBlueprint): Int {
    logln("Analyzing factory ${factoryBlueprint.id} blueprint")
    val simulationState = SimulationState(Resources(), Resources(ore = 1))

    for (minute in 1..MINUTES) {
        logln("== Minute $minute ==")
        logln("  Current state: ${simulationState.resources}")
        val currentYield = simulationState.yield
        val robotToBuy: RobotBlueprint? = selectRobotToBuy(simulationState, factoryBlueprint)
        if (robotToBuy != null) {
            logln("  Buying $robotToBuy for ${robotToBuy.cost}")
            simulationState.resources -= robotToBuy.cost
            simulationState.yield += robotToBuy.yield
        }
        logln("  Yield for next minute: ${simulationState.yield}")
        simulationState.resources += currentYield
    }

    return simulationState.resources.geode
}

fun selectRobotToBuy(simulationState: SimulationState, factoryBlueprint: FactoryBlueprint): RobotBlueprint? {
    val robots = arrayOf(
        factoryBlueprint.geodeRobotBlueprint,
        factoryBlueprint.obsidianRobotBlueprint,
        factoryBlueprint.clayRobotBlueprint,
        factoryBlueprint.oreRobotBlueprint,
    )

    val timeToBuild = robots.map { timeToBuild(simulationState, it) }
    if (simulationState.resources canBuy factoryBlueprint.geodeRobotBlueprint)
        return factoryBlueprint.geodeRobotBlueprint
    if (simulationState.resources canBuy factoryBlueprint.obsidianRobotBlueprint)
        return factoryBlueprint.obsidianRobotBlueprint
    if (simulationState.resources canBuy factoryBlueprint.clayRobotBlueprint)
        return factoryBlueprint.clayRobotBlueprint
    if (simulationState.resources canBuy factoryBlueprint.oreRobotBlueprint)
        return factoryBlueprint.oreRobotBlueprint
    return null
}

fun timeToBuild(simulationState: SimulationState, robot: RobotBlueprint): Int? {
    val oreTime = timeToCollect(simulationState, robot) { ore }
    val clayTime = timeToCollect(simulationState, robot) { clay }
    val obsidianTime = timeToCollect(simulationState, robot) { obsidian }

    val waitTimes = arrayOf(oreTime, clayTime, obsidianTime)
    if (waitTimes.any { it == null })
        return null // At least one resource collecting robot is missing
    return waitTimes.maxOfOrNull { it!! }
}

fun timeToCollect(simulationState: SimulationState, robot: RobotBlueprint, selector: Resources.() -> Int): Int? {
    val resourcePresent = selector(simulationState.resources)
    val resourceRequired = selector(robot.cost)
    if (resourcePresent >= resourceRequired)
        return 0 // Resource already in stock
    val resourceYield = selector(simulationState.yield)
    if (resourceYield == 0)
        return null // No way to acquire resource yet
    val resourceMissing = resourceRequired - resourcePresent
    val minutesToMine = resourceMissing / resourceYield.toFloat()
    return ceil(minutesToMine).roundToInt()
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        var totalQuality = 0
        for (blueprint in rawInput.map { parseFactoryBlueprint(it) }) {
            val blueprintQuality = blueprint.id * calculateGeodeOutputAfter24min(blueprint)
            if (blueprint.id == 1)
                check(blueprintQuality == 9)
            else if (blueprint.id == 2)
                check(blueprintQuality == 12)
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
