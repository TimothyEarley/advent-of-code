package de.earley.adventofcode2022.day18

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point3

fun main() = Day18.start()

object Day18 : BaseSolution<Set<Point3>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Set<Point3> = input.map {
		it.split(",", limit = 3).map(String::toInt).let { (x, y, z) -> Point3(x, y, z) }
	}.toSet()

	override fun partOne(data: Set<Point3>): Int = countExposedSurfaces(data)

	override fun partTwo(data: Set<Point3>): Int {
		// create a bounding cube
		val min = Point3(
			data.minOf { it.x },
			data.minOf { it.y },
			data.minOf { it.z }
		)
		val max = Point3(
			data.maxOf { it.x },
			data.maxOf { it.y },
			data.maxOf { it.z }
		)

		// flood fill from the outside
		val exposed = floodFill(data, (min + Point3(-1, -1, -1)) to (max + Point3(1, 1, 1)), emptySet(), setOf(max))

		val airPockets = (min..max).filter {
			it !in data && it !in exposed
		}

		return countExposedSurfaces(data + airPockets)
	}

	private fun countExposedSurfaces(data: Set<Point3>) = data.sumOf { p ->
		Point3.units().count {
			val adjacent = p + it
			adjacent !in data
		}
	}

	private tailrec fun floodFill(solid: Set<Point3>, bound: Pair<Point3, Point3>, visited: Set<Point3>, open: Set<Point3>): Set<Point3> {
		if (open.isEmpty()) return visited

		val next = open.first()
		val newOpens: List<Point3> = Point3.units().map { next + it }.filter {
			it in bound && it !in solid && it !in visited
		}.toList()

		return floodFill(solid, bound, visited + next, open - next + newOpens)
	}

	private operator fun Pair<Point3, Point3>.contains(p: Point3): Boolean =
		p.x in first.x..second.x && p.y in first.y..second.y && p.z in first.z..second.z
}
