package de.earley.adventofcode2022.day24

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.grid
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.neighbours

fun main() = Day24.start()

object Day24 : BaseSolution<Day24.Input, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Input = input.toList().let { lines ->
		val first = lines.first()
		val width = first.length - 2
		require(first == "#." + "#".repeat(width))

		val last = lines.last()
		require(last == "#".repeat(width) + ".#")

		val height = lines.size - 2

		val content = lines.drop(1).dropLast(1)
			.map { it.drop(1).dropLast(1) }

		val grid = grid(width, height) { p ->
			when (val c = content[p.y][p.x]) {
				'.' -> null
				else -> Blizzard(p, Direction.parseArrow(c))
			}
		}

		Input(
			grid.pointValues().filter { it.second != null }.map { it.second!! }.toList(),
			width,
			height
		)
	}

	override fun partOne(data: Input): Int {
		val to = Point(data.width - 1, data.height)
		val start = Point(0, -1)

		return pathCost(data, listOf(start, to))
	}

	override fun partTwo(data: Input): Int {
		val to = Point(data.width - 1, data.height)
		val start = Point(0, -1)

		return pathCost(data, listOf(start, to, start, to))
	}

	private fun pathCost(
		data: Input,
		waypoints: List<Point>,
	): Int {
		val blizzardsStorage = BlizzardsStorage(data)

		val `in` = Point(0, -1)
		val out = Point(data.width - 1, data.height)

		return waypoints.zipWithNext().fold(0) { currentTime, (from, to) ->
			currentTime + generalAStar(
				from = State(from, currentTime),
				goal = { it.position == to },
				heuristic = { it.position.manhattanDistanceTo(to) },
				neighbours = {
					(position.neighbours() + position)
						.filter {
							(it == `in` || it == out || (it.x in 0 until data.width && it.y in 0 until data.height))
						}
						.filter { p ->
							blizzardsStorage.getBlizzard(time + 1).none { it.position == p }
						}.map {
							State(it, time + 1) to 1
						}
				}
			)!!
		}
	}

	class BlizzardsStorage(val input: Input) {
		private val cache = mutableMapOf(0 to input.blizzards)

		fun getBlizzard(time: Int): List<Blizzard> =
			when (val cached = cache[time]) {
				null -> {
					val previous = getBlizzard(time - 1)
					previous.map {
						val newPosition = it.position + it.direction.point

						val fixedPosition = Point(
							(newPosition.x + input.width) % input.width,
							(newPosition.y + input.height) % input.height
						)

						it.copy(position = fixedPosition)
					}.also {
						cache[time] = it
					}
				}

				else -> cached
			}
	}

	data class State(
		val position: Point,
		val time: Int,
	)

	data class Input(
		val blizzards: List<Blizzard>,
		val width: Int,
		val height: Int,
	)

	data class Blizzard(
		val position: Point,
		val direction: Direction,
	)
}
