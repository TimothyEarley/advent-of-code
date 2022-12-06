package de.earley.adventofcode2022.day6

import de.earley.adventofcode.BaseSolution

fun main() = Day6.start()

object Day6 : BaseSolution<String, Int>() {

	override fun parseInput(input: Sequence<String>): String = input.toList().single()

	override fun partOne(data: String): Int = findStart2(data, 4)

	override fun partTwo(data: String): Int = findStart2(data, 14)

	private fun findStart2(data: String, length: Int): Int = data
		.asSequence()
		.windowed(size = length, partialWindows = false)
		.indexOfFirst { it.toSet().size == length } + length

}
