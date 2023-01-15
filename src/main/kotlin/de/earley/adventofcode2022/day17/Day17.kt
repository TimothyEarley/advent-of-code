package de.earley.adventofcode2022.day17

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point

fun main() = Day17.start()

object Day17 : BaseSolution<List<Day17.Direction>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Direction> = input.single().map {
		when (it) {
			'<' -> Direction.Left
			'>' -> Direction.Right
			else -> error("Unknown direction $it")
		}
	}

	// flipped from task since our coordinate system has y going up
	@Suppress("ktlint")
	private val pieces = listOf(
		Grid(
			4, 1, listOf(
				true, true, true, true,
			)
		),
		Grid(
			3, 3, listOf(
				false, true, false,
				true, true, true,
				false, true, false,
			)
		),
		Grid(
			3, 3, listOf(
				true, true, true,
				false, false, true,
				false, false, true,
			)
		),
		Grid(
			1, 4, listOf(
				true,
				true,
				true,
				true,
			)
		),
		Grid(
			2, 2, listOf(
				true, true,
				true, true,
			)
		),
	)

	override fun partOne(data: List<Direction>): Long = simulateTetris(2022L, data)
	override fun partTwo(data: List<Direction>): Long = simulateTetris(1000000000000L, data)

	private fun simulateTetris(
		iterations: Long,
		data: List<Direction>,
	): Long {
		val grid = TetrisGrid(mutableListOf(), 7)

		var shifted = 0L
		var jetIndex = 0

		/*
		 * We keep a record of previously seen states. Once we find an already seen state
		 * we can quickly add the heights of this state and fats forward the iterations.
		 * Then play out the rest as normal (by  setting useSeen to false)
		 */
		data class SeenKey(val cave: String, val pieceIndex: Int, val jetIndex: Int)
		data class SeenState(val shifted: Long, val atIteration: Long)

		val seen = mutableMapOf<SeenKey, SeenState>()
		var useSeen = true

		var iteration = -1L
		while (++iteration < iterations) {
			if (useSeen) {
				val key = SeenKey(grid.key(), (iteration % pieces.size).toInt(), (jetIndex % data.size).toInt())
				when (val prev = seen[key]) {
					null -> seen[key] = SeenState(shifted, iteration)
					else -> {
						val cycleTime = iteration - prev.atIteration
						val cyclesWeCanAdd = (iterations - iteration) / cycleTime

						iteration += cyclesWeCanAdd * cycleTime
						shifted += (shifted - prev.shifted) * cyclesWeCanAdd
						useSeen = false
					}
				}
			}

			val piece = pieces[(iteration % pieces.size).toInt()]

			var position = Point(2, grid.height + 3)

			/**
			 * Main simulation loop
			 */
			while (true) {
				// jet
				val newPosition = position + data[jetIndex++ % data.size].point
				if (!intersects(piece, grid, newPosition)) position = newPosition

				// drop
				val droppedPosition = position + Point(0, -1)
				if (!intersects(piece, grid, droppedPosition)) {
					position = droppedPosition
				} else {
					// land
					piece.pointValues().filter { it.second }.forEach { (p, _) ->
						grid[p + position] = true
					}

					// see if we can squash down the tower
					val canReach =
						floodFill(grid, emptySet(), setOf(Point(0, grid.height)))
					val toRemove = canReach.minOf { it.y }
					if (toRemove > 0) {
						grid.shiftDown(toRemove)
						shifted += toRemove
					}
					break
				}
			}
		}

		return grid.height + shifted
	}

	// TODO extract common util function
	private tailrec fun floodFill(grid: TetrisGrid, visited: Set<Point>, open: Set<Point>): Set<Point> {
		if (open.isEmpty()) return visited

		val next = open.first()
		val newOpens: List<Point> = Point.cardinals().map { next + it }.filter {
			// only go down, allow one row above grid as well
			it.y <= next.y && (it.y >= 0 && it.y <= grid.height && it.x in 0 until grid.width) && !grid[it] && it !in visited
		}.toList()

		return floodFill(grid, visited + next, open - next + newOpens)
	}

	private fun intersects(piece: Grid<Boolean>, grid: TetrisGrid, offset: Point): Boolean = piece
		.pointValues()
		.filter { it.second }
		.map { it.first + offset }
		.any {
			it.x < 0 || it.x >= grid.width || it.y < 0 || grid[it]
		}

	enum class Direction(val point: Point) {
		Left(Point(-1, 0)), Right(Point(1, 0))
	}

	class TetrisGrid(
		private val data: MutableList<MutableList<Boolean>>,
		val width: Int,
	) {
		val height: Int
			get() = data.size

		fun key(): String = data.joinToString("") { row -> row.joinToString("") { if (it) "#" else "." } }

		operator fun get(point: Point): Boolean =
			if (point !in this) {
				false
			} else {
				data[point.y][point.x]
			}

		operator fun set(point: Point, value: Boolean) {
			while (point.y >= data.size) data.add(MutableList(width) { false })
			data[point.y][point.x] = value
		}

		operator fun contains(point: Point): Boolean =
			point.y >= 0 && point.y < data.size && point.x >= 0 && point.x < width

		fun shiftDown(toRemove: Int) {
			repeat(toRemove) {
				data.removeFirst()
			}
		}
	}
}
