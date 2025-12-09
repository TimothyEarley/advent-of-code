package de.earley.adventofcode2025.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.pairs
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() = Day9.start()

object Day9 : BaseSolution<List<Point>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Point> = input.mapToList { Point.parse(it) }

	override fun partOne(data: List<Point>): Long = data.pairs().maxOf(this::area)

	override fun partTwo(data: List<Point>): Long {
		val edges = (data.zipWithNext() + (data.last() to data.first()))
			.map { (a, b) ->
				Line(
					Point(min(a.x, b.x), min(a.y, b.y)),
					Point(max(a.x, b.x), max(a.y, b.y))
				)
			}

		return data.pairs()
			.filter { (a, b) ->
				val xMin = min(a.x, b.x)
				val xMax = max(a.x, b.x)
				val yMin = min(a.y, b.y)
				val yMax = max(a.y, b.y)

				// does the rectangle contain any of the ends of the edges?
				edges.none { e ->
					xMin < e.end.x && xMax > e.start.x && yMin < e.end.y && yMax > e.start.y
				}
			}.maxOf { area(it) }
	}

	private data class Line(val start: Point, val end: Point)

	private fun area(pair: Pair<Point, Point>): Long =
		(abs(pair.first.x - pair.second.x) + 1).toLong() * (abs(pair.first.y - pair.second.y) + 1)

}