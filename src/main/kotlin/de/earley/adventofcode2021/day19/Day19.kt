package de.earley.adventofcode2021.day19

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Grid
import de.earley.adventofcode2021.Point3
import de.earley.adventofcode2021.axis
import de.earley.adventofcode2021.grid
import de.earley.adventofcode2021.manhattanTo
import de.earley.adventofcode2021.split

fun main() = Day19.start()

object Day19 : BaseSolution<List<Scanner>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Scanner> =
		input.toList()
			.split { it.isBlank() }
			.mapIndexed { i, it ->
				Scanner(it.drop(1).map(Point3.Companion::parse).toSet())
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
		val baseFixed = PositionedScanner(Point3(0, 0, 0), base.beacons)

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
		for (c in Configuration.all()) {
			val adjusted = other.adjust(c)

			// try to match a point in base to one in adjusted
			for (pThis in adjusted.beacons) {
				for (pBase in base.beacons) {
					val scannerRelativeToBase = pBase - pThis

					val transformed = adjusted.beacons.map { it + scannerRelativeToBase }.toSet()

					val overlap = transformed.count { it in base.beacons }
					if (overlap >= 12) {
						return PositionedScanner(scannerRelativeToBase, transformed)
					}
				}
			}
		}

		return null
	}
}

private operator fun Grid<Int>.times(p: Point3): Point3 =
	Point3(
		x = get(0, 0)!! * p.x + get(1, 0)!! * p.y + get(2, 0)!! * p.z,
		y = get(0, 1)!! * p.x + get(1, 1)!! * p.y + get(2, 1)!! * p.z,
		z = get(0, 2)!! * p.x + get(1, 2)!! * p.y + get(2, 2)!! * p.z
	)

class Scanner(val beacons: Set<Point3>)

class PositionedScanner(
	val position: Point3,
	val beacons: Set<Point3>
)

data class Configuration(
	val x: Point3,
	val y: Point3,
	val z: Point3
) {
	companion object {

		private val configs: List<Configuration> by lazy {
			Point3.units().flatMap { x ->
				Point3.units().flatMap { y ->
					if (x.axis() == y.axis()) emptySequence()
					else Point3.units().mapNotNull { z ->
						if (x.axis() == z.axis() || y.axis() == z.axis()) null
						else Configuration(x, y, z)
					}
				}
			}.toList()
		}

		// contains 48 instead of the required 24
		fun all(): Sequence<Configuration> = configs.asSequence()
	}
}

fun Scanner.adjust(c: Configuration): Scanner {
	val m = c.basisChangeMatrix()
	return Scanner(
		beacons.map {
			m * it
		}.toSet()
	)
}

private fun Configuration.basisChangeMatrix(): Grid<Int> = grid(3, 3) { p ->
	val vec = listOf(x, y, z)[p.x]
	when (p.y) {
		0 -> vec.x
		1 -> vec.y
		2 -> vec.z
		else -> error("")
	}
}
