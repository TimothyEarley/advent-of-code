package de.earley.adventofcode2024.day20

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.MutableGrid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day20(100).start()

class Day20(private val saving: Int) : BaseSolution<Day20.Input, Int, Int>() {

	data class Input(
		val start: Point,
		val end: Point,
		val grid: Grid<Type>
	)

	enum class Type {
		Track, Wall
	}

	override fun parseInput(input: Sequence<String>): Input = input.toList().let { lines ->
		val s = lines.withIndex().mapNotNull {
			it.value.indexOf('S').takeIf { i -> i >= 0 }
				?.let { x -> Point(x, it.index) }
		}
			.single()
		val e = lines.withIndex().mapNotNull {
			it.value.indexOf('E').takeIf { i -> i >= 0 }
				?.let { x -> Point(x, it.index) }
		}
			.single()
		Input(
			grid = lines.toGrid {
				when (it) {
					'#' -> Type.Wall
					'.', 'S', 'E' -> Type.Track
					else -> error(it)
				}
			},
			start = s,
			end = e
		)
	}

	override fun partOne(data: Input): Int = cheats(data, 2)

	override fun partTwo(data: Input): Int = cheats(data, 20)

	private fun cheats(data: Input, maxDistance: Int): Int {
		val toEnd = calculateDistanceGrid(data)
		return data.grid.pointValues()
			.flatMap { p ->
				data.grid.pointValues().map { p.point to it.point }
			}.filter { (p1, p2) ->
				data.grid[p1] == Type.Track && data.grid[p2] == Type.Track
			}.filter { (p1, p2) ->
				p1.manhattanDistanceTo(p2) <= maxDistance
			}
			.count { (p1, p2) ->
				val fromP1 = toEnd[p1]!!
				val fromP2 = toEnd[p2]!!
				val distance = p1.manhattanDistanceTo(p2)
				// the amount of time saved is how much quicker we get
				// to the end from p2 rather than p1 minus the time it takes to
				// get to p2 from p1
				val timeSaved = fromP1 - (fromP2 + distance)
				timeSaved >= saving
			}
	}

	private fun calculateDistanceGrid(data: Input): Grid<Int?> {
		val distanceGrid: MutableGrid<Int?> = grid(data.grid.width, data.grid.height) { null }
			.toMutableGrid()
		val open = mutableSetOf(data.end)
		distanceGrid[data.end] = 0
		while (open.isNotEmpty()) {
			val next = open.first()
			open.remove(next)
			val currentDistance = distanceGrid[next]!!
			open += next.neighbours(false)
				.filter {
					data.grid[it] == Type.Track && distanceGrid[it] == null
				}
				.onEach {
					distanceGrid[it] = currentDistance + 1
				}
				.toList()
		}

		return distanceGrid
	}

}
