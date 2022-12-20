package de.earley.adventofcode2022.day20

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList

fun main() = Day20.start()

object Day20 : BaseSolution<List<Long>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Long> = input.mapToList(String::toLong)

	override fun partOne(data: List<Long>): Long = decrypt(data, 1)

	override fun partTwo(data: List<Long>): Long {
		val decryptionKey = 811589153
		return decrypt(data.map { it.times(decryptionKey) }, 10)
	}

	private fun decrypt(data: List<Long>, times: Int): Long {
		val indexedNumbers = data.withIndex().toList()

		val current = indexedNumbers.toMutableList()

		repeat(times) {
			for (i in data.indices) {
				// move i
				val indexedValue = IndexedValue(i, data[i])
				val position = current.indexOfFirst { it.index == i }

				current.remove(indexedValue)
				val newPosition = position + indexedValue.value

				current.add(newPosition.mod(current.size), indexedValue)
			}
		}

		val zeroPosition = current.indexOfFirst { it.value == 0L }

		return current[(zeroPosition + 1000) % current.size].value +
				current[(zeroPosition + 2000) % current.size].value +
				current[(zeroPosition + 3000) % current.size].value
	}
}
