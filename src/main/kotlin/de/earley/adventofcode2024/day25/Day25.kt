package de.earley.adventofcode2024.day25

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split
import de.earley.adventofcode.toGrid

fun main() = Day25.start()

object Day25 : BaseSolution<Day25.Input, Long, Long>() {

	data class Input(
		val locks: List<List<Int>>,
		val keys: List<List<Int>>
	)

	override fun parseInput(input: Sequence<String>): Input = input
		.toList()
		.split { it.isBlank() }
		.map { block ->
			block.toGrid {
				when (it) {
					'#' -> true
					'.' -> false
					else -> error("")
				}
			}
		}
		.map { grid ->
			if (grid[0, 0] == true) {
				// lock
				val heights = (0..<grid.width).map { x ->
					(0..<grid.height).first { y -> grid[x, y] == false } - 1
				}
				null to heights
			} else {
				// key
				val heights = (0..<grid.width).map { x ->
					grid.height - (grid.height - 1 downTo 0).first { y -> grid[x, y] == false } - 2
				}
				heights to null
			}
		}.let { blocks ->
			Input(
				locks = blocks.mapNotNull { it.second },
				keys = blocks.mapNotNull { it.first }
			)
		}

	override fun partOne(data: Input): Long = data.locks.sumOf { lock ->
			data.keys.count { key ->
				(lock.zip(key).all {
					it.second <= 5 - it.first
				})
			}.toLong()
		}

	override fun partTwo(data: Input): Long = 0

}
