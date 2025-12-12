package de.earley.adventofcode

import kotlin.math.sign

open class Grid<T>(
	val width: Int,
	val height: Int,
	protected open val data: List<T>,
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

	data class PointValue<T>(val point: Point, val value: T)

	fun pointValues(): Sequence<PointValue<T>> = indices.map {
		@Suppress("UNCHECKED_CAST") // always a valid point
		PointValue(it, get(it) as T)
	}

	fun values(): List<T> = data

	private fun indexToPoint(i: Int): Point = Point(i.mod(width), i.floorDiv(width))

	fun indexOf(t: T): Point? = data.indexOf(t).takeIf { it != -1 }?.let(this::indexToPoint)

	fun <B> map(f: (T) -> B): Grid<B> = Grid(width, height, data.map(f))

	@Suppress("unused")
	fun <B> mapIndexed(f: (Point, T) -> B): Grid<B> = Grid(
		width,
		height,
		data.mapIndexed { index, t ->
			f(Point(index.mod(width), index.floorDiv(width)), t)
		}
	)

	fun getColumn(x: Int): List<T> = List(height) { y -> get(x, y)!! }
	fun getRow(y: Int): List<T> = List(width) { x -> get(x, y)!! }
}

@Suppress("unused")
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
		width,
		height,
		List(width * height) {
			content(Point(it.mod(width), it.floorDiv(width)))
		}
	)

fun <T> Sequence<String>.toGrid(f: (Char) -> T): Grid<T> = toList().toGrid(f)

fun <T> List<String>.toGrid(f: (Char) -> T): Grid<T> {
	return Grid(this.first().length, this.size, this.flatMap { it.map(f) })
}

fun <T> Grid<T>.floodFill(from: Point, condition: (T) -> Boolean): Set<Point> {
	val open = mutableSetOf(from)
	val visited = mutableSetOf<Point>()
	while (open.isNotEmpty()) {
		val next = open.first()
		open.remove(next)
		visited += next
		open += next.neighbours(false)
			.filter { it in this }
			.filter { it !in visited }
			.filter { condition(this[it]!!) }
	}
	return visited
}

fun <A, B> Grid<A>.toMutableGrid(): MutableGrid<B> where A : B = MutableGrid(width, height, values().toMutableList())

class MutableGrid<T>(
	width: Int,
	height: Int,
	override val data: MutableList<T>,
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

