package de.earley.adventofcode2021.day20

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode2021.split

fun main() = Day20.start()

object Day20 : BaseSolution<Input, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Input = input.toList().split { it.isBlank() }.let { (rep, grid) ->
		val width = grid.first().length
		val height = grid.size
		Input(
			rep.single(),
			Image(Grid(width, height, grid.flatMap { it.toList() }), '.')
		)
	}

	override fun partOne(data: Input): Int = solve(data, 2)
	override fun partTwo(data: Input): Int = solve(data, 50)

	private fun solve(data: Input, times: Int): Int =
		data
			.image.multipleEnhance(times, data.replacement)
			.data.values().count { it == '#' }

	private val threeByThree = listOf(
		Point(-1, -1),
		Point(0, -1),
		Point(1, -1),
		Point(-1, 0),
		Point(0, 0),
		Point(1, 0),
		Point(-1, +1),
		Point(0, +1),
		Point(1, +1),
	)

	private fun Image.multipleEnhance(times: Int, replacement: String): Image = (1..times).fold(this) { img, _ ->
		img.enhance(replacement)
	}

	private fun Image.enhance(replacement: String): Image {
		val extended = grid(data.width + 2, data.height + 2) { p ->
			val oldPoint = p.copy(x = p.x - 1, y = p.y - 1)
			val number = threeByThree.map { it + oldPoint }
				.map { data[it] ?: background }
				.fold(0) { acc, c ->
					(acc shl 1) or when (c) {
						'#' -> 1
						'.' -> 0
						else -> error("Wrong char")
					}
				}
			replacement[number]
		}
		val newBackground = when (background) {
			'#' -> replacement[0b111111111]
			'.' -> replacement[0]
			else -> error("Wrong char")
		}
		return Image(extended, newBackground)
	}
}

data class Input(
	val replacement: String,
	val image: Image,
)

data class Image(
	val data: Grid<Char>,
	val background: Char,
)
