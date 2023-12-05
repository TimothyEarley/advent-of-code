package de.earley.adventofcode2021.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point3
import de.earley.adventofcode.manhattanTo
import de.earley.adventofcode.split
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
	println("Time: " + measureTime { Day19.start() })
}

object Day19 : BaseSolution<List<Scanner>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Scanner> =
		input.toList()
			.split { it.isBlank() }
			.mapIndexed { i, it ->
				Scanner(i, it.drop(1).map(Point3.Companion::parse))
			}

	override fun partOne(data: List<Scanner>): Int = solvePosition(data).flatMap { it.beacons }.toSet().size

	override fun partTwo(data: List<Scanner>): Int = solvePosition(data).map { it.position }.let { scanners ->
		scanners.maxOf { a ->
			scanners.maxOf { b ->
				a.manhattanTo(b)
			}
		}
	}

	private fun solvePosition(scanners: List<Scanner>): List<PositionedScanner> {
		// fix scanner 0 to be at (0, 0) global with x, y, z facing the correct way
		val base = scanners.first()
		val baseFixed = PositionedScanner(base.id, Point3(0, 0, 0), base.beacons.toSet())

		val solvedScanners = mutableListOf(baseFixed)
		val open = scanners.drop(1).toMutableList()

		while (open.isNotEmpty()) {
			val next = open.removeAt(0)

			val match = solvedScanners.asSequence().mapNotNull {
				findMatch(it, next)
			}.firstOrNull()

			if (match == null) {
				open.add(next)
			} else {
				solvedScanners.add(match)
			}
		}

		return solvedScanners
	}

	private fun findMatch(base: PositionedScanner, other: Scanner): PositionedScanner? {
		for (orientation in orientations) {
			val adjusted = other.beacons.map { orientation(it) }

			// try to match a point in base to one in adjusted
			for (pThis in adjusted) {
				for (pBase in base.beacons) {
					val scannerRelativeToBase = pBase - pThis

					val overlap = adjusted.count { it + scannerRelativeToBase in base.beacons }
					if (overlap >= 12) {
						val transformed = adjusted.mapTo(HashSet(adjusted.size)) {
							it + scannerRelativeToBase
						}
						return PositionedScanner(
							other.id,
							scannerRelativeToBase,
							transformed
						)
					}
				}
			}
		}

		return null
	}
}

class Scanner(
	val id: Int,
	val beacons: List<Point3>,
)

class PositionedScanner(
	val id: Int,
	val position: Point3,
	val beacons: Set<Point3>,
)

typealias Orientation = (Point3) -> Point3

val orientations: List<Orientation> = listOf(
	{ Point3(it.x, it.y, it.z) },
	{ Point3(it.x, it.z, -it.y) },
	{ Point3(it.x, -it.y, -it.z) },
	{ Point3(it.x, -it.z, it.y) },

	{ Point3(-it.x, it.y, -it.z) },
	{ Point3(-it.x, -it.z, -it.y) },
	{ Point3(-it.x, -it.y, it.z) },
	{ Point3(-it.x, it.z, it.y) },

	{ Point3(it.y, -it.x, it.z) },
	{ Point3(it.y, it.z, it.x) },
	{ Point3(it.y, -it.z, -it.x) },
	{ Point3(it.y, it.x, -it.z) },

	{ Point3(-it.y, -it.x, -it.z) },
	{ Point3(-it.y, it.z, -it.x) },
	{ Point3(-it.y, it.x, it.z) },
	{ Point3(-it.y, -it.z, it.x) },

	{ Point3(it.z, it.y, -it.x) },
	{ Point3(it.z, -it.x, -it.y) },
	{ Point3(it.z, -it.y, it.x) },
	{ Point3(it.z, it.x, it.y) },

	{ Point3(-it.z, it.y, it.x) },
	{ Point3(-it.z, -it.x, it.y) },
	{ Point3(-it.z, -it.y, -it.x) },
	{ Point3(-it.z, it.x, -it.y) }
)
