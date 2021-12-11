package de.earley.adventofcode2021

data class Point(val x: Int, val y: Int) {
	companion object {
		// x,y
		fun parse(s: String): Point = s.split(",", limit = 2).let { (x, y) ->
			Point(x.toInt(), y.toInt())
		}
	}

	operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
	operator fun plus(other: Point) = Point(x + other.x, y + other.y)
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

open class Grid<T>(val width: Int, val height: Int, protected open val data: List<T>) {

	fun contains(x: Int, y: Int) = x in 0 until width && y in 0 until height
	operator fun contains(p: Point) = contains(p.x, p.y)

	operator fun get(x: Int, y: Int): T? = if (contains(x, y)) data[x + y * width] else null

	operator fun get(point: Point): T? = get(point.x, point.y)

	fun pointValues(): Sequence<Pair<Point, T>> = sequence {
		for (x in 0 until width) {
			for (y in 0 until height) {
				yield(Point(x, y) to get(x, y)!!)
			}
		}
	}

	fun values(): List<T> = data

	private fun indexToPoint(i: Int): Point = Point(i.mod(width), i.floorDiv(width))

	fun indexOf(t: T): Point? = data.indexOf(t).takeIf { it != -1 }?.let(this::indexToPoint)

	fun <B> map(f: (T) -> B): Grid<B> = Grid(width, height, data.map(f))
}

fun <A, B> Grid<A>.toMutableGrid(): MutableGrid<B> where A : B = MutableGrid(width, height, values().toMutableList())

class MutableGrid<T>(width: Int, height: Int, override val data: MutableList<T>) : Grid<T>(width, height, data) {
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
