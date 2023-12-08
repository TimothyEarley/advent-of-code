package de.earley.adventofcode2023.day6

import de.earley.adventofcode.BaseSolution
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

fun main() = Day6.start()

object Day6 : BaseSolution<List<Day6.Race>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Race> = input.toList().let { (time, dist) ->
		val times = time.removePrefix("Time:").trim().split(" +".toRegex()).map { it.toLong() }
		val distances = dist.removePrefix("Distance:").trim().split(" +".toRegex()).map { it.toLong() }

		times.zip(distances).map { (t, d) -> Race(t, d) }
	}

	data class Race(val time: Long, val distance: Long)

	override fun partOne(data: List<Race>): Long = data.map { it.waysToWin() }.reduce(Long::times)

	private fun Race.waysToWin(): Long {
		// myDist = (time - x) * x = - x^2 + time * x
		// myDist =? distance
		// ==> x^2 - time * x + distance = 0
		// x₁,₂ = time/2 ± sqrt( time^2 / 4 - distance )
		// and then a bit of fiddling with rounding
		val s = sqrt(time.toDouble().pow(2) / 4.0 - distance)
		val x1 = floor(time / 2.0 - s)
		val x2 = ceil(time / 2.0 + s - 1)
		return (x2 - x1).toLong()
	}

	override fun partTwo(data: List<Race>): Long {
		val race = data.fold(Race(0, 0)) { acc, race ->
			val time = ("${acc.time}${race.time}").toLong()
			val distance = ("${acc.distance}${race.distance}").toLong()
			Race(time, distance)
		}
		return race.waysToWin()
	}
}
