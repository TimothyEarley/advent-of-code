package de.earley.adventofcode2024.day9

import de.earley.adventofcode.BaseSolution

fun main() = Day9.start()

object Day9 : BaseSolution<List<Int>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Int> = input.single().toList().map { it.digitToInt() }

	override fun partOne(data: List<Int>): Long = defrag(data, onlyWhole = false)

	override fun partTwo(data: List<Int>): Long = defrag(data, onlyWhole = true)

	private data class Entry(
		var isFile: Boolean,
		val id: Int,
		// we need to keep track of both the size left of this segment when looping through
		var size: Int,
		// and the amount of data left. When moving data from further back we reduce this
		// but not the size
		var dataLeft: Int
	)

	private fun defrag(data: List<Int>, onlyWhole: Boolean): Long {
		val totalSize = data.sum()
		val stack = data.withIndex()
			.map { Entry(it.index % 2 == 0, it.index / 2, it.value, it.value) }
			.toMutableList()

		var result = 0L
		for (i in 0 until totalSize) {
			// skip all empty entries
			while (stack.first().size <= 0) {
				stack.removeFirst()
			}
			val first = stack.first()
			if (first.isFile && first.dataLeft > 0) {
				result += i * first.id
				first.dataLeft--
			} else {
				val last = stack.lastOrNull {
					it.isFile &&
						it.dataLeft > 0 &&
						(!onlyWhole || it.dataLeft <= first.size)
				}
				if (last != null) {
					// we can fill in this gap
					result += i * last.id
					last.dataLeft--
					if (last.dataLeft <= 0) {
						// file is gone, so mark accordingly
						// (this is also the reason for differentiating between
						// size and dataLeft)
						last.isFile = false
					}
				}
			}
			// advance
			first.size--
		}

		return result
	}

}
