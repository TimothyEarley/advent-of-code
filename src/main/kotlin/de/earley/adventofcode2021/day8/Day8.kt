package de.earley.adventofcode2021.day8

import de.earley.adventofcode2021.BaseSolution

fun main() = Day8.start()

object Day8 : BaseSolution<List<Reading>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Reading> =
		input.map {
			val (signal, output) = it.split('|', limit = 2)
			Reading(parseDigits(signal), parseDigits(output))
		}.toList()

	private fun parseDigits(s: String): List<Digit> = s.split(' ').filter { it.isNotBlank() }.map { d ->
		d.map {
			Segment.valueOf(it.uppercase())
		}.toSet()
	}

	private val uniqueCounts = setOf(2, 4, 3, 7)
	override fun partOne(data: List<Reading>): Int =
		data.flatMap(Reading::output).count { it.size in uniqueCounts }

	override fun partTwo(data: List<Reading>): Int = data.sumOf(Reading::decodeOutput)
}

enum class Segment {
	A, B, C, D, E, F, G
}

typealias Digit = Set<Segment>

data class Reading(val signal: List<Digit>, val output: List<Digit>) {
	fun decodeOutput(): Int {
		// since the signal contains each number exactly once, we just need to find them
		val bySize = signal.groupBy { it.size }

		// the easy ones
		val one = bySize[2]!!.single()
		val four = bySize[4]!!.single()
		val seven = bySize[3]!!.single()
		val eight = bySize[7]!!.single()

		// the 5 segment ones
		val three = bySize[5]!!.single { it.containsAll(one) }
		val five = bySize[5]!!.single { it != three && it.intersect(four).size == 3 }
		val two = bySize[5]!!.single { it.intersect(four).size == 2 }

		// the 6 segment ones
		val zero = bySize[6]!!.single { ! it.containsAll(five) }
		val six = bySize[6]!!.single { ! it.containsAll(one) }
		val nine = bySize[6]!!.single { it.containsAll(seven) && it.containsAll(four) }

		// now decode the output
		return output.joinToString("") {
			when (it) {
				zero -> "0"
				one -> "1"
				two -> "2"
				three -> "3"
				four -> "4"
				five -> "5"
				six -> "6"
				seven -> "7"
				eight -> "8"
				nine -> "9"
				else -> error("Not a valid digit")
			}
		}.toInt()
	}
}
