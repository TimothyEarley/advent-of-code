package de.earley.adventofcode2024.day7

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day7.start()

object Day7 : BaseSolution<List<Day7.Equation>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Equation> = input.mapToList {
		it.split(": ", limit = 2).let { (result, args) ->
			Equation(result.toLong(), args.split(" ").map(String::toLong))
		}
	}

	data class Equation(
		val result: Long,
		val arguments: List<Long>
	)

	override fun partOne(data: List<Equation>): Long = data
		.filter { it.hasSolution(false) }
		.sumOf(Equation::result)

	override fun partTwo(data: List<Equation>): Long = data
		.filter { it.hasSolution(true) }
		.sumOf(Equation::result)

	private fun Equation.hasSolution(withPipe: Boolean): Boolean =
		Equation(result, arguments.drop(1))
			.hasSolution(arguments.first(), withPipe)

	private fun Equation.hasSolution(current : Long, withPipe: Boolean): Boolean {
		return if (arguments.isEmpty()) {
			current == result
		} else if (current > result) {
			// all numbers are positive and increase the result, so bail if exceeded
			return false
		} else {
			val next = copy(arguments = arguments.drop(1))

			next.hasSolution(current + arguments.first(), withPipe) ||
				next.hasSolution(current * arguments.first(), withPipe) ||
				(withPipe && next.hasSolution("${current}${arguments.first()}".toLong(), withPipe))
		}
	}

}
