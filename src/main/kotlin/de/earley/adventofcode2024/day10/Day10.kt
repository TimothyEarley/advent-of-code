package de.earley.adventofcode2024.day10

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day10.start()

object Day10 : BaseSolution<Grid<Int>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Int> = input.toGrid(Char::digitToInt)

	override fun partOne(data: Grid<Int>): Long = data
		.pointValues()
		.filter { (_, v) -> v == 0 }
		.map { (p, _) -> reachableWays(data, p) }
		.sumOf { g -> g.pointValues().count { (p, v) -> data[p] == 9 && v > 0 }.toLong() }

	override fun partTwo(data: Grid<Int>): Long = data
		.pointValues()
		.filter { (_, v) -> v == 0 }
		.map { (p, _) -> reachableWays(data, p) }
		.sumOf { g -> g.pointValues().filter { (p, _) -> data[p] == 9 }.sumOf { (_, v) -> v.toLong() } }

	private fun reachableWays(grid: Grid<Int>, from: Point): Grid<Int> {
		val timesReached = grid.map { 0 }.toMutableGrid()
		timesReached[from] = 1
		val open = mutableSetOf(from)
		while (open.isNotEmpty()) {
			val next = open.first()
			open -= next
			open += next.neighbours(false)
				.filter { grid.contains(it) }
				.filter { grid[next]!! + 1 == grid[it] }
				.onEach {
					timesReached[it] = timesReached[it]!! + timesReached[next]!!
				}
		}
		return timesReached
	}
}
