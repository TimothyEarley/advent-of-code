package de.earley.adventofcode2021.day25

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Grid
import de.earley.adventofcode2021.Point
import de.earley.adventofcode2021.toMutableGrid

fun main() = Day25.start()

object Day25 : BaseSolution<Grid<SeaCucumber?>, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<SeaCucumber?> {
		val l = input.toList()
		val width = l.first().length
		val height = l.size
		return Grid(
			width, height,
			l.flatMap {
				it.map { c ->
					when (c) {
						'>' -> SeaCucumber(true)
						'v' -> SeaCucumber(false)
						'.' -> null
						else -> error("")
					}
				}
			}
		)
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
			.filter { it.second?.right == true }
			.filter { g[wrap(it.first + Point(1, 0))] == null }
			.toList()
		for (r in rights) {
			moved = true
			g[wrap(r.first + Point(1, 0))] = g[r.first]
			g[r.first] = null
		}

		val downs = g.pointValues()
			.filter { it.second?.right == false }
			.filter { g[wrap(it.first + Point(0, 1))] == null }
			.toList()
		for (d in downs) {
			moved = true
			g[wrap(d.first + Point(0, 1))] = g[d.first]
			g[d.first] = null
		}

		return g.takeIf { moved }
	}

	private fun Grid<*>.wrap(point: Point): Point = point.copy(
		x = point.x % width,
		y = point.y % height
	)
}

data class SeaCucumber(
	val right: Boolean
)
