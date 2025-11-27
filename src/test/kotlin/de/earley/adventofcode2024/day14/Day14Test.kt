package de.earley.adventofcode2024.day14

import de.earley.adventofcode.readResource
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day14Test : WordSpec({
	"Day14" should
		{
			val uut1 = Day14(11, 7)
			val t = uut1.readResource("testInput.txt").useLines(uut1::parseInput)

			"solve part one" {
				uut1.partOne(t) shouldBe 12
			}

			val uut2 = Day14(101, 103)
			val t2 = uut2.readResource("input.txt").useLines(uut2::parseInput)
			"solve part two" {
				uut2.partTwo(t2) shouldBe 6587
			}
		}
})
