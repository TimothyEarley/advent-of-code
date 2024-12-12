package de.earley.adventofcode2023.day17

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid

fun main() = Day17.start()

object Day17 : BaseSolution<Grid<Int>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Int> = input.toGrid(Char::digitToInt)

	data class RecentPath(
		val current: Point,
		val dir: Direction?,
		val timesMoved: Int,
	)

	override fun partOne(data: Grid<Int>): Long = heatLoss(data, 0, 3)

	override fun partTwo(data: Grid<Int>): Long = heatLoss(data, 4, 10)

	private fun heatLoss(data: Grid<Int>, minStraight: Int, maxStraight: Int): Long {
		val end = Point(data.width - 1, data.height - 1)
		return generalAStar(
			RecentPath(Point(0, 0), null, 0),
			{ it.current == end },
			{ it.current.manhattanDistanceTo(end) },
			{
				if (dir == null) {
					// at start
					current.neighbours().filter { it in data }
						.map { RecentPath(it, Direction.fromPoint(it - current), 1) to data[it]!! }
				} else {
					Direction.entries.asSequence()
						.filterNot { it == dir && timesMoved >= maxStraight }
						.filterNot { it != dir && timesMoved < minStraight }
						.filterNot { it == dir.reverse() }
						.filter { current + it.point in data }
						.map {
							RecentPath(current + it.point, it, if (it == dir) timesMoved + 1 else 1) to data[current + it.point]!!
						}
				}
			},
			true
		)!!.toLong()
	}

	private fun Direction.reverse(): Direction = when (this) {
		Direction.Left -> Direction.Right
		Direction.Right -> Direction.Left
		Direction.Up -> Direction.Down
		Direction.Down -> Direction.Up
	}
}
