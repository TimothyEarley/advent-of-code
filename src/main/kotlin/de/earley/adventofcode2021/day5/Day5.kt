package de.earley.adventofcode2021.day5

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Point
import kotlin.math.sign

fun main() = Day5.start()

data class Segment(val start: Point, val end: Point)

object Day5 : BaseSolution<List<Segment>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Segment> = input.map {
		val (from, to) = it.split(" -> ", limit = 2)
		Segment(Point.parse(from), Point.parse(to))
	}.toList()

	override fun partOne(data: List<Segment>): Int = countOverlaps(
		data.filter {
			it.start.x - it.end.x == 0 || it.start.y - it.end.y == 0
		}
	)

	override fun partTwo(data: List<Segment>): Int = countOverlaps(data)

	// iterate over points on the line (only horizontal, vertical or 45° work)
	private fun Point.lineTo(other: Point): Iterator<Point> = iterator {
		val diff = other - this@lineTo
		val diffStep = Point(diff.x.sign, diff.y.sign)
		var current = this@lineTo
		while (current != other + diffStep) {
			yield(current)
			current += diffStep
		}
	}

	private fun countOverlaps(lines: List<Segment>): Int {
		val counts: MutableMap<Point, Int> = hashMapOf()
		for (line in lines) {
			for (p in (line.start.lineTo(line.end))) {
				counts.compute(p) { _, i ->
					(i ?: 0) + 1
				}
			}
		}
		return counts.count { it.value >= 2 }
	}
}
