package de.earley.adventofcode2023.day22

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point3
import de.earley.adventofcode.mapToList
import kotlin.math.max
import kotlin.math.min

fun main() = Day22.start()

private typealias Name = Char

object Day22 : BaseSolution<List<Day22.Brick>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Brick> = input.withIndex()
		.mapToList { (i, s) ->
			val (a, b) = s.split("~")
			Brick(i, Point3.parse(a), Point3.parse(b))
		}

	data class Brick(
		val id: Int,
		val a: Point3,
		val b: Point3,
	)

	data class BrickStructure(
		val name: Name,
		val supporting: List<Name>,
		val supportedBy: List<Name>,
		val grounded: Boolean,
	)

	override fun partOne(data: List<Brick>): Int = settleBricks(data).values.count {
		it.supporting.isEmpty()
	}

	private fun settleBricks(data: List<Brick>): Map<Name, BrickStructure> {
		class MutableBrick(
			val name: Name,
			var a: Point3,
			var b: Point3,
			var layingOn: List<Name>,
			var grounded: Boolean,
		)

		val sorted = data
			.map { MutableBrick((it.id + 'A'.code).toChar(), it.a, it.b, mutableListOf(), false) }
			.sortedBy { min(it.a.z, it.b.z) }

		do {
			var activeFalling = false
			for (brick in sorted) {
				val zBelow = min(brick.a.z, brick.b.z) - 1
				if (zBelow <= 0) {
					// we are on the ground
					brick.grounded = true
					continue
				}
				// check other bricks
				val bricksToLandOn = sorted.filter { other ->
					other != brick &&
						max(other.a.z, other.b.z) == zBelow &&
						other.a.x..other.b.x intersects brick.a.x..brick.b.x &&
						other.a.y..other.b.y intersects brick.a.y..brick.b.y
				}
				if (bricksToLandOn.isNotEmpty()) {
					brick.layingOn = bricksToLandOn.map { it.name }
					continue // we are on another brick
				}

				// we can fall
				activeFalling = true
				brick.a += Point3(0, 0, -1)
				brick.b += Point3(0, 0, -1)
			}
		} while (activeFalling)

		return sorted.map { brick ->
			BrickStructure(
				brick.name,
				sorted.filter { brick.name in it.layingOn }.map { it.name },
				brick.layingOn,
				brick.grounded
			)
		}.associateBy { it.name }
	}

	override fun partTwo(data: List<Brick>): Int {
		val settled = settleBricks(data)

		return settled.values.sumOf { brick ->
			// how many would fall?
			val candidates = brick.supporting.map { settled[it]!! }.toMutableSet()
			val wouldFall = mutableSetOf(brick.name)
			do {
				var keepGoing = false
				val canFall = candidates.filter {
					!it.grounded && (it.supportedBy - wouldFall).isEmpty()
				}
				if (canFall.isNotEmpty()) {
					keepGoing = true
					candidates += canFall.flatMap { b -> b.supporting.map { settled[it]!! } }
					wouldFall += canFall.map { it.name }
					candidates -= wouldFall.map { settled[it]!! }.toSet()
				}
			} while (keepGoing)

			wouldFall.size - 1
		}
	}
}

private infix fun IntRange.intersects(other: IntRange): Boolean =
	first <= other.last && last >= other.first
