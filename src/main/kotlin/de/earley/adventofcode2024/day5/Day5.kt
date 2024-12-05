package de.earley.adventofcode2024.day5

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split

fun main() = Day5.start()


data class Input(
	// map from number to what must come before,
	// i.e. given x|y then y becomes the key and x a value
	val rules: Rules,
	val updates: List<Update>
)

private typealias Rules = Map<Int, Set<Int>>
private typealias Update = List<Int>

object Day5 : BaseSolution<Input, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Input = input.toList()
		.split { it.isBlank() }.let { (rules, updates) ->
			Input(
				rules = rules
					.map {
						it.split('|').map(String::toInt).let { (x, y) -> x to y }
					}
					.groupBy(Pair<Int, Int>::second, Pair<Int, Int>::first)
					.mapValues { it.value.toSet() },
				updates = updates.map { it.split(',').map(String::toInt) }
			)
		}

	override fun partOne(data: Input): Long =
		data.updates
			.filter { firstError(it, data.rules) == null }
			.sumMiddles()

	override fun partTwo(data: Input): Long =
		data.updates
			.filterNot { firstError(it, data.rules) == null }
			.map { correct(it, data.rules) }
			.sumMiddles()

	private fun List<Update>.sumMiddles(): Long = sumOf {
		it[it.size / 2].toLong()
	}

	private fun firstError(update: List<Int>, rules: Rules): Pair<Int, Int>? {
		for (i in update.indices) {
			val x = update[i]
			val before = rules[x] ?: continue
			for (j in i + 1..update.lastIndex) {
				if (update[j] in before) {
					return i to j
				}
			}
		}
		return null
	}

	private fun correct(update: List<Int>, rules: Rules): List<Int> {
		val newUpdate = update.toMutableList()
		var error = firstError(newUpdate, rules)
		while (error != null) {
			// sort the error
			val x = newUpdate[error.first]
			newUpdate.removeAt(error.first)
			// find first possible location
			val before = rules[x]!!
			for (i in error.second..newUpdate.size) {
				if (i < newUpdate.size && newUpdate[i] in before) continue
				newUpdate.add(i, x)
				break
			}
			error = firstError(newUpdate, rules)
		}
		return newUpdate
	}

}
