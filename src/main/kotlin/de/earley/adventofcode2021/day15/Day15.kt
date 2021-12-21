package de.earley.adventofcode2021.day15

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Grid
import de.earley.adventofcode2021.Point
import de.earley.adventofcode2021.manhattanDistanceTo
import de.earley.adventofcode2021.modStart1
import de.earley.adventofcode2021.neighbours
import java.util.PriorityQueue

fun main() = Day15.start()

object Day15 : BaseSolution<Grid<Int>, Int>() {

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

data class Node(
	val point: Point,
	val cost: Int,
	val heuristic: Int
)

fun aStar(grid: (Point) -> Int?, from: Point, to: Point, newNodeCallback: ((Node) -> Unit)? = null): Int {

	val closed = mutableSetOf<Point>()
	val open = PriorityQueue(compareBy<Node> { it.cost + it.heuristic }).apply {
		add(Node(from, 0, from.manhattanDistanceTo(to)))
	}

	while (open.isNotEmpty()) {
		val current = open.remove()
		open.remove(current)
		closed.add(current.point)

		if (current.point == to)
			return current.cost

		// expand neighbours
		for (next in current.point.neighbours()) {
			if (next in closed) continue // don't revisit closed nodes (the heuristic is admissible and consistent)

			val costToEnter = grid(next) ?: continue
			val thisCost = current.cost + costToEnter

			val previousOpened = open.find { it.point == next }
			// if new or better, add it to queue
			if (previousOpened == null || previousOpened.cost >= thisCost) {
				val newNode = Node(next, thisCost, next.manhattanDistanceTo(to))
				open.add(newNode)
				if (newNodeCallback != null) newNodeCallback(newNode)
			}
		}
	}

	error("No path found!")
}
