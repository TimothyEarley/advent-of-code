package de.earley.adventofcode2023.day10

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode.neighbours
import kotlin.math.abs

fun main() = Day10.start()

object Day10 : BaseSolution<Grid<Day10.Pipe>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Pipe> = input.toList().let { lines ->
		Grid(lines.first().length, lines.size, lines.flatMap { line ->
			line.toCharArray().map { c ->
				Pipe.entries.find { it.symbol == c } ?: error("Not found: $c")
			}.toList()
		})
	}

	enum class Pipe(val symbol: Char) {
		Start('S'),
		Vertical('|'),
		Horizontal('-'),
		NorthEast('L'),
		NorthWest('J'),
		SouthWest('7'),
		SouthEast('F'),
		NoPipe('.'),
	}

	private fun Pipe.connected(from: Point): List<Point> = when (this) {
		Pipe.Start -> error("no idea")
		Pipe.Vertical -> listOf(from + Direction.Up.point, from + Direction.Down.point)
		Pipe.Horizontal -> listOf(from + Direction.Left.point, from + Direction.Right.point)
		Pipe.NorthEast -> listOf(from + Direction.Up.point, from + Direction.Right.point)
		Pipe.NorthWest -> listOf(from + Direction.Up.point, from + Direction.Left.point)
		Pipe.SouthWest -> listOf(from + Direction.Down.point, from + Direction.Left.point)
		Pipe.SouthEast -> listOf(from + Direction.Down.point, from + Direction.Right.point)
		Pipe.NoPipe -> emptyList()
	}

	override fun partOne(data: Grid<Pipe>): Int {
		return loopFromStart(data).size / 2
	}

	private fun loopFromStart(data: Grid<Pipe>): List<Point> {
		val start = data.pointValues().find { it.second == Pipe.Start }!!.first
		val next = start.neighbours().find {
			start in (data[it]?.connected(it) ?: emptyList())
		}!!
		return loop(data, listOf(start), start, next, start)
	}

	private tailrec fun loop(
		grid: Grid<Pipe>,
		loopSoFar: List<Point>,
		prev: Point,
		current: Point,
		end: Point,
	): List<Point> = if (current == end) {
		loopSoFar
	} else {
		loop(grid, loopSoFar + current, current, grid[current]!!.connected(current).find { it != prev }!!, end)
	}

	override fun partTwo(data: Grid<Pipe>): Int {
		val loop = loopFromStart(data)
		// Shoelace Formula: https://en.wikipedia.org/wiki/Shoelace_formula
		val area = abs(loop.indices.sumOf { i ->
			loop[i].x * (loop.getOrElse(i + 1) { loop.first() }.y - loop.getOrElse(i - 1) { loop.last() }.y)
		} / 2.0)
		// Pick's theorem: https://en.wikipedia.org/wiki/Pick%27s_theorem
		val interior = area + 1 - loop.size / 2.0

		return interior.toInt()
	}
}
