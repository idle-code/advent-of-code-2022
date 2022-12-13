package day13

import logln
import logEnabled
import readInput

private const val DAY_NUMBER = 13

fun parseFile(rawInput: List<String>): List<Pair<Packet, Packet>> {
    val iterator = rawInput.iterator()
    val pairList = mutableListOf<Pair<Packet, Packet>>()
    while (iterator.hasNext()) {
        pairList.add(parseListPair(iterator))
    }
    return pairList
}

fun parseListPair(iterator: Iterator<String>): Pair<Packet, Packet> {
    val first = parseList(iterator.next())
    val second = parseList(iterator.next())
    if (iterator.hasNext())
        iterator.next() // Consume empty line
    return Pair(first, second)
}

fun parseList(repr: String): Packet {
    val tokens = tokenize(repr)
//    logln("TOKENS: " + tokens.joinToString(":") { it })
    val packetStack = ArrayDeque<Packet>()
    for (token in tokens) {
        when (token) {
            "[" -> packetStack.addLast(Packet())
            "]" -> {
                if (packetStack.size > 1) {
                    val innerPacket = packetStack.removeLast()
                    packetStack.last().add(innerPacket)
                }
            }

            else -> packetStack.last().add(Packet(token.toInt()))
        }
    }
    check(packetStack.size == 1)
    return packetStack.first()
}

fun tokenize(expr: String): List<String> {
    val tokenList = mutableListOf<String>()
    var builder = StringBuilder()
    fun finishToken() {
        val token = builder.toString()
        if (token.isNotEmpty()) {
            tokenList.add(token)
            builder = StringBuilder()
        }
    }
    
    for (c in expr) {
        when (c) {
            '[' -> tokenList.add(c.toString())
            ']' -> {
                finishToken()
                tokenList.add(c.toString())
            }

            in "0123456789" -> builder.append(c)
            ',' -> finishToken()
            else -> throw IllegalArgumentException("Unknown packet symbol $c")
        }
    }
    return tokenList
}

class Packet : Comparable<Packet> {
    private var value: Int? = null

    private var elements: MutableList<Packet>? = null

    constructor(constant: Int) {
        value = constant
    }

    constructor(innerPackets: List<Packet> = listOf()) {
        elements = innerPackets.toMutableList()
    }

    override fun toString(): String {
        if (value != null)
            return value.toString()
        return "[" + elements!!.joinToString(", ") { it.toString() } + "]"
    }

    fun add(packet: Packet) {
        elements!!.add(packet)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Packet)
            return false
        return this.compareTo(other) == 0
    }

    override operator fun compareTo(right: Packet): Int {
        val leftValue = value
        val rightValue = right.value
        if (leftValue != null && rightValue != null) {
            return leftValue.compareTo(rightValue)
        }

        val leftElements = this.elements ?: mutableListOf(this)
        val rightElements = right.elements ?: mutableListOf(right)
        val elementPairs = leftElements.zip(rightElements)
        for (pair in elementPairs) {
            if (pair.first != pair.second) {
                return pair.first.compareTo(pair.second)
            }
        }
        return leftElements.size.compareTo(rightElements.size)
    }
}

fun main() {
    fun part1(rawInput: List<String>): Int {
        val pairList = parseFile(rawInput)
        var indexSum = 0
        pairList.forEachIndexed { index, (left, right) ->
//            logln("")
//            logln("LEFT:  $left")
//            logln("RIGHT: $right")
            if (left < right)
                indexSum += index + 1
        }
        return indexSum
    }

    fun part2(rawInput: List<String>): Int {
        val pairList = parseFile(rawInput)
        val firstKeyPacket = Packet(listOf(Packet(listOf(Packet(2)))))
        val secondKeyPacket = Packet(listOf(Packet(listOf(Packet(6)))))

        val flatList = pairList.flatMap { listOf(it.first, it.second) }
        val sortedFlatList = (flatList + listOf(firstKeyPacket, secondKeyPacket)).sorted()
        return (1 + sortedFlatList.indexOf(firstKeyPacket)) * (1 + sortedFlatList.indexOf(secondKeyPacket))
    }

    val sampleInput = readInput("sample_data", DAY_NUMBER)
    val mainInput = readInput("main_data", DAY_NUMBER)

    logEnabled = true

    val part1SampleResult = part1(sampleInput)
    println(part1SampleResult)
    check(part1SampleResult == 13)

    val part1MainResult = part1(mainInput)
    println(part1MainResult)
    check(part1MainResult == 5330)

    val part2SampleResult = part2(sampleInput)
    println(part2SampleResult)
    check(part2SampleResult == 140)

    val part2MainResult = part2(mainInput)
    println(part2MainResult)
    check(part2MainResult == 27648)
}
