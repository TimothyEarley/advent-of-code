package de.earley.adventofcode2022.day1

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split

fun main() = Day1.start()

object Day1 : BaseSolution<List<Int>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Int> =
		input.toList()
			.split { it.isBlank() }
			.map { oneElf -> oneElf.sumOf { it.toInt() } }


	override fun partOne(data: List<Int>): Int = data.max()

	override fun partTwo(data: List<Int>): Int = data.sortedDescending().take(3).sum()

}
