package day02

import readInput

enum class RPS(val points: Int) {
    Rock(1),
    Paper(2),
    Scissors(3)
}

enum class RoundResult(val points: Int) {
    Loose(0),
    Draw(3),
    Win(6)
}

data class TournamentPair(val opponentChoice: RPS, val roundResult: RoundResult)

val inputLineRegex = """([ABC]) ([XYZ])""".toRegex()

fun parseInput(inputLines: List<String>): Iterable<TournamentPair> {
    val pairs = mutableListOf<TournamentPair>()
    for (line in inputLines) {
        val (opponentChoiceLetter, roundResultLetter) =
            inputLineRegex.matchEntire(line)
                ?.destructured
                ?: throw IllegalArgumentException("Incorrect input line $line")

        val opponentChoice = when (opponentChoiceLetter) {
            "A" -> RPS.Rock
            "B" -> RPS.Paper
            "C" -> RPS.Scissors
            else -> throw IllegalArgumentException("Incorrect opponent choice $opponentChoiceLetter")
        }

        val roundResult = when (roundResultLetter) {
            "X" -> RoundResult.Loose
            "Y" -> RoundResult.Draw
            "Z" -> RoundResult.Win
            else -> throw IllegalArgumentException("Incorrect my choice $roundResultLetter")
        }

        pairs.add(TournamentPair(opponentChoice, roundResult))
    }

    return pairs
}


fun main() {
    fun calculateScore(pair: TournamentPair): Int {
        return when (pair.roundResult) {
            RoundResult.Loose -> 0 + when (pair.opponentChoice) {
                RPS.Rock -> RPS.Scissors.points
                RPS.Paper -> RPS.Rock.points
                RPS.Scissors -> RPS.Paper.points
            }

            RoundResult.Draw -> 3 + pair.opponentChoice.points

            RoundResult.Win -> 6 + when (pair.opponentChoice) {
                RPS.Rock -> RPS.Paper.points
                RPS.Paper -> RPS.Scissors.points
                RPS.Scissors -> RPS.Rock.points
            }
        }
    }

//    fun part1(input: List<String>): Int {
//        val pairs = parseInput(input)
//        return pairs.sumOf { calculateScore(it) }
//    }

    fun part2(input: List<String>): Int {
        val pairs = parseInput(input)
        return pairs.sumOf { calculateScore(it) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("sample_data", 2)
    //check(part1(testInput) == 15)

    val input = readInput("main_data", 2)
    //println(part1(input))
    check(part2(testInput) == 12)
    println(part2(testInput))
    println(part2(input))
}
