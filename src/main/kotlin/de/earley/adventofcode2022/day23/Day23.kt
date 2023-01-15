package de.earley.adventofcode2022.day23

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.toGrid

fun main() = Day23.start()

object Day23 : BaseSolution<List<Point>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Point> = input.toList().let { lines ->
		val data = lines.flatMap { it.toCharArray().toList() }
		val grid = Grid<Char>(lines[0].length, lines.size, data)

		grid.pointValues()
			.filter { it.second == '#' }
			.map { it.first }
			.toList()
	}

	override fun partOne(data: List<Point>): Int =
		(0 until 10).fold(data) { acc, i ->
			simulateRound(acc, i).first
		}.toGrid().values().count { !it }

	override fun partTwo(data: List<Point>): Int = generateSequence(0) { it + 1 }
		.runningFold(data to true) { acc, i -> simulateRound(acc.first, i) }
		.indexOfFirst { !it.second }

	private fun simulateRound(data: List<Point>, round: Int): Pair<List<Point>, Boolean> {
		var moved = false
		val desired = data.map { p ->

			val n = p + Point(0, -1) in data
			val ne = p + Point(1, -1) in data
			val e = p + Point(1, 0) in data
			val se = p + Point(1, 1) in data
			val s = p + Point(0, 1) in data
			val sw = p + Point(-1, 1) in data
			val w = p + Point(-1, 0) in data
			val nw = p + Point(-1, -1) in data

			p to when {
				!n && !ne && !e && !se && !s && !sw && !w && !nw -> p

				// meh
				round % 4 == 0 && !n && !ne && !nw -> p + Point(0, -1)
				round % 4 == 0 && !s && !se && !sw -> p + Point(0, 1)
				round % 4 == 0 && !w && !nw && !sw -> p + Point(-1, 0)
				round % 4 == 0 && !e && !ne && !se -> p + Point(1, 0)

				round % 4 == 1 && !s && !se && !sw -> p + Point(0, 1)
				round % 4 == 1 && !w && !nw && !sw -> p + Point(-1, 0)
				round % 4 == 1 && !e && !ne && !se -> p + Point(1, 0)
				round % 4 == 1 && !n && !ne && !nw -> p + Point(0, -1)

				round % 4 == 2 && !w && !nw && !sw -> p + Point(-1, 0)
				round % 4 == 2 && !e && !ne && !se -> p + Point(1, 0)
				round % 4 == 2 && !n && !ne && !nw -> p + Point(0, -1)
				round % 4 == 2 && !s && !se && !sw -> p + Point(0, 1)

				round % 4 == 3 && !e && !ne && !se -> p + Point(1, 0)
				round % 4 == 3 && !n && !ne && !nw -> p + Point(0, -1)
				round % 4 == 3 && !s && !se && !sw -> p + Point(0, 1)
				round % 4 == 3 && !w && !nw && !sw -> p + Point(-1, 0)

				else -> p
			}
		}

		val actual = desired.map { (current, desire) ->
			when {
				desired.count { it.second == desire } > 1 -> current
				else -> desire.also { if (current != desire) moved = true }
			}
		}

		return actual to moved
	}
}
