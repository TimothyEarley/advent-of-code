package de.earley.adventofcode2021.day11

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.Grid
import de.earley.adventofcode2021.MutableGrid
import de.earley.adventofcode2021.Point
import de.earley.adventofcode2021.neighbours
import de.earley.adventofcode2021.toMutableGrid

fun main() = Day11.start()

object Day11 : BaseSolution<Grid<Int>, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Int> =
		Grid(10, 10, input.flatMap { it.toList().map(Char::digitToInt) }.toList())

	override fun partOne(data: Grid<Int>): Int = runSimulation(data).take(100).sum()

	override fun partTwo(data: Grid<Int>): Int = runSimulation(data).indexOf(100) + 1

	private fun runSimulation(data: Grid<Int>): Sequence<Int> = sequence {
		val state = data.toMutableGrid()
		while (true) {
			yield(step(state))
		}
	}

	/**
	 * @return number of flashes
	 */
	private fun step(data: MutableGrid<Int>): Int {
		// increase by one
		data.mutate { it + 1 }

		// if flash, propagate
		val flashed = mutableSetOf<Point>()
		do {
			var didFlash = false
			data.pointValues().filter { (p, v) ->
				v > 9 && p !in flashed
			}.forEach { (p, _) ->
				flashed += p
				didFlash = true
				for (n in p.neighbours(true)) {
					val vn = data[n] ?: continue
					data[n] = vn + 1
				}
			}
		} while (didFlash)

		// set flashes to 0
		flashed.forEach {
			data[it] = 0
		}

		return flashed.size
	}
}
