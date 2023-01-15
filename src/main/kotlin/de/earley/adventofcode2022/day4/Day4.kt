package de.earley.adventofcode2022.day4

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList

fun main() = Day4.start()

object Day4 : BaseSolution<List<Pair<IntRange, IntRange>>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Pair<IntRange, IntRange>> = input.mapToList {
		fun parseRange(s: String): IntRange = s
			.split("-", limit = 2)
			.let { (x, y) -> x.toInt()..y.toInt() }

		it.split(",", limit = 2).map(::parseRange).let { (a, b) -> Pair(a, b) }
	}

	override fun partOne(data: List<Pair<IntRange, IntRange>>): Int = data.count { (a, b) ->
		a.contains(b) || b.contains(a)
	}

	override fun partTwo(data: List<Pair<IntRange, IntRange>>): Int = data.count { (a, b) ->
		(a intersect b).isNotEmpty()
	}

	private fun IntRange.contains(other: IntRange): Boolean = first <= other.first && other.last <= last
}
