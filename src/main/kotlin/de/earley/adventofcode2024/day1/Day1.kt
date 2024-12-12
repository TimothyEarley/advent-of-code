package de.earley.adventofcode2024.day1

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList
import kotlin.math.abs

fun main() = Day1.start()

object Day1 : BaseSolution<Pair<List<Int>, List<Int>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Pair<List<Int>, List<Int>> {
		val lines = input.mapToList { line -> line.split(" +".toRegex(), limit = 2).map { it.toInt() } }
		return lines.map { it[0] } to lines.map { it[1] }
	}

	override fun partOne(data: Pair<List<Int>, List<Int>>): Long {
		return data.first.sorted().zip(data.second.sorted())
			.sumOf { abs(it.first - it.second).toLong() }
	}

	override fun partTwo(data: Pair<List<Int>, List<Int>>): Long = data.first.sumOf { a ->
		a * data.second.count { it == a }.toLong()
	}
}
