package de.earley.adventofcode2022.day25

import de.earley.adventofcode.BaseSolution
import kotlin.math.pow

fun main() = Day25.start()

object Day25 : BaseSolution<List<String>, String, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): String = data.sumOf {
		it.fromSNAFUToInt()
	}.toSNAFU()

	override fun partTwo(data: List<String>): Int = 0

	fun Long.toSNAFU(): String {
		val base5 = toString(radix = 5)
		val result = base5.reversed().toCharArray().toMutableList()

		tailrec fun inc(at: Int) {
			if (at >= result.size) result.add('0')
			when (result[at]) {
				'=' -> result[at] = '-'
				'-' -> result[at] = '0'
				'0' -> result[at] = '1'
				'1' -> result[at] = '2'

				// these cascade down
				'2' -> {
					result[at] = '='
					inc(at + 1)
				}

				'3' -> {
					result[at] = '-'
					inc(at + 1)
				}

				'4' -> {
					result[at] = '0'
					inc(at + 1)
				}
			}
		}

		for (i in 0 until result.size) {
			when (result[i]) {
				'3' -> {
					result[i] = '='
					inc(i + 1)
				}

				'4' -> {
					result[i] = '-'
					inc(i + 1)
				}

				else -> {
					// nothing
				}
			}
		}

		return result.reversed().joinToString("")
	}

	private fun String.fromSNAFUToInt(): Long {
		return this.reversed().foldIndexed(0L) { index, acc, c ->

			val worth = 5.0.pow(index).toLong()

			Math.addExact(
				acc,
				Math.multiplyExact(
					when (c) {
						'2' -> 2
						'1' -> 1
						'0' -> 0
						'-' -> -1
						'=' -> -2
						else -> error("Unknown digit $c")
					},
					worth
				)
			)
		}
	}
}
