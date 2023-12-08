package de.earley.adventofcode2022.day14

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.toMutableGrid
import kotlin.math.max
import kotlin.math.min

fun main() = Day14.start()

object Day14 : BaseSolution<List<Day14.RockFormation>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<RockFormation> = input.mapToList {
		RockFormation(
			it.split(" -> ").map(Point.Companion::parse)
		)
	}

	override fun partOne(data: List<RockFormation>): Int {
		val (adjust, grid) = createGrid(data, Point(1, 1))
		return runSimulation(grid, Point(500, 0) - adjust)
	}

	override fun partTwo(data: List<RockFormation>): Int {
		val (adjust, grid) = createGrid(data, Point(data.maxOf { it.points.maxOf(Point::y) } + 2, 3))
		val floorY = data.maxOf { it.points.maxOf(Point::y) } + 2

		val floorAdded = grid.toMutableGrid().apply {
			for (x in 0 until grid.width) {
				this[x, floorY - adjust.y] = CaveContent.Rock
			}
		}

		return runSimulation(floorAdded, Point(500, 0) - adjust)
	}

	private fun createGrid(data: List<RockFormation>, padding: Point): Pair<Point, Grid<CaveContent>> {
		val minX = min(data.minOf { it.points.minOf(Point::x) }, 500)
		val maxX = max(data.maxOf { it.points.maxOf(Point::x) }, 500)
		val minY = min(data.minOf { it.points.minOf(Point::y) }, 0)
		val maxY = max(data.maxOf { it.points.maxOf(Point::y) }, 0)

		val width = maxX - minX + 2 * padding.x
		val height = maxY - minY + 2 * padding.y
		val adjust = Point(minX - padding.x, minY - padding.y)

		val grid = grid(width, height) { p ->
			when {
				data.any { it.isRock(p + adjust) } -> CaveContent.Rock
				else -> CaveContent.Air
			}
		}
		return adjust to grid
	}

	private fun runSimulation(data: Grid<CaveContent>, sandStart: Point): Int {
		val state = data.toMutableGrid()
		var landedSand = 0

		while (true) {
			// spawn a piece of sand
			var sand = sandStart
			if (state[sand] != CaveContent.Air) {
				// cannot spawn
				return landedSand
			}
			var landed = false
			while (!landed) {
				// check if can go down
				when (CaveContent.Air) {
					state[sand + Point(0, 1)] -> sand += Point(0, 1)
					state[sand + Point(-1, 1)] -> sand += Point(-1, 1)
					state[sand + Point(1, 1)] -> sand += Point(1, 1)
					else -> {
						// check void
						if (sand + Point(0, 1) !in state) {
							// fall into void
							return landedSand
						}

						// land sand
						state[sand] = CaveContent.Sand
						landedSand++
						landed = true
					}
				}
			}
		}
	}

	data class RockFormation(val points: List<Point>)

	private fun RockFormation.isRock(p: Point): Boolean = points.zipWithNext().any { p.isBetween(it.first, it.second) }

	private fun Point.isBetween(from: Point, to: Point) =
		(x == from.x && x == to.x && from.y <= y && y <= to.y) ||
			(x == from.x && x == to.x && from.y >= y && y >= to.y) ||
			(y == from.y && y == to.y && from.x <= x && x <= to.x) ||
			(y == from.y && y == to.y && from.x >= x && x >= to.x)

	enum class CaveContent {
		Rock, Air, Sand
	}
}
