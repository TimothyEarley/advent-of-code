package de.earley.adventofcode2025.day3

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day3.start()

private typealias Joltage = Short

object Day3 : BaseSolution<List<Day3.Bank>, Long, Long>() {

	data class Bank(val batteries : List<Joltage>)

	override fun parseInput(input: Sequence<String>): List<Bank> = input.mapToList {
		Bank(it.map(Char::digitToInt).map(Int::toShort))
	}

	override fun partOne(data: List<Bank>): Long = data.sumOf {
		it.maxJoltage(2)
	}

	override fun partTwo(data: List<Bank>): Long = data.sumOf {
		it.maxJoltage(12)
	}

	private fun Bank.maxJoltage(numberOfBatteries: Int): Long {
		var value = 0L
		var startIndex = 0
		for (i in numberOfBatteries - 1 downTo 0) {
			// Idea: Find the biggest digit after the last number we added while leaving enough for the remaining digits
			val lastPossibleIndex = batteries.lastIndex - i
			val (max, maxIndex) = batteries.maxAndIndex(startIndex, lastPossibleIndex)
			startIndex = maxIndex + 1
			value *= 10
			value += max
		}
		return value
	}

	private fun List<Joltage>.maxAndIndex(after : Int, before: Int): Pair<Joltage, Int> {
		var max = Short.MIN_VALUE
		var maxIndex = -1
		for (i in after .. before) {
			val value = this[i]
			if (value > max) {
				max = value
				maxIndex = i
			}
		}
		return max to maxIndex
	}

}
