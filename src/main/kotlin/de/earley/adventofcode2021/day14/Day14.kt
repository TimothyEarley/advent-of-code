package de.earley.adventofcode2021.day14

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split

fun main() = Day14.start()

object Day14 : BaseSolution<Input, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Input =
		input.toList().split { it.isBlank() }.let { (poly, rules) ->
			Input(
				poly.single(),
				rules.associate {
					it.split(" -> ", limit = 2).let { (h, t) ->
						require(h.length == 2)
						(h[0] to h[1]) to t.single()
					}
				}
			)
		}

	override fun partOne(data: Input): Long = runAll(data, 10)

	override fun partTwo(data: Input): Long = runAll(data, 40)

	private fun runAll(data: Input, steps: Int): Long {
		var pairCounts: Map<Pair<Char, Char>, Long> = data.startingPolymer
			.zipWithNext().associateWith { 1 }

		repeat(steps) {
			val newState = mutableMapOf<Pair<Char, Char>, Long>()
			pairCounts.forEach { (s, i) ->
				for (p in newPairsAfterStep(s, data.rules)) {
					newState.merge(p, i) { x, y -> x + y }
				}
			}
			pairCounts = newState
		}

		val groups = pairCounts.entries
			.groupBy({ it.key.first }, { it.value })
			.mapValues { it.value.sum() }
			.toMutableMap()

		// add the last Char since it is not the start of pair and so is missing
		groups.merge(data.startingPolymer.last(), 1) { x, y -> x + y }

		return groups.maxOf(Map.Entry<Char, Long>::value) - groups.minOf(Map.Entry<Char, Long>::value)
	}

	private fun newPairsAfterStep(poly: Pair<Char, Char>, rules: Map<Pair<Char, Char>, Char>): List<Pair<Char, Char>> =
		when (val replace = rules[poly]) {
			null -> listOf(poly)
			else -> listOf(poly.first to replace, replace to poly.second)
		}
}

data class Input(
	val startingPolymer: String,
	val rules: Map<Pair<Char, Char>, Char>,
)
