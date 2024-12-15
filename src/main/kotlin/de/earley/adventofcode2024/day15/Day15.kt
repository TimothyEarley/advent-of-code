package de.earley.adventofcode2024.day15

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode.split
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day15.start()

object Day15 : BaseSolution<Day15.Input, Long, Long>() {

	data class Input(
		val warehouse: Grid<Type>,
		val instructions: List<Direction>,
		val robotPos: Point,
	)

	enum class Type {
		SPACE, BOX, WALL,
		LEFT_BOX, RIGHT_BOX
	}

	override fun parseInput(input: Sequence<String>): Input =
		input.toList().split { it.isBlank() }.let { (grid, instructions) ->
			Input(grid.toGrid {
				when (it) {
					'#' -> Type.WALL
					'.', '@' -> Type.SPACE
					'O' -> Type.BOX
					else -> error(it)
				}
			},
				instructions.joinToString("").map { Direction.parseArrow(it) },
				grid.withIndex()
					.mapNotNull {
						it.value.indexOf('@').takeIf { i -> i >= 0 }
							?.let { x -> Point(x, it.index) }
					}
					.single()
			)
		}

	override fun partOne(data: Input): Long = gpsOfBoxes(runInstructions(data))
	override fun partTwo(data: Input): Long = gpsOfBoxes(runInstructions(expand(data)))

	private fun expand(data: Input): Input {
		val expanded = grid(width = data.warehouse.width * 2, height = data.warehouse.height) { p ->
			val smallX = p.x / 2
			when (data.warehouse[smallX, p.y]!!) {
				Type.SPACE -> Type.SPACE
				Type.BOX -> {
					val offset = p.x % 2
					if (offset == 0) Type.LEFT_BOX
					else Type.RIGHT_BOX
				}

				Type.WALL -> Type.WALL
				Type.LEFT_BOX, Type.RIGHT_BOX -> error("Not present in input")
			}
		}
		val expandedData = data.copy(
			warehouse = expanded,
			robotPos = data.robotPos.copy(x = data.robotPos.x * 2)
		)
		return expandedData
	}

	private fun gpsOfBoxes(end: Input) = end.warehouse.pointValues()
		.filter { it.second == Type.BOX || it.second == Type.LEFT_BOX }
		.sumOf { it.first.x.toLong() + it.first.y * 100 }

	private fun runInstructions(data: Input) =
		data.instructions.fold(data) { current, instruction ->
			val next = current.robotPos + instruction.point
			when (current.warehouse[next]) {
				null -> error("fell of map")
				Type.SPACE -> current.copy(robotPos = next)
				Type.WALL -> current
				Type.BOX, Type.LEFT_BOX, Type.RIGHT_BOX -> {
					val canBePushed = canBePushed(current.warehouse, next, instruction)
					if (canBePushed == null) {
						current
					} else {
						val nextWarehouse = current.warehouse.toMutableGrid()
						canBePushed.forEach { p ->
							// check if the prev is also being pushed, otherwise this is empty
							val prev = p - instruction.point
							nextWarehouse[p] = if (prev in canBePushed) {
								current.warehouse[prev]!!
							} else {
								Type.SPACE
							}
						}
						current.copy(
							warehouse = nextWarehouse,
							robotPos = next
						)
					}
				}
			}
		}

	private fun canBePushed(grid: Grid<Type>, box: Point, dir: Direction): Set<Point>? {
		val open = mutableSetOf(box)
		val done = mutableSetOf<Point>()

		fun addIfNotClosed(p: Point) {
			if (!done.contains(p)) {
				open.add(p)
			}
		}

		while (open.isNotEmpty()) {
			val next = open.first()
			open.remove(next)
			done.add(next)
			when (grid[next]) {
				Type.SPACE -> {}
				Type.WALL, null -> return null
				Type.BOX -> addIfNotClosed(next + dir.point)
				Type.LEFT_BOX -> {
					addIfNotClosed(next + dir.point)
					addIfNotClosed(next + Direction.Right.point)
				}

				Type.RIGHT_BOX -> {
					addIfNotClosed(next + dir.point)
					addIfNotClosed(next + Direction.Left.point)
				}
			}
		}
		return done
	}
}
