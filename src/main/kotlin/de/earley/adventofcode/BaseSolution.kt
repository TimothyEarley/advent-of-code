package de.earley.adventofcode

import kotlin.time.TimedValue
import kotlin.time.measureTimedValue

abstract class BaseSolution<In, Out1, Out2> {

	fun start() {
		val day = this::class.java.simpleName.takeLastWhile(Char::isDigit).toInt()

		val data = readResource("input.txt").useLines(this::parseInput)
		val one = measureTimedValue { partOne(data) }
		val two = measureTimedValue { partTwo(data) }

		printResult(one, two, day)
	}

	private fun printResult(one: TimedValue<Out1>, two: TimedValue<Out2>, day: Int) {
		val widthResult = maxOf(one.value.toString().length, two.value.toString().length)
		val widthTime = maxOf(one.duration.toString().length, two.duration.toString().length)
		val totalWidth = widthResult + widthTime + 14
		val bar = "═".repeat(totalWidth)
		val daySpacesLeft = (totalWidth - 6) / 2
		val daySpaceLeft = " ".repeat(daySpacesLeft)
		val daySpaceRight = " ".repeat(totalWidth - 6 - daySpacesLeft)

		println(
			"""
			╔$bar╗
			║${daySpaceLeft}Day ${day.toString().padStart(2)}$daySpaceRight║
			╠$bar╣
			║ Part 1 ║ ${one.value.toString().padEnd(widthResult)} ║ ${one.duration.toString().padStart(widthTime)} ║
			╠$bar╣
			║ Part 2 ║ ${two.value.toString().padEnd(widthResult)} ║ ${two.duration.toString().padStart(widthTime)} ║
			╚$bar╝
			""".trimIndent()
		)
	}

	abstract fun parseInput(input: Sequence<String>): In
	abstract fun partOne(data: In): Out1
	abstract fun partTwo(data: In): Out2
}
