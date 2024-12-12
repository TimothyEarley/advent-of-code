package de.earley.adventofcode2023.day21

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid
import space.kscience.kmath.functions.ListPolynomial
import space.kscience.kmath.functions.asFunctionOver
import space.kscience.kmath.misc.toIntExact
import space.kscience.kmath.operations.LongRing

fun main() = Day21(64).start()

data class Day21(
	val steps: Int,
	val actualSteps: Long = 26501365,
) : BaseSolution<Grid<Day21.Tile>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Tile> = input.toGrid { c ->
		when (c) {
			'.' -> Tile.Empty
			'S' -> Tile.Start
			'#' -> Tile.Rock
			else -> error("")
		}
	}

	enum class Tile {
		Empty, Start, Rock
	}

	override fun partOne(data: Grid<Tile>): Long =
		(1..steps).fold(setOf(data.pointValues().find { it.second == Tile.Start }!!.first)) { acc, _ ->
			acc.next(data)
		}.size.toLong()

	private fun Set<Point>.next(data: Grid<Tile>): Set<Point> =
		asSequence()
			.flatMap { it.neighbours() }
			.filter { it in data }
			.filter { data[it] != Tile.Rock }
			.toSet()

	override fun partTwo(data: Grid<Tile>): Long {
		val start = data.pointValues().find { it.second == Tile.Start }!!.first

		// S is in the center, rows/cols from S are clear, outside is clear
		// => the answer function is a quadratic equation

		// the number of steps not included in the cycling pattern, start simulation at that
		val rem = actualSteps.rem(data.width).toIntExact()

		var ps = setOf(start)
		repeat(rem) {
			ps = ps.nextWrap(data)
		}
		val at0 = ps.size

		repeat(data.width) {
			ps = ps.nextWrap(data)
		}
		val at1 = ps.size

		repeat(data.width) {
			ps = ps.nextWrap(data)
		}
		val at2 = ps.size

		// so we note the formula for reachable after x cycles has
		// f(x) = a * x^2 + b * x + c
		// f(0) = at0, f(1) = at1, f(2) = at2
		// c = at0
		// a + b + c = at1    ==> b = at1 - a - at0
		// 4a + 2b + c = at2  ==> 4a + 2at1 - 2a - 2at0 + at0 = at2
		//                    ==> 2a + 2at1 - at0 = at2
		//                    ==> a = (at2 + at0) / 2 - at1
		// and then finally   ==> b = at1 - at2/2 - at0/2 + at1 - at0
		//                        b = 2at1 - at2/2 - 3*at0/2

		val quadratic = ListPolynomial(
			at0.toLong(),
			(2 * at1 - at2 / 2.0 - 3 * at0 / 2.0).toLong(),
			((at2 + at0) / 2 - at1).toLong(),
		)

		return quadratic.asFunctionOver(LongRing).invoke(actualSteps / data.width)
	}

	private fun Set<Point>.nextWrap(data: Grid<Tile>): Set<Point> =
		asSequence()
			.flatMap { it.neighbours() }
			.filter { data[Point(it.x.mod(data.width), it.y.mod(data.height))] != Tile.Rock }
			.toSet()
}
