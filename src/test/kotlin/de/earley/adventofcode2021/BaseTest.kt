package de.earley.adventofcode2021

import io.kotest.core.spec.style.wordSpec
import io.kotest.matchers.shouldBe

fun <T> testDay(uut: BaseSolution<T>, expectedOne: Int, expectedTwo: Int) = wordSpec {
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
