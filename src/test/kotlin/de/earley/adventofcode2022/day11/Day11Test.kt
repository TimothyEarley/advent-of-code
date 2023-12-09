package de.earley.adventofcode2022.day11

import de.earley.adventofcode.lcm
import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day11Test : WordSpec({
	include(testDay(Day11, 10605, 2713310158))

	"lcm" should {
		lcm(4, 6) shouldBe 12
		lcm(5, 13) shouldBe 65
		lcm(100, 70) shouldBe 700
	}
})
