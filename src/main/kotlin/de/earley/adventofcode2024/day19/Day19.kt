package de.earley.adventofcode2024.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split

typealias Pattern = String

fun main() = Day19.start()

object Day19 : BaseSolution<Day19.Input, Long, Long>() {

	data class Input(
		val availablePatterns: List<Pattern>,
		val desiredPatterns: List<Pattern>
	)

	override fun parseInput(input: Sequence<String>): Input =
		input.toList().split { it.isBlank() }.let { (available, desired) ->
			Input(
				available.single().split(", "),
				desired
			)
		}

	override fun partOne(data: Input): Long = data.desiredPatterns.count {
		possible(it, data.availablePatterns.sortedByDescending(Pattern::length)) > 0
	}.toLong()

	override fun partTwo(data: Input): Long = data.desiredPatterns.sumOf {
		possible(it, data.availablePatterns.sortedByDescending(Pattern::length))
	}

	private val cache: MutableMap<String, Long> = mutableMapOf()
	private fun possible(pattern: Pattern, availablePatterns: List<Pattern>): Long {
		if (cache.contains(pattern)) return cache[pattern]!!

		val result = if (pattern.isEmpty()) {
			1
		} else {
			availablePatterns
				.asSequence()
				.filter { pattern.startsWith(it) }
				.map { pattern.removePrefix(it) }
				.sumOf { possible(it, availablePatterns) }
		}
		cache[pattern] = result
		return result
	}

}
