package de.earley.adventofcode2021.day10

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.median

fun main() = Day10.start()

object Day10 : BaseSolution<List<String>, Long>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Long = data.sumOf {
		// find first illegal character or 0
		when (it.findFirstError().first) {
			')' -> 3
			']' -> 57
			'}' -> 1197
			'>' -> 25137
			else -> 0
		}.toLong()
	}

	/**
	 * @return the wrong closing char (if any) and the unmatched tail (in correct order to close things,
	 * but opening brackets instead of closing)
	 */
	private fun String.findFirstError(): Pair<Char?, List<Char>> {
		val stack = ArrayDeque<Char>()

		for (c in this) {
			when (c) {
				'(', '[', '{', '<' -> stack.addFirst(c)
				')', ']', '}', '>' -> {
					val first = stack.removeFirst()
					val correct = when (c) {
						')' -> first == '('
						']' -> first == '['
						'}' -> first == '{'
						'>' -> first == '<'
						else -> error("not possible")
					}
					if (!correct) return c to (stack.apply { addFirst(first) })
				}
				else -> error("Illegal char $c")
			}
		}

		return null to stack
	}

	override fun partTwo(data: List<String>): Long = data
		.map { it.findFirstError() }
		.filter { it.first == null }
		.map {
			it.second.fold(0L) { acc, c ->
				acc * 5 + when (c) {
					'(' -> 1
					'[' -> 2
					'{' -> 3
					'<' -> 4
					else -> error("Not possible")
				}
			}
		}
		.median()
}
