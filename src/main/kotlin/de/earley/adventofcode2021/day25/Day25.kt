package de.earley.adventofcode2021.day25

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day25.start()

object Day25 : BaseSolution<Grid<SeaCucumber?>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<SeaCucumber?> = input.toGrid { c ->
		when (c) {
			'>' -> SeaCucumber(true)
			'v' -> SeaCucumber(false)
			'.' -> null
			else -> error("")
		}
	}

	override fun partOne(data: Grid<SeaCucumber?>): Int {
		var i = 0
		var g = data
		while (true) {
			i++
			g = g.step() ?: return i
		}
	}

	override fun partTwo(data: Grid<SeaCucumber?>): Int = 0

	private fun Grid<SeaCucumber?>.step(): Grid<SeaCucumber?>? {
		val g = toMutableGrid()
		var moved = false

		val rights = g.pointValues()
			.filter { it.value?.right == true }
			.filter { g[wrap(it.point + Point(1, 0))] == null }
			.toList()
		for (r in rights) {
			moved = true
			g[wrap(r.point + Point(1, 0))] = g[r.point]
			g[r.point] = null
		}

		val downs = g.pointValues()
			.filter { it.value?.right == false }
			.filter { g[wrap(it.point+ Point(0, 1))] == null }
			.toList()
		for (d in downs) {
			moved = true
			g[wrap(d.point + Point(0, 1))] = g[d.point]
			g[d.point] = null
		}

		return g.takeIf { moved }
	}

	private fun Grid<*>.wrap(point: Point): Point = point.copy(
		x = point.x % width,
		y = point.y % height
	)
}

data class SeaCucumber(
	val right: Boolean,
)
