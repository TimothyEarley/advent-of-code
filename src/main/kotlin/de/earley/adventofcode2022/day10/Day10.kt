package de.earley.adventofcode2022.day10

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList
import kotlin.math.abs

fun main() = Day10.start()

object Day10 : BaseSolution<List<Day10.Instruction>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Instruction> = input.mapToList {
		when {
			it == "noop" -> Instruction.Noop
			it.startsWith("addx") -> Instruction.Addx(it.split(" ", limit = 2)[1].toInt())
			else -> error("Unknown instruction $it")
		}
	}

	override fun partOne(data: List<Instruction>): Int = runAndAccumulate(data, Int::plus, 0) { state ->
		(state.cycle) * if ((state.cycle - 20) % 40 == 0) state.x else 0

	}

	override fun partTwo(data: List<Instruction>): Int = runAndAccumulate(data, String::plus, "") { state ->
		val pixel = if (abs((state.cycle - 1) % 40 - state.x) <= 1) "#" else "."
		val newline = if (state.cycle % 40 == 0) "\n" else ""
		pixel + newline
	}.let {
		println(it)
		0
	}

	private fun <T> runAndAccumulate(
		data: List<Instruction>,
		plus: T.(T) -> T,
		stateStart: T,
		addCycle: (State<T>) -> T
	): T =
		data.fold(State(stateStart, 1, 1)) { state, instruction ->
			val newTotal = state.total.plus(addCycle(state))
			when (instruction) {
				is Instruction.Addx -> {
					// do two cycles
					val newNewTotal = newTotal.plus(addCycle(state.copy(total = newTotal, cycle = state.cycle + 1)))
					State(newNewTotal, state.cycle + 2, state.x + instruction.v)
				}

				Instruction.Noop -> {
					State(newTotal, state.cycle + 1, state.x)
				}
			}
		}.total

	data class State<T>(val total: T, val cycle: Int, val x: Int)

	sealed interface Instruction {
		object Noop : Instruction
		data class Addx(val v: Int) : Instruction
	}

}
