package de.earley.adventofcode

import de.earley.adventofcode2021.readResource

abstract class BaseSolution<In, Out1, Out2> {

	fun start() {
		val day = this::class.java.simpleName.takeLastWhile(Char::isDigit).toInt()

		val data = readResource("input.txt").useLines(this::parseInput)
		val one = partOne(data)
		val two = partTwo(data)

		printResult(one, two, day)
	}

	private fun printResult(one: Out1, two: Out2, day: Int) {
		val width = maxOf(one.toString().length, two.toString().length)
		val totalWidth = width + 11
		val bar = "═".repeat(totalWidth)
		val daySpacesLeft = (totalWidth - 6) / 2
		val daySpaceLeft = " ".repeat(daySpacesLeft)
		val daySpaceRight = " ".repeat(totalWidth - 6 - daySpacesLeft)

		println(
			"""
			╔$bar╗
			║${daySpaceLeft}Day ${day.toString().padStart(2)}$daySpaceRight║
			╠$bar╣
			║ Part 1 ║ ${one.toString().padEnd(width)} ║
			╠$bar╣
			║ Part 2 ║ ${two.toString().padEnd(width)} ║
			╚$bar╝
			""".trimIndent()
		)
	}

	abstract fun parseInput(input: Sequence<String>): In
	abstract fun partOne(data: In): Out1
	abstract fun partTwo(data: In): Out2
}