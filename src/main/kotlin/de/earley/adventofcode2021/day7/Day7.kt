package de.earley.adventofcode2021.day7

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.cache
import kotlin.math.abs
import kotlin.math.roundToInt

fun main() = Day7.start()

object Day7 : BaseSolution<List<Int>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Int> =
		input.flatMap { it.split(',').map(String::toInt) }.toList()

	override fun partOne(data: List<Int>): Int = findBest(data, ::totalDistance)

	override fun partTwo(data: List<Int>): Int = findBest(data, ::totalDistance2)

	private fun findBest(data: List<Int>, f: (List<Int>, Int) -> Int): Int {
		val avg = data.average().roundToInt()
		val cachedF = { i: Int -> f(data, i) }.cache()
		return findMin(avg, cachedF)
	}

	tailrec fun findMin(point: Int, cost: (Int) -> Int): Int {
		// assume the function has exactly one minimum

		val costThis = cost(point)
		return when {
			cost(point - 1) < costThis -> findMin(point - 1, cost)
			cost(point + 1) < costThis -> findMin(point + 1, cost)
			else -> costThis
		}
	}

	fun totalDistance(data: List<Int>, target: Int) = data.sumOf { abs(it - target) }
	fun totalDistance2(data: List<Int>, target: Int) = data.sumOf {
		val a = abs(it - target)
		// 1 + 2 + 3 + ... + a = a * (a + 1) / 2
		(a * (a + 1)) / 2
	}
}
