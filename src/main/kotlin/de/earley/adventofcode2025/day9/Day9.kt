package de.earley.adventofcode2025.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode.mapToList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() = Day9.start()

object Day9 : BaseSolution<List<Point>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Point> = input.mapToList { Point.parse(it) }

	override fun partOne(data: List<Point>): Long = data.pairs().maxOf {
		(abs(it.first.x - it.second.x) + 1).toLong() * (abs(it.first.y - it.second.y) + 1)
	}

	enum class Shape(
		val isInteriorRight: Boolean,
		val isInteriorBottom: Boolean,
		val isInteriorLeft: Boolean,
		val isInteriorTop: Boolean,
	) {
		/*  xxx
		 *  xP-
 		 *  x|
 		 */
		TopLeftOutside(true, true, true, true),

		/*   P-
		 *   |x
		 */
		TopLeftInside(true, true, false, false),

		/*  xxx
		 *  -Px
		 *   |x
		 */
		TopRightOutside(true, true, true, true),

		/*   -P
		 *   x|
 		 */
		TopRightInside(false, true, true, false),


		/*    |x
		 *   -Px
		 *   xxx
		 */
		BottomRightOutside(true, true, true, true),

		/*   x|
		 *   -P
		 */
		BottomRightInside(false, false, true, true),

		/*
		 *  x|
		 *  xP-
		 *  xxx
		 */
		BottomLeftOutside(true, true, true, true),

		/*  |x
		 *  P-
		 */
		BottomLeftInside(true, false, false, true),
	}

	override fun partTwo(data: List<Point>): Long {

		// Note: This all assumes the points are in clockwise order!

		val pointShapes = data.withIndex().associate { (i, p) ->
			val prev = data[(i - 1).mod(data.size)]
			val next = data[(i + 1).mod(data.size)]

			p to when {
				prev.y == p.y && prev.x < p.x -> when {
					next.y < p.y -> Shape.BottomRightOutside
					else -> Shape.TopRightInside
				}

				prev.y == p.y && prev.x > p.x -> when {
					next.y < p.y -> Shape.BottomLeftInside
					else -> Shape.TopLeftOutside
				}

				prev.y < p.y -> when {
					next.x < p.x -> Shape.BottomRightInside
					else -> Shape.BottomLeftOutside
				}

				prev.y > p.y -> when {
					next.x < p.x -> Shape.TopRightOutside
					else -> Shape.TopLeftInside
				}

				else -> error("Should not happen")
			}
		}

		return data.pairs()
			.map {
				it to (abs(it.first.x - it.second.x) + 1).toLong() * (abs(it.first.y - it.second.y) + 1)
			}.sortedByDescending { it.second }
			.first { (points, _) ->
				val (a, b) = points
				val xRange = min(a.x, b.x)..max(a.x, b.x)
				val yRange = min(a.y, b.y)..max(a.y, b.y)

				// check the corners
				val topLeft = Point(xRange.first, yRange.first)
				val topRight = Point(xRange.last, yRange.first)
				val bottomRight = Point(xRange.last, yRange.last)
				val bottomLeft = Point(xRange.first, yRange.last)

				val corners = (pointShapes[topLeft]?.isInteriorRight ?: true)
					&& (pointShapes[topLeft]?.isInteriorBottom ?: true)
					&& (pointShapes[topRight]?.isInteriorLeft ?: true)
					&& (pointShapes[topRight]?.isInteriorBottom ?: true)
					&& (pointShapes[bottomRight]?.isInteriorTop ?: true)
					&& (pointShapes[bottomRight]?.isInteriorLeft ?: true)
					&& (pointShapes[bottomLeft]?.isInteriorRight ?: true)
					&& (pointShapes[bottomLeft]?.isInteriorTop ?: true)

				val horizontalEdge = data.filter { (it.y == yRange.first || it.y == yRange.last) && it.x > xRange.first && it.x < xRange.last }
					.all {
						pointShapes[it]!!.isInteriorLeft && pointShapes[it]!!.isInteriorRight
					}
				val verticalEdge = data.filter { (it.x == xRange.first || it.x == xRange.last) && it.y > yRange.first && it.y < yRange.last }
					.all {
						pointShapes[it]!!.isInteriorTop && pointShapes[it]!!.isInteriorBottom
					}

				corners && horizontalEdge && verticalEdge
			}.second
	}

}

// 4771532800 (part 1)
// 1559107992
//   72242816

private fun <T> List<T>.pairs(): List<Pair<T, T>> = indices.flatMap { i ->
	val a = this[i]
	(i + 1..lastIndex).map { a to this[it] }
}
