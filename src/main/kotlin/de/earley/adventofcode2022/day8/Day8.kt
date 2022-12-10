package de.earley.adventofcode2022.day8

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.manhattanDistanceTo

fun main() = Day8.start()

object Day8 : BaseSolution<Grid<Int>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Int> =
		input.toList()
			.let { list ->
				Grid(list.first().length, list.size, list.flatMap { it.map(Char::digitToInt) })
			}

	override fun partOne(data: Grid<Int>): Int = data.indices.count { p ->
		Point.cardinals().any { dir -> checkDir(p, data, dir).first }
	}

	override fun partTwo(data: Grid<Int>): Int = data.indices.maxOf { p ->
		Point.cardinals().map { dir -> checkDir(p, data, dir).second }.reduce(Int::times)
	}

	private fun checkDir(from: Point, data: Grid<Int>, dir: Point): Pair<Boolean, Int> {
		val myHeight = data[from]!!
		var n = from + dir
		while (data.contains(n)) {
			if (data[n]!! >= myHeight) return false to n.manhattanDistanceTo(from)
			n += dir
		}
		return true to n.manhattanDistanceTo(from) - 1
	}
}
