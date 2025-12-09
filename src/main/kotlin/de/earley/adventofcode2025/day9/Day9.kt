package de.earley.adventofcode2025.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.neighbours
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() = Day9.start()

object Day9 : BaseSolution<List<Point>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Point> = input.mapToList { Point.parse(it) }

	override fun partOne(data: List<Point>): Long = data.pairs().maxOf {
		(abs(it.first.x - it.second.x) + 1).toLong() * (abs(it.first.y - it.second.y) + 1)
	}

	override fun partTwo(data: List<Point>): Long {

		val xStops = data.mapTo(mutableSetOf(), Point::x)
		val yStops = data.mapTo(mutableSetOf(), Point::y)

		// the vertical edges are needed for detecting a point in a shape using the basic point in polygon algorithm
		// since points on the edge count as inside the edge, we expand the shape by one and then do an interior
		// check on that

		@Suppress("ReplaceRangeToWithRangeUntil") // makes it more consistent here
		val verticalEdges = (data.zipWithNext() + (data.last() to data.first()))
			.filter { it.first.x == it.second.x }
			// create the edge
			.map { it.first.x to (min(it.first.y, it.second.y) ..  max(it.first.y, it.second.y)) }
			// move the edge
			.map { (x, yRange) ->
				val top = Point(x, yRange.first)
				val bottom = Point(x, yRange.last)
				// if there is space above, move up by one
				val prevTop = data[(data.indexOf(top) - 1).mod(data.size)]
				val nextTop = data[(data.indexOf(top) + 1).mod(data.size)]
				val prevBottom = data[(data.indexOf(bottom) - 1).mod(data.size)]
				val nextBottom = data[(data.indexOf(bottom) + 1).mod(data.size)]

				/*
				 Where did we come from? Also remember we are winding around the shape clockwise (manually verified)

				      A---F---B
				          |
				          |
        			  C---L---D
				 */

				// TODO could be nested to reduce redundant checks or split the calulations for new x, top y, and bot y
				when {
					// A F L C
					prevTop.x < top.x && prevBottom == top && nextBottom.x < bottom.x -> (x + 1) to (top.y - 1 .. bottom.y + 1)
					// A F L D
					prevTop.x < top.x && prevBottom == top && nextBottom.x > bottom.x -> (x + 1) to (top.y - 1 .. bottom.y - 1)
					// B F L C
					prevTop.x > top.x && prevBottom == top && nextBottom.x < bottom.x -> (x + 1) to (top.y + 1 .. bottom.y + 1)
					// B F L D
					prevTop.x > top.x && prevBottom == top && nextBottom.x > bottom.x -> (x + 1) to (top.y + 1 .. bottom.y - 1)

					// C L F A
					prevBottom.x < bottom.x && prevTop == bottom && nextTop.x < top.x -> (x - 1) to (top.y + 1 .. bottom.y - 1)
					// C L F B
					prevBottom.x < bottom.x && prevTop == bottom && nextTop.x > top.x -> (x - 1) to (top.y - 1 .. bottom.y - 1)
					// D L F A
					prevBottom.x > bottom.x && prevTop == bottom && nextTop.x < top.x -> (x - 1) to (top.y + 1 .. bottom.y + 1)
					// D L F B
					prevBottom.x > bottom.x && prevTop == bottom && nextTop.x > top.x -> (x - 1) to (top.y - 1 .. bottom.y + 1)

					else -> error("Should not happen")
				}
			}

		return data.pairs()
			.map {
				it to (abs(it.first.x - it.second.x) + 1).toLong() * (abs(it.first.y - it.second.y) + 1)
			}.sortedByDescending { it.second }
			.first { (points, _) ->
				val (a, b) = points
				val xRange = min(a.x, b.x) .. max(a.x, b.x)
				val yRange = min(a.y, b.y) .. max(a.y, b.y)

				val xToCheck = xStops.filter { it in xRange }
				val yToCheck = yStops.filter { it in yRange }

//				val pointsToCheck = ( xToCheck.map { Point(it, a.y) } +
//					xToCheck.map { Point(it, b.y) } +
//					yToCheck.map { Point(a.y, it) } +
//					yToCheck.map { Point(b.y, it) } )
//					// TODO: why do I need these?
//					.flatMap { p ->
//						// add some extra point
//						p.neighbours(diagonal = true).filter { it.x in xRange && it.y in yRange }.plus(p)
//					}

				val pointsToCheck = data.flatMap {
					it.neighbours(diagonal = true).plus(it)
				}.filter { it.x in xRange && it.y in yRange }

				pointsToCheck.all { p ->
					verticalEdges.count { (x, yRange) ->
						// does the edge lie one the path from screen left to the point p?
						p.x > x && p.y in yRange
					} % 2 == 1
				}

			}.second
	}

}

// 1559107992
//   72242816

private fun <T> List<T>.pairs(): List<Pair<T, T>> = indices.flatMap { i ->
	val a = this[i]
	(i + 1..lastIndex).map { a to this[it] }
}
