package de.earley.adventofcode2021.day5

import de.earley.adventofcode2021.BaseSolution
import kotlin.math.sign

fun main() = Day5.start()

data class Point(val x: Int, val y: Int) {
	companion object {
		fun parse(s: String): Point = s.split(",", limit = 2).let { (x, y) ->
			Point(x.toInt(), y.toInt())
		}
	}

	operator fun minus(other: Point): Point = Point(x - other.x, y - other.y)
	operator fun plus(other: Point) = Point(x + other.x, y + other.y)

	// iterate over points on the line (only horizontal, vertical or 45° work)
	fun lineTo(other: Point): Iterator<Point> = iterator {
		val diff = other - this@Point
		val diffStep = Point(diff.x.sign, diff.y.sign)
		var current = this@Point
		while (current != other + diffStep) {
			yield(current)
			current += diffStep
		}
	}
}

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
