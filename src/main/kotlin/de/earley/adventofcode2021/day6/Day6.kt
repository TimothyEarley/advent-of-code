package de.earley.adventofcode2021.day6

import de.earley.adventofcode2021.BaseSolution

const val startingCycleTime = 6 // 7 days

fun main() = Day6.start()

object Day6 : BaseSolution<List<Int>, Long>() {

	override fun parseInput(input: Sequence<String>): List<Int> = input.flatMap {
		it.split(',').map(String::toInt)
	}.toList()

	override fun partOne(data: List<Int>): Long = simulate(data, 80)

	override fun partTwo(data: List<Int>): Long = simulate(data, 256)

	private fun simulate(data: List<Int>, simulationTime: Int): Long {
		val arraySize = startingCycleTime + 2 + 1

		// instead of keeping track of the fish individually, keep track of how many
		// there are in each state of the countdown
		var fish = LongArray(arraySize) { 0 }

		for (d in data) {
			fish[d]++
		}

		repeat(simulationTime) {
			val newFish = LongArray(arraySize) { 0 }
			for (i in fish.indices) {
				val count = fish[i]
				if (i == 0) {
					// the old fish reset
					newFish[startingCycleTime] += count
					// and spawn new fish (slower first cycle)
					newFish[startingCycleTime + 2] += count
				} else {
					// decrease countdown
					newFish[i - 1] += count
				}
			}
			fish = newFish
		}

		return fish.sum()
	}
}
