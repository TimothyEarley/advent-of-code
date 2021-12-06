package de.earley.adventofcode2021.day2

import de.earley.adventofcode2021.BaseSolution

fun main() = Day2.start()

object Day2 : BaseSolution<List<Day2.Command>, Int>() {

	enum class Direction {
		FORWARD, UP, DOWN
	}

	data class Command(val direction: Direction, val amount: Int)

	override fun parseInput(input: Sequence<String>): List<Command> = input
		.map {
			val (d, a) = it.split(' ', limit = 2)
			val dir = when (d) {
				"forward" -> Direction.FORWARD
				"up" -> Direction.UP
				"down" -> Direction.DOWN
				else -> error("Unkown direction $d")
			}
			Command(dir, a.toInt())
		}
		.toList()

	override fun partOne(data: List<Command>): Int = data.fold(0 to 0) { (x, y), command ->
		when (command.direction) {
			Direction.FORWARD -> x + command.amount to y
			Direction.UP -> x to y - command.amount
			Direction.DOWN -> x to y + command.amount
		}
	}.let { it.first * it.second }

	data class State(val horizontal: Int, val depth: Int, val aim: Int)

	override fun partTwo(data: List<Command>): Int = data.fold(State(0, 0, 0)) { s, command ->
		when (command.direction) {
			Direction.FORWARD -> s.copy(horizontal = s.horizontal + command.amount, depth = s.depth + s.aim * command.amount)
			Direction.UP -> s.copy(aim = s.aim - command.amount)
			Direction.DOWN -> s.copy(aim = s.aim + command.amount)
		}
	}.let { it.horizontal * it.depth }
}
