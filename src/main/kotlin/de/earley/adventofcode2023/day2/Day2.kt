package de.earley.adventofcode2023.day2

import de.earley.adventofcode.BaseSolution
import kotlin.math.max

fun main() = Day2.start()

object Day2 : BaseSolution<List<Day2.Game>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Game> = input.mapIndexed { i, line ->
		val pulls = line.removePrefix("Game ${i + 1}: ")
		val reveals = pulls.split(";").map { pull ->
			pull.trim().split(",").fold(Reveal.zero) { acc, it ->
				val (count, colour) = it.trim().split(" ", limit = 2)
				when (colour) {
					"red" -> acc.copy(red = acc.red + count.toInt())
					"green" -> acc.copy(green = acc.green + count.toInt())
					"blue" -> acc.copy(blue = acc.blue + count.toInt())
					else -> error("Unexpected colour: $colour")
				}
			}
		}
		Game(i + 1, reveals)
	}.toList()

	data class Game(
		val id: Int,
		val reveals: List<Reveal>,
	)

	data class Reveal(
		val red: Int,
		val green: Int,
		val blue: Int,
	) {
		companion object {
			val zero: Reveal = Reveal(0, 0, 0)
		}
	}

	override fun partOne(data: List<Game>): Int {
		return data.filter { it.possible(Reveal(12, 13, 14)) }
			.sumOf { it.id }
	}

	private fun Game.possible(actual: Reveal): Boolean = reveals.all { it.possible(actual) }
	private fun Reveal.possible(actual: Reveal): Boolean =
		red <= actual.red && green <= actual.green && blue <= actual.blue

	override fun partTwo(data: List<Game>): Int = data.sumOf { game ->
		game.reveals.fold(Reveal.zero, ::max).let { min ->
			min.red * min.green * min.blue
		}
	}

	private fun max(a: Reveal, b: Reveal): Reveal = Reveal(
		red = max(a.red, b.red),
		green = max(a.green, b.green),
		blue = max(a.blue, b.blue),
	)
}
