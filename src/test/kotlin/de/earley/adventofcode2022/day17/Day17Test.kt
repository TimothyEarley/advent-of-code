package de.earley.adventofcode2022.day17

import de.earley.adventofcode.readResource
import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day17Test : WordSpec({
	include(testDay(Day17, 3068, 1514285714288L))
	"Day 17 real" should {
		val uut = Day17
		val t = uut.readResource("input.txt").useLines(uut::parseInput)

		"solve part one" {
			uut.partOne(t) shouldBe 3085L
		}

		"solve part two" {
			uut.partTwo(t) shouldBe 1535483870924L
		}
	}
})
