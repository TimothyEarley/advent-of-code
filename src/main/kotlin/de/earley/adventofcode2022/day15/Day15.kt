package de.earley.adventofcode2022.day15

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.manhattanLength
import de.earley.adventofcode2021.mapToList
import kotlin.math.abs

fun main() = Day15(4000000).start()

class Day15(private val maxRange: Int) : BaseSolution<List<Day15.BeaconInfo>, Int, Long>() {

	private val regex = "Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)".toRegex()
	override fun parseInput(input: Sequence<String>): List<BeaconInfo> = input.mapToList {
		val (bx, by, dx, dy) = regex.matchEntire(it)?.destructured ?: error(it)
		BeaconInfo(Point(bx.toInt(), by.toInt()), Point(dx.toInt(), dy.toInt()))
	}

	override fun partOne(data: List<BeaconInfo>): Int {
		val startX = data.minOf { it.sensor.x - it.beacon.x }
		val endX = data.maxOf { it.sensor.x + it.beacon.x }
		val y = maxRange / 2

		return (startX..endX).count { x ->
			val p = Point(x, y)
			(!data.none { sensor ->
				val distance = sensor.sensor - p
				distance.manhattanLength() <= sensor.beaconDistance  && p != sensor.beacon
			})
		}
	}

	/**
	 * Basic idea: Scan along all x and y to find a spot not covered by any sensor
	 * Problem: 4000000^2 are too many checks
	 * Idea: Skip along the x axis to quickly get out of range of the sensor block us
	 * Calculation: If we are in a sensor exclusion zone, pick the sensor with the
	 * largest exclusion zone radius. Then we have the following, where the beacon (S) is at the center
	 * and we (*) are somewhere inside the radius
	 *
	 *         ∧
	 *        / \
	 *       /   \
	 *      / *   \$
	 *     /       \
	 *    <    S    >
	 *     \       /
	 *      \     /
	 *       \   /
	 *        \ /
	 *         v
	 *
	 * We want to find x position "$" to get outside the zone, so the calculation is
	 * (where r is the exclusion radius):
	 * | $.x - S.x | + | $.y - $.y | = r and $.y = *.y and $.x > S.x, so
	 * $.x = r + S.x - |*.y - S.y|
	 */
	override fun partTwo(data: List<BeaconInfo>): Long {
		for (y in 0 .. maxRange) {
			var x = 0
			while (x <= maxRange) {
				val p = Point(x, y)
				// find the closest sensor
				val s = data
					.filter { it.sensor.manhattanDistanceTo(p) <= it.beaconDistance }
					.maxByOrNull { it.sensor.manhattanDistanceTo(p) }
					?:  return x * 4000000L + y // found it!
				// skip along
				val nx = s.beaconDistance + s.sensor.x - abs(y - s.sensor.y)
				x = nx + 1
			}
		}

		error("No beacon found")
	}

	data class BeaconInfo(
		val sensor: Point, val beacon: Point
	) {
		val beaconDistance = sensor.manhattanDistanceTo(beacon)
	}

}
