package de.earley.adventofcode2025.day2

import de.earley.adventofcode.BaseSolution

fun main() = Day2.start()

object Day2 : BaseSolution<List<LongRange>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<LongRange> = input.single().split(',').map { range ->
		range.split('-', limit = 2)
			.map(String::toLong)
			.let { LongRange(it[0], it[1]) }
	}

	override fun partOne(data: List<LongRange>): Long = data.sumOf { range ->
		range.filter { isInvalid(it, 2) }.sum()
	}

	override fun partTwo(data: List<LongRange>): Long = data.sumOf { range ->
		range.filter { isInvalid(it, Int.MAX_VALUE) }.sum()
	}

	private fun isInvalid(value: Long, maxPatternCount: Int): Boolean {
		val valueString = value.toString()

		outer@ for (patternLength in 1..valueString.length / 2) {
			val pattern = valueString.take(patternLength)
			if (valueString.length.mod(patternLength) != 0) continue
			val patternCount = valueString.length / patternLength
			if (patternCount > maxPatternCount) continue

			for (i in 1..<patternCount) {
				val patternSegment = valueString.substring(i * patternLength, (i + 1) * patternLength)
				if (patternSegment != pattern) continue@outer
			}

			return true
		}
		return false
	}

}