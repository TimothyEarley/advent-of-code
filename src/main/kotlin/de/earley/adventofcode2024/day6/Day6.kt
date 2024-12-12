package de.earley.adventofcode2024.day6

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.runUntil
import de.earley.adventofcode.toMutableGrid

fun main() = Day6.start()

object Day6 : BaseSolution<Day6.Input, Long, Long>() {

	data class Input(val start: State, val grid: Grid<Space>)

	enum class Space {
		Empty, Wall
	}

	data class State(val pos: Point, val dir: Direction)

	override fun parseInput(input: Sequence<String>): Input = input.toList().let { lines ->
		var start: State? = null
		val grid = Grid(lines.first().length, lines.size, lines.flatMapIndexed { y, line ->
			line.mapIndexed { x, char ->
				when (char) {
					'.' -> Space.Empty
					'#' -> Space.Wall
					else -> {
						start = State(Point(x, y), Direction.parseArrow(char))
						Space.Empty
					}
				}
			}
		})
		Input(start ?: error("No start found"), grid)
	}

	override fun partOne(data: Input): Long {
		val (_, visited) = simulateGuard(data.start, data.grid)
		return visited.size.toLong()
	}


	override fun partTwo(data: Input): Long {
		val (_, originalPath) = simulateGuard(data.start, data.grid)
		return data.grid.indices
			.filter { it in originalPath }
			.count { newObstacle ->
				val newGrid = data.grid.toMutableGrid().apply {
					set(newObstacle, Space.Wall)
				}
				val (final, _) = simulateGuard(data.start, newGrid)
				newGrid.contains(final)
			}
			.toLong()
	}

	private fun simulateGuard(start: State, grid: Grid<Space>): Pair<Point, Set<Point>> {
		val visited = mutableSetOf<State>()
		val final = runUntil(
			start,
			{ !grid.contains(it.pos) || it in visited }
		) { state ->
			visited += state
			when (grid[state.pos + state.dir.point]) {
				Space.Wall -> State(
					state.pos, when (state.dir) {
						Direction.Left -> Direction.Up
						Direction.Right -> Direction.Down
						Direction.Up -> Direction.Right
						Direction.Down -> Direction.Left
					}
				)

				Space.Empty, null -> State(
					state.pos + state.dir.point,
					state.dir
				)
			}
		}
		return final.pos to visited.map { it.pos }.toSet()
	}
}
