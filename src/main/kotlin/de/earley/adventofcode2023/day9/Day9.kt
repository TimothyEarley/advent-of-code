package de.earley.adventofcode2023.day9

import de.earley.adventofcode.BaseSolution

fun main() = Day9.start()

object Day9 : BaseSolution<List<List<Int>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<List<Int>> = input.toList()
		.map { it.split(" ").map(String::toInt) }

	override fun partOne(data: List<List<Int>>): Long = data.sumOf { line ->
		line.diffTillZeros().sumOf { it.last() }.toLong()
	}

	private fun List<Int>.diffTillZeros(): List<List<Int>> =
		if (this.all { it == 0 }) {
			listOf(this)
		} else {
			listOf(this) + this.zipWithNext { a, b -> b - a }.diffTillZeros()
		}

	override fun partTwo(data: List<List<Int>>): Long = data.sumOf { line ->
		line.diffTillZeros()
			.map { it.first() }
			.reversed()
			.fold(0L) { acc, i -> i - acc }
			.toLong()
	}
}
