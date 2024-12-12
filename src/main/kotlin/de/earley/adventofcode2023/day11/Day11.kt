package de.earley.adventofcode2023.day11

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.toGrid
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() = Day11(1000000).start()

class Day11(private val expansion: Int) : BaseSolution<Grid<Boolean>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Boolean> = input.toGrid {
		it == '#'
	}

	override fun partOne(data: Grid<Boolean>): Long = solve(data, 2)

	override fun partTwo(data: Grid<Boolean>): Long = solve(data, expansion)

	private fun solve(data: Grid<Boolean>, expansion: Int): Long {
		val expandColumns = (0 ..< data.width).filter { x ->
			(0 ..< data.height).all { y -> !data[x, y]!! }
		}.toSet()

		val expandRows = (0 ..< data.height).filter { y ->
			(0 ..< data.width).all { x -> !data[x, y]!! }
		}.toSet()

		val galaxies = data.pointValues()
			.filter { it.second }
			.map { it.first }

		return galaxies.sumOf { a ->
			galaxies.filter { it.x > a.x || (it.x == a.x && it.y > a.y) }
				.sumOf { b ->
					val distanceX = abs(a.x - b.x) +
						expandColumns.count { min(a.x, b.x) < it && it < max(a.x, b.x) }.toLong() * (expansion - 1)
					val distanceY = abs(a.y - b.y) +
						expandRows.count { min(a.y, b.y) < it && it < max(a.y, b.y) }.toLong() * (expansion - 1)
					distanceX + distanceY
				}
		}
	}
}
