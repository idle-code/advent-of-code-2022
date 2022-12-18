import kotlin.math.abs

data class Position(val x: Int, val y: Int) {

    operator fun compareTo(other: Position): Int {
        val xCompare = this.x.compareTo(other.x)
        if (xCompare != 0)
            return xCompare
        return this.y.compareTo(other.y)
    }

    fun manhattanDistanceTo(other: Position): Int {
        return abs(x - other.x) + abs(y - other.y)
    }

    operator fun minus(other: Position): Position {
        return Position(this.x - other.x, this.y - other.y)
    }

    operator fun plus(other: Position): Position {
        return Position(this.x + other.x, this.y + other.y)
    }
}