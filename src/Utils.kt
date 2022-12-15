import java.io.File
import java.lang.IllegalStateException
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String, dayNumber: Int) = File("src/day${dayNumber.toString().padStart(2, '0')}", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * Conditional message printing
 */
var logEnabled: Boolean = false

fun <T> log(message: T) {
    if (!logEnabled)
        return
    print(message.toString())
}

fun <T> logln(message: T) {
    if (!logEnabled)
        return
    println(message.toString())
}

typealias Grid<T> = Array<Array<T>>

fun <T> Grid<T>.width(): Int {
    if (this.isEmpty())
        throw IllegalStateException("Couldn't determine width without grid data")
    return this[0].size
}

fun <T> Grid<T>.height(): Int {
    return this.size
}

fun <T> Grid<T>.contains(x: Int, y: Int): Boolean {
    return x in 0 until this.width() && y in 0 until this.height()
}
