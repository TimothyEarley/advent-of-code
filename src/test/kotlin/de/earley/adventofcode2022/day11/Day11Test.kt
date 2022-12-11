package de.earley.adventofcode2022.day11

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day11Test : WordSpec({
	include(testDay(Day11, 10605, 2713310158))

	"lcm" should {
		Day11.lcm(4, 6) shouldBe 12
		Day11.lcm(5, 13) shouldBe 65
		Day11.lcm(100, 70) shouldBe 700
	}
})
