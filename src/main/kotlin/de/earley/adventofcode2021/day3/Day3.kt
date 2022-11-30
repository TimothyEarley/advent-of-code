package de.earley.adventofcode2021.day3

import de.earley.adventofcode.BaseSolution

fun main() = Day3.start()

object Day3 : BaseSolution<List<String>, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int {
		val length = data.first().length

		val positions = (0 until length).map { i ->
			data.map { it[i] }
		}

		val gamma = positions.map { it.mostCommon() }.joinToString("").toInt(2)
		val epsilon = positions.map { it.leastCommon() }.joinToString("").toInt(2)

		return gamma * epsilon
	}

	override fun partTwo(data: List<String>): Int {
		val oxygen = data.findValue { mostCommon() }
		val co2 = data.findValue { leastCommon() }

		return oxygen * co2
	}

	private fun List<String>.findValue(f: List<Char>.() -> Char): Int {
		val candidates = this.toMutableList()
		var bitPosition = 0
		while (candidates.size > 1) {
			val pred = candidates.map { it[bitPosition] }.f()
			candidates.retainAll { it[bitPosition] == pred }
			bitPosition++
		}
		return candidates.single().toInt(2)
	}

	private fun List<Char>.mostCommon(): Char = if (count { it == '0' } > count { it == '1' }) '0' else '1'
	private fun List<Char>.leastCommon(): Char = if (count { it == '0' } > count { it == '1' }) '1' else '0'
}
