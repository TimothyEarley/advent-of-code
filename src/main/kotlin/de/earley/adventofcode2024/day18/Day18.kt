package de.earley.adventofcode2024.day18

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.grid
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.neighbours

fun main() = Day18(71,71, 1023).start()

class Day18(
	private val width: Int,
	private val height: Int,
	private val simulateFor: Int
) : BaseSolution<List<Point>, Long, String>() {

	override fun parseInput(input: Sequence<String>): List<Point> = input.mapToList {
		Point.parse(it)
	}

	override fun partOne(data: List<Point>): Long {
		val grid = createGrid(data)
		return pathLength(grid, simulateFor)!!.toLong()
	}

	override fun partTwo(data: List<Point>): String {
		val grid = createGrid(data)

		var max = data.size
		var min = 0
		while (min < max) {
			val half = min + (max - min) / 2
			val solution = pathLength(grid, half)
			if (solution != null) {
				min = half + 1
			} else {
				max = half
			}
		}
		return data[max].let {
			"${it.x},${it.y}"
		}
	}

	private fun createGrid(data: List<Point>): Grid<Int> = grid(width, height) { p ->
		require(data.count { it == p } <= 1) { p }
		data.indexOf(p).takeIf { it >= 0 } ?: Int.MAX_VALUE
	}

	private fun pathLength(grid: Grid<Int>, time: Int): Int? {
		val start = Point(0, 0)
		val goal = Point(width - 1, height - 1)

		return generalAStar(
			from = start,
			goal = { it == goal },
			heuristic = { it.manhattanDistanceTo(goal) },
			neighbours = {
				neighbours(false)
					.filter { it in grid }
					.filter { grid[it]!! > time }
					.map { it to 1 }
			}
		)
	}

}
