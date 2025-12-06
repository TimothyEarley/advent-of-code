package de.earley.adventofcode2025.day6

import de.earley.adventofcode.BaseSolution

fun main() = Day6.start()

object Day6 : BaseSolution<List<Day6.Problem>, Long, Long>() {

	data class Problem(val numbers: List<String>, val op: Operation)
	data class ParsedProblem(val numbers: List<Long>, val op: Operation)

	enum class Operation(val realized: (Long, Long) -> Long) {
		Plus(Long::plus), Times(Long::times)
	}

	override fun parseInput(input: Sequence<String>): List<Problem> = input.toList().let { list ->
		val numberRows = list.dropLast(1)
		val maxRowSize = numberRows.maxOf { it.length }
		val ops = list.last()

		val columnRanges = ops.indices
			.filter { ops[it] != ' ' }
			.plus(maxRowSize + 1)
			.zipWithNext()
			.map { it.first until it.second - 1 }

		columnRanges.map { range ->
			Problem(
				numbers = numberRows
					.map { it.padEnd(maxRowSize, ' ').substring(range) },
				op = when (ops[range.first]) {
					'+' -> Operation.Plus
					'*' -> Operation.Times
					else -> error("Unexpected operation")
				}
			)
		}
	}

	private fun List<ParsedProblem>.solve(): Long = sumOf { problem ->
		problem.numbers.reduce(problem.op.realized)
	}

	override fun partOne(data: List<Problem>): Long =
		data.map { problem -> ParsedProblem(problem.numbers.map { it.trim().toLong() }, problem.op) }
			.solve()

	override fun partTwo(data: List<Problem>): Long =
		data.map { problem ->
			val flippedNumbers = (problem.numbers.first().lastIndex downTo 0).map { i ->
				String(problem.numbers.map { it[i] }.toCharArray()).trim().toLong()
			}
			ParsedProblem(flippedNumbers, problem.op)
		}
			.solve()

}
