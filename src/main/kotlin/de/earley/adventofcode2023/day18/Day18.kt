package de.earley.adventofcode2023.day18

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.LongPoint
import de.earley.adventofcode.mapToList
import kotlin.math.abs
import kotlin.math.max

fun main() = Day18.start()

object Day18 : BaseSolution<List<Day18.Line>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Line> = input.mapToList { line ->
		val (a, b, c) = line.split(" ", limit = 3)
		Line(Direction.parseLetter(a.single()), b.toInt(), c)
	}

	data class Line(
		val direction: Direction,
		val steps: Int,
		val colour: String
	)

	override fun partOne(data: List<Line>): Long = area(data)

	private fun area(data: List<Line>): Long {
		var perimeter = 0L
		val loop = data.runningFold(LongPoint(0, 0)) { pos, line ->
			val next = pos + line.direction.point * line.steps
			perimeter += max(abs(pos.x - next.x), abs(pos.y - next.y))
			next
		}

		// Shoelace Formula: https://en.wikipedia.org/wiki/Shoelace_formula
		val area = abs(
			loop.indices.sumOf { i ->
				loop[i].x * (loop.getOrElse(i + 1) { loop.first() }.y - loop.getOrElse(i - 1) { loop.last() }.y)
			} / 2.0
		)

		// Pick's theorem: https://en.wikipedia.org/wiki/Pick%27s_theorem
		// area + 1 - perimeter/2 + perimeter = area + 1 + perimeter/2
		return area.toLong() + 1 + perimeter/2
	}

	override fun partTwo(data: List<Line>): Long = area(
		data.map {
			val hex = it.colour.removePrefix("(#").removeSuffix(")")
			val distHex = hex.take(5)
			val dir = when (hex.last().digitToInt()) {
				0 -> Direction.Right
				1 -> Direction.Down
				2 -> Direction.Left
				3 -> Direction.Up
				else -> error("Wrong dir")
			}
			Line(dir, distHex.toInt(16), it.colour)
		}
	)

}
