package de.earley.adventofcode

import io.kotest.core.spec.style.wordSpec
import io.kotest.matchers.shouldBe

fun <In, Out1, Out2> testDay(uut: BaseSolution<In, Out1, Out2>, expectedOne: Out1, expectedTwo: Out2, separateInput: Boolean = false) = wordSpec {
	uut.javaClass.simpleName should
		{
			val t = uut.readResource("testInput.txt").useLines(uut::parseInput)

			"solve part one" {
				uut.partOne(t) shouldBe expectedOne
			}

			val t2 = if (separateInput) uut.readResource("testInput2.txt").useLines(uut::parseInput) else t
			"solve part two" {
				uut.partTwo(t2) shouldBe expectedTwo
			}
		}
}
