package de.earley.adventofcode

import kotlin.math.abs
import kotlin.math.sign

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


open class Grid<T>(
	val width: Int,
	val height: Int,
	protected open val data: List<T>
) {

	val indices: Sequence<Point>
		get() = sequence {
			for (x in 0 until width) {
				for (y in 0 until height) {
					yield(Point(x, y))
				}
			}
		}

	fun contains(x: Int, y: Int) = x in 0 until width && y in 0 until height
	operator fun contains(p: Point) = contains(p.x, p.y)

	operator fun get(x: Int, y: Int): T? = if (contains(x, y)) data[x + y * width] else null

	operator fun get(point: Point): T? = get(point.x, point.y)

	fun pointValues(): Sequence<Pair<Point, T>> = indices.map {
		@Suppress("UNCHECKED_CAST") // always a valid point
		it to get(it) as T
	}

	fun values(): List<T> = data

	private fun indexToPoint(i: Int): Point = Point(i.mod(width), i.floorDiv(width))

	fun indexOf(t: T): Point? = data.indexOf(t).takeIf { it != -1 }?.let(this::indexToPoint)

	fun <B> map(f: (T) -> B): Grid<B> = Grid(width, height, data.map(f))
}

fun <T> Grid<T>.prettyPrint() {
	for (y in 0 until height) {
		for (x in 0 until width) {
			print(get(x, y))
		}
		println()
	}
	println()
}

fun <T> grid(width: Int, height: Int, content: (Point) -> T): Grid<T> =
	Grid(
		width, height,
		List(width * height) {
			content(Point(it.mod(width), it.floorDiv(width)))
		}
	)

fun <A, B> Grid<A>.toMutableGrid(): MutableGrid<B> where A : B = MutableGrid(width, height, values().toMutableList())

class MutableGrid<T>(
	width: Int,
	height: Int,
	override val data: MutableList<T>
) : Grid<T>(width, height, data) {
	operator fun set(x: Int, y: Int, t: T) {
		data[x + y * width] = t
	}

	operator fun set(p: Point, t: T) = set(p.x, p.y, t)

	fun mutate(f: (T) -> T) {
		for (i in data.indices) {
			data[i] = f(data[i])
		}
	}
}


fun List<Point>.toGrid(padding: Point = Point(0, 0)): Grid<Boolean> {
	val minX = minOf { it.x }
	val maxX = maxOf { it.x }
	val minY = minOf { it.y }
	val maxY = maxOf { it.y }

	// because 0 is also a coordinate
	val extraPadding = Point(
		if (maxX.sign != minX.sign) 1 else 0,
		if (maxY.sign != minY.sign) 1 else 0
	)

	val width = maxX - minX + 2 * padding.x + extraPadding.x
	val height = maxY - minY + 2 * padding.y + extraPadding.y
	val adjust = Point(minX - padding.x, minY - padding.y)

	return grid(width, height) { p ->
		(p + adjust) in this
	}
}