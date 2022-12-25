package de.earley.adventofcode2022.day25

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class Day25Test : WordSpec({
	include(testDay(Day25, "2=-1=0", 0))

	"toSNAFU" should {
		"test data" {
			with(Day25) {
				1L.toSNAFU() shouldBe "1"
				2L.toSNAFU() shouldBe "2"
				3L.toSNAFU() shouldBe "1="
				4L.toSNAFU() shouldBe "1-"
				5L.toSNAFU() shouldBe "10"
				6L.toSNAFU() shouldBe "11"
				7L.toSNAFU() shouldBe "12"
				8L.toSNAFU() shouldBe "2="
				9L.toSNAFU() shouldBe "2-"
				10L.toSNAFU() shouldBe "20"
				15L.toSNAFU() shouldBe "1=0"
				20L.toSNAFU() shouldBe "1-0"
				2022L.toSNAFU() shouldBe "1=11-2"
				12345L.toSNAFU() shouldBe "1-0---0"
				314159265L.toSNAFU() shouldBe "1121-1110-1=0"
			}
		}
	}
})
