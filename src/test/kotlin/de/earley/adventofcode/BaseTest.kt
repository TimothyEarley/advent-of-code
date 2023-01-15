package de.earley.adventofcode

import de.earley.adventofcode2021.readResource
import io.kotest.core.spec.style.wordSpec
import io.kotest.matchers.shouldBe

fun <In, Out1, Out2> testDay(uut: BaseSolution<In, Out1, Out2>, expectedOne: Out1, expectedTwo: Out2) = wordSpec {
	uut.javaClass.simpleName should
		{
			val t = uut.readResource("testInput.txt").useLines(uut::parseInput)

			"solve part one" {
				uut.partOne(t) shouldBe expectedOne
			}

			"solve part two" {
				uut.partTwo(t) shouldBe expectedTwo
			}
		}
}
