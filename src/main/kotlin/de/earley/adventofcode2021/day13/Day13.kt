package de.earley.adventofcode2021.day13

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode2021.split

fun main() = Day13.start()

object Day13 : BaseSolution<Manual, Int, String>() {

	override fun parseInput(input: Sequence<String>): Manual {
		val (dots, instructions) = input.toList().split { it.isBlank() }

		return Manual(
			dots.map(Point.Companion::parse).toSet(),
			instructions.map {
				val (xy, amount) = it.substringAfter("fold along ").split("=", limit = 2)
				if (xy == "x") {
					Vertical(amount.toInt())
				} else {
					Horizontal(amount.toInt())
				}
			}
		)
	}

	override fun partOne(data: Manual): Int = applyFold(data.dots, data.folds.first()).size

	override fun partTwo(data: Manual): String = data.folds.fold(data.dots, ::applyFold).toPrettyString()

	private fun applyFold(points: Set<Point>, f: Fold): Set<Point> = when (f) {
		is Horizontal -> points.map {
			if (it.y < f.y) it else it.copy(y = 2 * f.y - it.y)
		}.toSet()

		is Vertical -> points.map {
			if (it.x < f.x) it else it.copy(x = 2 * f.x - it.x)
		}.toSet()
	}
}

private fun Set<Point>.toPrettyString(): String = buildString {
	val width = this@toPrettyString.maxOf { it.x }
	val height = this@toPrettyString.maxOf { it.y }

	for (y in 0..height) {
		for (x in 0..width) {
			if (contains(Point(x, y))) append('█') else append(' ')
		}
		this.appendLine()
	}
}

data class Manual(
	val dots: Set<Point>,
	val folds: List<Fold>
)

sealed interface Fold
data class Vertical(val x: Int) : Fold
data class Horizontal(val y: Int) : Fold
