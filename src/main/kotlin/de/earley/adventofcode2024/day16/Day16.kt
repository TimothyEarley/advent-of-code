package de.earley.adventofcode2024.day16

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStarNode
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.toGrid

fun main() = Day16.start()

object Day16 : BaseSolution<Day16.Input, Long, Long>() {

	data class Input(
		val grid: Grid<Type>,
		val start: Point,
		val end: Point
	)

	enum class Type {
		WALL, AIR
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
					'#' -> Type.WALL
					'.', 'S', 'E' -> Type.AIR
					else -> error(it)
				}
			},
			start = s,
			end = e
		)
	}

	data class State(
		val pos: Point,
		val dir: Direction
	)

	override fun partOne(data: Input): Long = getPaths(data).first().cost.toLong()
	override fun partTwo(data: Input): Long {
		val paths = getPaths(data).flatMap { it.toPath() }.map { it.pos }.toSet()
		return data.grid.pointValues()
			.count { it.first in paths }
			.toLong()
	}

	private fun getPaths(data: Input) = generalAStarNode(
		from = State(data.start, Direction.Right),
		goal = { it.pos == data.end },
		heuristic = { it.pos.manhattanDistanceTo(data.end) },
		neighbours = {
			sequence {
				when (dir) {
					Direction.Left, Direction.Right -> {
						yield(copy(dir = Direction.Up) to 1000)
						yield(copy(dir = Direction.Down) to 1000)
					}

					Direction.Up, Direction.Down -> {
						yield(copy(dir = Direction.Left) to 1000)
						yield(copy(dir = Direction.Right) to 1000)
					}
				}
				val next = pos + dir.point
				if (data.grid[next] == Type.AIR) yield(copy(pos = next) to 1)
			}
		},
		strict = false
	)
}
