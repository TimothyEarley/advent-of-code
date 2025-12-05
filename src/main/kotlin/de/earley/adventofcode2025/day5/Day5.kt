package de.earley.adventofcode2025.day5

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split
import kotlin.math.max

fun main() = Day5.start()

object Day5 : BaseSolution<Day5.Inventory, Long, Long>() {

	data class Inventory(
		val ranges: List<LongRange>,
		val ids: List<Long>
	)

	override fun parseInput(input: Sequence<String>): Inventory = input.toList().split { it.isBlank() }.let { (ranges, ids) ->
		Inventory(
			ranges.map { it.split("-").map(String::toLong).let { (l, r) -> LongRange(l, r) } },
			ids.map { it.toLong() }
		)

	}

	override fun partOne(data: Inventory): Long = data.ids.count { id -> data.ranges.any { it.contains(id) } }.toLong()

	override fun partTwo(data: Inventory): Long {
		val sorted = data.ranges.sortedBy { it.first }
		var index = -1L
		var sum = 0L
		sorted.forEach { range ->
			if (index <= range.last) {
				//       F--------L
				//   i     or  i
				sum += range.last - max(range.first, index) + 1
				index = range.last + 1
				//       F---------L
				//                  i
			}
		}

		return sum
	}
}
