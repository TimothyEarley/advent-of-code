package de.earley.adventofcode2025.day1

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList
import kotlin.math.abs

fun main() = Day1.start()

object Day1 : BaseSolution<List<Day1.Instruction>, Int, Int>() {

	enum class Direction {
		Left, Right
	}

	data class Instruction(val direction: Direction, val value: Int) {
		val directedValue = when (direction) {
			Direction.Left -> -value
			Direction.Right -> value
		}
	}

	override fun parseInput(input: Sequence<String>): List<Instruction> = input.mapToList {
		val dir = when (it.first()) {
			'L' -> Direction.Left
			'R' -> Direction.Right
			else -> error("Invalid input")
		}
		val value = it.drop(1).toInt()
		Instruction(dir, value)
	}

	override fun partOne(data: List<Instruction>): Int = data.runningFold(50) { state, i ->
		(state + i.directedValue).mod(100)
	}.count { it == 0 }

	override fun partTwo(data: List<Instruction>): Int = data.fold(0 to 50) { (count, state), i ->
		val addedValue = state + i.directedValue
		val newState = addedValue.mod(100)
		// did we land exactly on zero?
		val exactlyZero = if (addedValue == 0) 1 else 0
		// how much higher/lower than 100 were we? -> how often we crossed zero
		val crosses = abs(addedValue.div(100))
		// except we might have missed the first under zero as -50/100 = 0
		val firstUnderZero = if (addedValue < 0 && state != 0) 1 else 0
		val timesZero = exactlyZero + firstUnderZero + crosses
		(count + timesZero) to newState
	}.first

}
