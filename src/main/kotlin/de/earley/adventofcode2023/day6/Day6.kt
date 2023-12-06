package de.earley.adventofcode2023.day6

import de.earley.adventofcode.BaseSolution

fun main() = Day6.start()

object Day6 : BaseSolution<List<Day6.Race>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Race> = input.toList().let { (time, dist) ->
		val times = time.removePrefix("Time:").trim().split(" +".toRegex()).map { it.trim().toLong() }
		val distances = dist.removePrefix("Distance:").trim().split(" +".toRegex()).map { it.trim().toLong() }

		times.zip(distances).map { (t, d) -> Race(t, d) }
	}

	data class Race(val time: Long, val distance: Long)

	override fun partOne(data: List<Race>): Long = data.map { it.waysToWin() }.reduce(Long::times)

	private tailrec fun Race.waysToWin(speed: Int = 0, waysToWin: Long = 0): Long {
		if (time <= 0) return waysToWin
		// we still have time left, two options: go now or keep accelerating
		val goNowDistance = time * speed
		val goNowWin = if (goNowDistance > distance) 1 else 0
		return this.copy(time = time - 1).waysToWin(speed + 1, waysToWin + goNowWin)
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
