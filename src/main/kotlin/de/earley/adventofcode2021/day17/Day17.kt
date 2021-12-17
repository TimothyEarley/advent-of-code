package de.earley.adventofcode2021.day17

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Point
import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() = Day17.start()

/**
 * Assumes the target is in the bottom right section
 */
object Day17 : BaseSolution<TargetArea, Int>() {

	override fun parseInput(input: Sequence<String>): TargetArea =
		input.single().removePrefix("target area: ").split(", ").let { (x, y) ->
			val (lowerX, higherX) = x.removePrefix("x=").split("..").map(String::toInt)
			val (lowerY, higherY) = y.removePrefix("y=").split("..").map(String::toInt)
			TargetArea(
				Point(minOf(lowerX, higherX), maxOf(lowerY, higherY)),
				Point(maxOf(lowerX, higherX), minOf(lowerY, higherY))
			)
		}

	override fun partOne(data: TargetArea): Int {
		// so the speed at the target can be max the target height
		// if we go up the speed when we reach y = 0 again is the
		// original speed. If we take 1 step from there to the target
		// we cannot overshoot it, so "originalSpeed < distance to bottom of target + 1"

		val maxSpeed = data.bottomRight.y.absoluteValue + 1
		val maxX = data.bottomRight.x + 1
		val start = Point(0, 0)

		return (0..maxSpeed).maxOf { y ->
			(0..maxX).maxOf { x ->
				simulateShot(start, Point(x, y), data) ?: -1
			}
		}
	}

	override fun partTwo(data: TargetArea): Int {
		// same as 1, but we also consider the negative y starting directions
		val maxSpeed = data.bottomRight.y.absoluteValue + 1
		val maxX = data.bottomRight.x + 1
		val start = Point(0, 0)

		return (-maxSpeed..maxSpeed).sumOf { y ->
			(0..maxX).count { x ->
				simulateShot(start, Point(x, y), data) != null
			}
		}
	}

	/**
	 * @return max y reached or null if not hit
	 */
	private tailrec fun simulateShot(position: Point, direction: Point, targetArea: TargetArea, maxY: Int = position.y): Int? {
		if (position.y < targetArea.bottomRight.y) return null
		if (position in targetArea) return maxY
		val newPosition = position + direction
		return simulateShot(
			newPosition,
			direction + Point(-direction.x.sign, -1),
			targetArea,
			maxOf(newPosition.y, maxY)
		)
	}
}

data class TargetArea(
	val topLeft: Point, val bottomRight: Point
) {
	operator fun contains(position: Point): Boolean =
		position.x in topLeft.x..bottomRight.x && position.y in bottomRight.y..topLeft.y
}
