package de.earley.adventofcode

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
	companion object {
		// x,y
		fun parse(s: String): Point = s.split(",", limit = 2).let { (x, y) ->
            Point(x.toInt(), y.toInt())
		}

		fun cardinals(): Sequence<Point> = sequenceOf(
            Point(0, 1),
            Point(0, -1),
            Point(1, 0),
            Point(-1, 0),
		)
	}

	operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
	operator fun plus(other: Point): Point = Point(x + other.x, y + other.y)
	operator fun div(by: Int): Point = Point(x / by, y / by)
	operator fun times(i: Int): Point = Point(x * i, y * i)
	fun divRound(by: Int, round: Double.() -> Int): Point =
        Point((x / by.toDouble()).round(), (y / by.toDouble()).round())
}

fun Point.neighbours(diagonal: Boolean = false): Sequence<Point> = sequence {
	yield(Point(x - 1, y))
	yield(Point(x + 1, y))
	yield(Point(x, y + 1))
	yield(Point(x, y - 1))
	if (diagonal) {
		yield(Point(x + 1, y + 1))
		yield(Point(x + 1, y - 1))
		yield(Point(x - 1, y + 1))
		yield(Point(x - 1, y - 1))
	}
}

fun Point.isNeighbourOrSameOf(other: Point, diagonal: Boolean = false): Boolean =
	(this.manhattanDistanceTo(other) <= 1) || (diagonal && abs(x - other.x) <= 1 && abs(y - other.y) <= 1)

fun Point.manhattanDistanceTo(to: Point): Int = abs(x - to.x) + abs(y - to.y)
fun Point.manhattanLength(): Int = abs(x) + abs(y)

data class LongPoint(val x: Long, val y: Long) {
	operator fun plus(other: Point): LongPoint =
		LongPoint(x + other.x, y + other.y)
}