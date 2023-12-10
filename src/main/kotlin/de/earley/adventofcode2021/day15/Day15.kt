package de.earley.adventofcode2021.day15

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Node
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.neighbours
import de.earley.adventofcode2021.modStart1

fun main() = Day15.start()

object Day15 : BaseSolution<Grid<Int>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Int> {
		val l = input.toList()
		val width = l.first().length
		val height = l.size
		return Grid(width, height, l.flatMap { it.toList().map(Char::digitToInt) })
	}

	override fun partOne(data: Grid<Int>): Int =
		aStar(data::get, Point(0, 0), Point(data.width - 1, data.height - 1))

	override fun partTwo(data: Grid<Int>): Int =
		aStar(tiledGet(data, 5), Point(0, 0), Point(data.width * 5 - 1, data.height * 5 - 1))

	fun tiledGet(grid: Grid<Int>, expandBy: Int): (Point) -> Int? = {
		if (it.x < 0 || it.y < 0 || it.x >= grid.width * expandBy || it.y >= grid.height * expandBy) {
			null
		} else {
			val xInGrid = it.x.rem(grid.width)
			val yInGrid = it.y.rem(grid.height)
			grid[xInGrid, yInGrid]?.let { originalValue ->
				val tileX = it.x.floorDiv(grid.width)
				val tileY = it.y.floorDiv(grid.height)
				val newValue = originalValue + tileX + tileY
				// wrap around to be in 1..9
				newValue modStart1 9
			}
		}
	}
}

fun aStar(grid: (Point) -> Int?, from: Point, to: Point, newNodeCallback: ((Node<Point>) -> Unit)? = null): Int =
	generalAStar(
		from,
		{ it == to },
		{ it.manhattanDistanceTo(to) },
		{ neighbours().mapNotNull { n -> grid(n)?.let { n to it } } },
		true,
		newNodeCallback = newNodeCallback
	)!!
