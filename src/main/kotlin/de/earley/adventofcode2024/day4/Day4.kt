package de.earley.adventofcode2024.day4

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours

fun main() = Day4.start()

object Day4 : BaseSolution<Grid<Char>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Char> = input.toList().let { lines ->
		Grid(lines.first().length, lines.size, lines.flatMap { it.toList() }.toList())
	}

	override fun partOne(data: Grid<Char>): Long = data.pointValues().sumOf { (p, v) ->
		if (v == 'X') {
			(Point(0, 0).neighbours(true)).count { dir ->
				val m = data[p + dir * 1]
				val a = data[p + dir * 2]
				val s = data[p + dir * 3]
				m == 'M' && a == 'A' && s == 'S'
			}.toLong()
		} else {
			0L
		}
	}

	override fun partTwo(data: Grid<Char>): Long = data.pointValues().count { (p, v) ->
		if (v == 'A') {
			val topLeft = data[p + Point(-1, -1)]
			val topRight = data[p + Point(1, -1)]
			val botLeft = data[p + Point(-1, 1)]
			val botRight = data[p + Point(1, 1)]

			((topLeft == 'M' && botRight == 'S') || (topLeft == 'S' && botRight == 'M')) &&
				((botLeft == 'M' && topRight == 'S') || (botLeft == 'S' && topRight == 'M'))
		} else {
			false
		}
	}.toLong()
}
