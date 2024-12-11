package de.earley.adventofcode2024.day11

import de.earley.adventofcode.BaseSolution
import kotlin.math.log10
import kotlin.math.pow

fun main() = Day11.start()

object Day11 : BaseSolution<List<Long>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Long> = input.single().split(" ").map(String::toLong)

	override fun partOne(data: List<Long>): Long = runFor(data, 25)
	override fun partTwo(data: List<Long>): Long = runFor(data, 75)

	private fun runFor(data: List<Long>, times: Int): Long {
		var frequencies = data.groupingBy { it }
			.eachCount()
			.mapValues { it.value.toLong() }
		repeat(times) {
			frequencies = frequencies.blink()
		}
		return frequencies.values.sum()
	}

	private fun Map<Long, Long>.blink(): Map<Long, Long> {
		val newFrequencies = LinkedHashMap<Long, Long>(2 * size)
		fun add(k: Long, v: Long) {
			newFrequencies.compute(k) { _: Long, prev: Long? ->
				(prev ?: 0L) + v
			}
		}
		forEach { (key, value) ->
			val numDigits = (log10(key.toDouble()) + 1).toInt()
			when {
				key == 0L -> add(1L, value)
				numDigits % 2 == 0 -> {
					val splitter = 10.0.pow(numDigits / 2.0)
					add((key / splitter).toLong(), value)
					add((key % splitter).toLong(), value)
				}
				else -> add(key * 2024, value)
			}
		}
		return newFrequencies
	}
}
