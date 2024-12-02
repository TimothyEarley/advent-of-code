package de.earley.adventofcode2024.day2

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day2.start()

object Day2 : BaseSolution<List<List<Int>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<List<Int>> = input.mapToList {
		it.split(" ").map(String::toInt)
	}

	override fun partOne(data: List<List<Int>>): Long = data.count(::safe).toLong()

	override fun partTwo(data: List<List<Int>>): Long = data.count { line ->
		line.indices.any { i ->
			safe(line.toMutableList().also { it.removeAt(i) })
		}
	}.toLong()

	private fun safe(list: List<Int>) =
		list.zipWithNext().all { it.first < it.second && it.second - it.first < 4 } ||
			list.zipWithNext().all { it.first > it.second && it.first - it.second < 4 }
}
