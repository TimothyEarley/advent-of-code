package de.earley.adventofcode

import kotlin.math.abs

data class Point3(
	val x: Int,
	val y: Int,
	val z: Int,
) {
	operator fun minus(other: Point3): Point3 = Point3(
		x - other.x,
		y - other.y,
		z - other.z
	)

	operator fun plus(other: Point3) = Point3(
		x + other.x,
		y + other.y,
		z + other.z
	)

	operator fun rangeTo(to: Point3): Sequence<Point3> = sequence {
		for (rx in x..to.x) {
			for (ry in y..to.y) {
				for (rz in z..to.z) {
					yield(Point3(rx, ry, rz))
				}
			}
		}
	}

	companion object {
		fun parse(s: String): Point3 = s.split(",", limit = 3).map(String::trim).map(String::toInt).let { (x, y, z) ->
			Point3(x, y, z)
		}

		fun units(): Sequence<Point3> = sequence {
			yield(Point3(1, 0, 0))
			yield(Point3(0, 1, 0))
			yield(Point3(0, 0, 1))
			yield(Point3(-1, 0, 0))
			yield(Point3(0, -1, 0))
			yield(Point3(0, 0, -1))
		}
	}
}

fun Point3.manhattanTo(other: Point3): Int = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

data class LongPoint3(
	val x: Long,
	val y: Long,
	val z: Long,
) {
	companion object {
		fun parse(s: String): LongPoint3 =
			s.split(",", limit = 3).map(String::trim).map(String::toLong).let { (x, y, z) ->
				LongPoint3(x, y, z)
			}
	}
}
