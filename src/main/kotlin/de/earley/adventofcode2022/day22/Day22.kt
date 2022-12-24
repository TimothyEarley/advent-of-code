package de.earley.adventofcode2022.day22

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode2021.split
import kotlin.math.sqrt

fun main() = Day22.start()

object Day22 : BaseSolution<Day22.Input, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Input = input.toList()
		.split { it.isBlank() }
		.let { (map, movement) ->

			val grid = grid(map.maxOf(String::length), map.size) { point ->
				val line = map[point.y]
				when (line.getOrNull(point.x)) {
					'#' -> MapItem.Wall
					'.' -> MapItem.Air
					' ', null -> MapItem.OffMap
					else -> error("invalid map $map at $point")
				}
			}

			val regex = "(?=[LR])|(?<=[LR])".toRegex()
			val parsedMovement = movement.single().split(regex).map {
				when (it) {
					"L" -> TurnLeft
					"R" -> TurnRight
					else -> Walk(it.toInt())
				}
			}

			Input(grid, parsedMovement)
		}

	override fun partOne(data: Input): Int = solve(data) { position, facing ->
		val newPosition = position + facing.point
		//TODO cleanup
		var newStart = Point(newPosition.x % data.grid.width, newPosition.y % data.grid.height)
		while (data.grid[newStart] == MapItem.OffMap || data.grid[newStart] == null) {
			newStart += facing.point
			newStart = Point(
				(newStart.x + data.grid.width) % data.grid.width,
				(newStart.y + data.grid.height) % data.grid.height
			)
		}
		newStart to facing
	}

	// use https://www.geogebra.org/m/pCv2EvwD
	override fun partTwo(data: Input): Int {

		val blockSizeSqr = data.grid.values().count {
			it == MapItem.Wall || it == MapItem.Air
		} / 6
		val blockSize = sqrt(blockSizeSqr.toFloat()).toInt()

		return solve(data) { position, facing ->
			val blockX = position.x / blockSize
			val blockY = position.y / blockSize

			// hardcoded
			if (blockSize == 4) testNet(blockX, blockY, facing, blockSize, position)
			else prodNet(blockX, blockY, facing, blockSize, position)
		}
	}

	private fun prodNet(
		blockX: Int,
		blockY: Int,
		facing: Direction,
		blockSize: Int,
		position: Point
	): Pair<Point, Direction> = when (Point(blockX, blockY)) {
		Point(1, 0) -> when (facing) {
			Direction.Left -> Point(
				x = 0,
				y = 3 * blockSize - 1 - (position.y % blockSize)
			) to Direction.Right
			Direction.Right -> TODO()
			Direction.Up -> Point(
				x = 0,
				y = 3 * blockSize + (position.x % blockSize)
			) to Direction.Right
			Direction.Down -> TODO()
		}
		Point(0, 2) -> when (facing) {
			Direction.Left -> Point(
				x = blockSize,
				y = blockSize - 1 - (position.y % blockSize),
			) to Direction.Right
			Direction.Right -> TODO()
			Direction.Up -> Point(
				x = blockSize,
				y = blockSize + (position.x % blockSize)
			) to Direction.Right
			Direction.Down -> TODO()
		}
		Point(0, 3) -> when (facing) {
			Direction.Left -> Point(
				x = blockSize + (position.y % blockSize),
				y = 0
			) to Direction.Down
			Direction.Right -> Point(
				x = blockSize + (position.y % blockSize),
				y = 3 * blockSize - 1
			) to Direction.Up
			Direction.Up -> TODO()
			Direction.Down -> Point(
				x = 2 * blockSize + (position.x % blockSize),
				y = 0
			) to Direction.Down
		}
		Point(1, 2) -> when (facing) {
			Direction.Left -> TODO()
			Direction.Right -> Point(
				x = 3 * blockSize - 1,
				y = blockSize - 1 - (position.y % blockSize)
			) to Direction.Left
			Direction.Up -> TODO()
			Direction.Down -> Point(
				x = blockSize - 1,
				y = 3 * blockSize + (position.x % blockSize)
			) to Direction.Left
		}
		Point(2, 0) -> when (facing) {
			Direction.Left -> TODO()
			Direction.Right -> Point(
				x = 2 * blockSize - 1,
				y = 3 * blockSize - 1 - (position.y % blockSize)
			) to Direction.Left
			Direction.Up -> Point(
				x = position.x % blockSize,
				y = 4 * blockSize - 1
			) to Direction.Up
			Direction.Down -> Point(
				x = 2 * blockSize - 1,
				y = blockSize + (position.x % blockSize)
			) to Direction.Left
		}
		Point(1, 1) -> when (facing) {
			Direction.Left -> Point(
				x = position.y % blockSize,
				y = 2 * blockSize
			) to Direction.Down
			Direction.Right -> Point(
				x = 2 * blockSize + (position.y % blockSize),
				y = blockSize - 1
			) to Direction.Up
			Direction.Up -> TODO()
			Direction.Down -> TODO()
		}
		else -> error("Unknown block $blockX, $blockY with $facing")
	}

	private fun testNet(
		blockX: Int,
		blockY: Int,
		facing: Direction,
		blockSize: Int,
		position: Point
	): Pair<Point, Direction> = when (Point(blockX, blockY)) {
		Point(2, 0) -> TODO()
		Point(0, 1) -> TODO()
		Point(1, 1) -> when (facing) {
			Direction.Left -> TODO()
			Direction.Right -> TODO()
			Direction.Up -> Point(
				x = 2 * blockSize,
				y = position.x % blockSize
			) to Direction.Right

			Direction.Down -> TODO()
		}

		Point(2, 1) -> when (facing) {
			Direction.Left -> TODO()
			Direction.Right -> Point(
				x = 4 * blockSize - 1 - (position.y % blockSize),
				y = 2 * blockSize
			) to Direction.Down

			Direction.Up -> TODO()
			Direction.Down -> TODO()
		}

		Point(2, 2) -> when (facing) {
			Direction.Left -> TODO()
			Direction.Right -> TODO()
			Direction.Up -> TODO()
			Direction.Down -> Point(
				x = blockSize - 1 - (position.x % blockSize),
				y = 2 * blockSize - 1
			) to Direction.Up
		}

		Point(3, 2) -> TODO()
		else -> error("Wrong block: $position - $blockX, $blockY with bs $blockSize")
	}

	private fun solve(data: Input, wrapAround: (Point, Direction) -> Pair<Point, Direction>): Int {
		val start = data.grid.indexOf(MapItem.Air)!!
		val points: MutableMap<Point, Direction> = mutableMapOf(start to Direction.Right)

		val result = data.movement.fold(State(start, Direction.Right)) { acc, movementCommand ->
			when (movementCommand) {
				TurnLeft -> acc.copy(
					facing = when (acc.facing) {
						Direction.Left -> Direction.Down
						Direction.Right -> Direction.Up
						Direction.Up -> Direction.Left
						Direction.Down -> Direction.Right
					}
				)

				TurnRight -> acc.copy(
					facing = when (acc.facing) {
						Direction.Left -> Direction.Up
						Direction.Right -> Direction.Down
						Direction.Up -> Direction.Right
						Direction.Down -> Direction.Left
					}
				)

				is Walk -> doWalk(acc, movementCommand, points, data, wrapAround)
			}.also { s ->
				points[s.position] = s.facing
			}
		}

		return result.decode()
	}

	private fun State.decode(): Int = 1000 * (position.y + 1) + 4 * (position.x + 1) + when (facing) {
		Direction.Left -> 2
		Direction.Right -> 0
		Direction.Up -> 3
		Direction.Down -> 1
	}.also {
		println("Decoded: $position with $facing")
	}


	private fun doWalk(
		state: State,
		movementCommand: Walk,
		points: MutableMap<Point, Direction>,
		data: Input,
		wrapAround: (Point, Direction) -> Pair<Point, Direction>
	): State = (1..movementCommand.steps).fold(state) { acc, _ ->
		val newPosition = acc.position + acc.facing.point
		when (data.grid[newPosition]) {
			MapItem.Air -> acc.copy(position = newPosition)
			MapItem.Wall -> {
				// early return
				return acc
			}

			MapItem.OffMap, null -> {
				// wrap around
				val (newStart, newFacing) = wrapAround(acc.position, acc.facing)

				when (data.grid[newStart]) {
					MapItem.Air -> acc.copy(position = newStart, facing = newFacing)
					MapItem.Wall -> acc
					MapItem.OffMap, null -> error("Should be on map")
				}
			}
		}.also { s ->
			points[s.position] = s.facing

		}
	}

	data class State(
		val position: Point,
		val facing: Direction
	)
	
	enum class MapItem {
		Air, Wall, OffMap
	}

	data class Input(
		val grid: Grid<MapItem>,
		val movement: List<MovementCommand>
	)

	sealed interface MovementCommand
	object TurnLeft : MovementCommand
	object TurnRight : MovementCommand
	data class Walk(val steps: Int) : MovementCommand

}
