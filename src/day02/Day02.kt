package day02

import readInput

enum class RPS(val points: Int) {
    Rock(1),
    Paper(2),
    Scissors(3)
}

data class TournamentPair(val opponentChoice: RPS, val myChoice: RPS)

val inputLineRegex = """([ABC]) ([XYZ])""".toRegex()

fun parseInput(inputLines: List<String>): Iterable<TournamentPair> {
    val pairs = mutableListOf<TournamentPair>()
    for (line in inputLines) {
        val (opponentChoiceLetter, myChoiceLetter) =
            inputLineRegex.matchEntire(line)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $line")

        val opponentChoice = when (opponentChoiceLetter) {
            "A" -> RPS.Rock
            "B" -> RPS.Paper
            "C" -> RPS.Scissors
            else -> throw IllegalArgumentException("Incorrect opponent choice $opponentChoiceLetter")
        }

        val myChoice = when (myChoiceLetter) {
            "X" -> RPS.Rock
            "Y" -> RPS.Paper
            "Z" -> RPS.Scissors
            else -> throw IllegalArgumentException("Incorrect my choice $myChoiceLetter")
        }

        pairs.add(TournamentPair(opponentChoice, myChoice))
    }

    return pairs
}


fun main() {
    fun calculateScore(pair: TournamentPair): Int {
        val resultPoints = when (Pair(pair.opponentChoice, pair.myChoice)) {
            Pair(RPS.Rock, RPS.Paper), Pair(RPS.Paper, RPS.Scissors), Pair(RPS.Scissors, RPS.Rock) -> 6
            Pair(RPS.Rock, RPS.Rock), Pair(RPS.Paper, RPS.Paper), Pair(RPS.Scissors, RPS.Scissors) -> 3
            else -> 0
        }

        return resultPoints + pair.myChoice.points
    }

    fun part1(input: List<String>): Int {
        val pairs = parseInput(input)
        return pairs.sumOf { calculateScore(it) }
    }

    fun part2(input: List<String>): Int {
        //val pairs = parseInput(input)
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("sample_data", 2)
    check(part1(testInput) == 15)

    val input = readInput("main_data", 2)
    println(part1(input))
    check(part2(testInput) == 12)
    println(part2(testInput))
}
