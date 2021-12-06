package de.earley.adventofcode2021.day1

import de.earley.adventofcode2021.BaseSolution

fun main() = Day1.start()

object Day1 : BaseSolution<List<Int>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Int> = input.map { it.toInt() }.toList()

	override fun partOne(data: List<Int>): Int = data.countIncreases()

	override fun partTwo(data: List<Int>): Int = data.windowed(3).map { it.sum() }.countIncreases()

	private fun List<Int>.countIncreases(): Int = zipWithNext { prev, cur ->
		if (cur > prev) 1 else 0
	}.sum()
}
