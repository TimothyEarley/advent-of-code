package de.earley.adventofcode2022.day3

import de.earley.adventofcode.BaseSolution

fun main() = Day3.start()

object Day3 : BaseSolution<List<String>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int = data.sumOf { s ->
		val first = s.substring(0 until s.length / 2).toSet()
		val second = s.substring(s.length / 2).toSet()

		val common = first intersect second
		common.sumOf(::letterScore)
	}

	override fun partTwo(data: List<String>): Int = data
		.map(String::toSet)
		.chunked(3)
		.map { it.reduce(Set<Char>::intersect).single() } // map to common
		.sumOf(::letterScore)

	private fun letterScore(it: Char) = if (it.isUpperCase()) it - 'A' + 27 else it - 'a' + 1
}
