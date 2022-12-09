import java.io.File
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
