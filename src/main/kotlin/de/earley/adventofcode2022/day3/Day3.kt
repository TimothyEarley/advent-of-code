package de.earley.adventofcode2022.day3

import de.earley.adventofcode.BaseSolution

fun main() = Day3.start()

object Day3 : BaseSolution<List<String>, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int = data.sumOf { s ->

		val first = s.subSequence(0, s.length / 2)
		val second = s.subSequence(s.length / 2, s.length)

		val common = first.toSet().intersect(second.toSet())

		common.sumOf(::letterScore)
	}

	override fun partTwo(data: List<String>): Int = data.chunked(3).sumOf { (a, b, c) ->
		val common = a.toSet().intersect(b.toSet()).intersect(c.toSet()).single()
		letterScore(common)
	}

	private fun letterScore(it: Char) = if (it.isUpperCase()) it - 'A' + 27 else it - 'a' + 1

}
