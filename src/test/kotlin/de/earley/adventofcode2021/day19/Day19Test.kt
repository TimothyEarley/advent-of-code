package de.earley.adventofcode2021.day19

import de.earley.adventofcode2021.Point3
import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldBeIn

class Day19Test : WordSpec({
	include(testDay(Day19, 79, 3621))

	"configurations" should {

		val scanner = Scanner(
			-1,
			listOf(
				Point3(-1, -1, 1),
				Point3(-2, -2, 2),
				Point3(-3, -3, 3),
				Point3(-2, -3, 1),
				Point3(5, 6, -4),
				Point3(8, 0, 7),
			)
		)

		"include" {

			val options = orientations.map { o ->
				scanner.beacons.map { o(it) }.toSet()
			}

			setOf(
				Point3(1, -1, 1),
				Point3(2, -2, 2),
				Point3(3, -3, 3),
				Point3(2, -1, 3),
				Point3(-5, 4, -6),
				Point3(-8, -7, 0),
			) shouldBeIn options

			setOf(
				Point3(-1, -1, -1),
				Point3(-2, -2, -2),
				Point3(-3, -3, -3),
				Point3(-1, -3, -2),
				Point3(4, 6, 5),
				Point3(-7, 0, 8),
			) shouldBeIn options

			setOf(
				Point3(1, 1, -1),
				Point3(2, 2, -2),
				Point3(3, 3, -3),
				Point3(1, 3, -2),
				Point3(-4, -6, 5),
				Point3(7, 0, 8),
			) shouldBeIn options

			setOf(
				Point3(1, 1, 1),
				Point3(2, 2, 2),
				Point3(3, 3, 3),
				Point3(3, 1, 2),
				Point3(-6, -4, -5),
				Point3(0, 7, -8),
			) shouldBeIn options
		}
	}
})
