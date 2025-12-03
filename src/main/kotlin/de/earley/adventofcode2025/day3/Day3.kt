package de.earley.adventofcode2025.day3

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day3.start()

private typealias Joltage = Int

object Day3 : BaseSolution<List<Day3.Bank>, Long, Long>() {

	data class Bank(val batteries : List<Joltage>)

	override fun parseInput(input: Sequence<String>): List<Bank> = input.mapToList { Bank(it.map { it.digitToInt() }) }

	override fun partOne(data: List<Bank>): Long = data.sumOf {
		val max = it.batteries.max()
		val maxPosition = it.batteries.indexOf(max)

		if (maxPosition == it.batteries.lastIndex) {
			val secondMaxBefore = it.batteries.slice(0 until maxPosition).max()
			(secondMaxBefore * 10 + max).toLong()
		} else {
			val secondMaxAfter = it.batteries.drop(maxPosition + 1).max()
			(max * 10 + secondMaxAfter).toLong()
		}

	}

	override fun partTwo(data: List<Bank>): Long = data.sumOf {
		var value = 0L
		var startIndex = 0
		for (i in 11 downTo  0) {
			val lastPossibleIndex = it.batteries.lastIndex - i
			val max = it.batteries.slice(startIndex .. lastPossibleIndex).max()
			val maxIndex = it.batteries.slice(startIndex .. lastPossibleIndex).indexOf(max) + startIndex
			startIndex = maxIndex + 1
			value *= 10
			value += max
		}
		value
	}

}
