package de.earley.adventofcode2021.day22

import de.earley.adventofcode2021.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day22Test : WordSpec({
	include(testDay(Day22, 474140, 2758514936282235))

	"overlap" should {
		"be valid" {
			Region(
				x = 10..20,
				y = 30..40,
				z = 50..60,
			).overlap(
				Region(
					x = 0..100,
					y = 0..35,
					z = 55..61
				)
			) shouldBe Region(
				x = 10..20,
				y = 30..35,
				z = 55..60
			)
		}
	}
})
