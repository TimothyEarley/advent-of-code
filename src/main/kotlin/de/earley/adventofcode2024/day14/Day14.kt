package de.earley.adventofcode2024.day14

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.prettyPrint

fun main() = Day14(101, 103).start()

class Day14(val width: Int, val height: Int) : BaseSolution<List<Day14.Robot>, Long, Long>() {

	data class Robot(
		val pos: Point,
		val vel: Point
	)

	override fun parseInput(input: Sequence<String>): List<Robot> = input.mapToList { line ->
		line.split(" ", limit = 2).let { (p, v) ->
			Robot(
				pos = p.substringAfter('=').split(',').map(String::toInt).let { (x, y) -> Point(x, y) },
				vel = v.substringAfter('=').split(',').map(String::toInt).let { (x, y) -> Point(x, y) }
			)
		}
	}

	override fun partOne(data: List<Robot>): Long =
		data.map {
			afterSteps(it, 100)
		}.groupingBy {
			when {
				it.x < width / 2 && it.y < height / 2 -> '1'
				it.x < width / 2 && it.y > height / 2 -> '2'
				it.x > width / 2 && it.y < height / 2 -> '3'
				it.x > width / 2 && it.y > height / 2 -> '4'
				else -> null
			}
		}
			.eachCount()
			.filter { it.key != null }
			.map { it.value.toLong() }
			.reduce { acc, l -> acc * l }

	private fun afterSteps(it: Robot, steps: Int): Point {
		val result = it.pos + it.vel * steps
		return Point(
			result.x.mod(width),
			result.y.mod(height),
		)
	}

	override fun partTwo(data: List<Robot>): Long {
		var i = 0
		while (true) {
			val positions = data.map { afterSteps(it, i) }
			if (positions.distinct().size == positions.size) {
				// potential match, check if we have a border, i.e.
				// a row of set pixels
				if (positions.any { p ->
						(1..10).all { dx ->
							positions.contains(p.copy(x = p.x + dx))
						}
					}) {
					grid(width, height) { p ->
						if (positions.contains(p)) '█' else ' '
					}.prettyPrint()
					return i.toLong()
				}
			}
			i++
		}

	}

}
