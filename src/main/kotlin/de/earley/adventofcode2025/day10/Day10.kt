package de.earley.adventofcode2025.day10

import com.microsoft.z3.Context
import com.microsoft.z3.IntNum
import com.microsoft.z3.Status
import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.eq
import de.earley.adventofcode.ge
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.int
import de.earley.adventofcode.mapToList

fun main() = Day10.start()

object Day10 : BaseSolution<List<Day10.Instruction>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Instruction> = input.mapToList { line ->
		Instruction(
			target = line.substring(line.indexOf('[') + 1, line.indexOf(']')).map {
				when (it) {
					'.' -> false
					'#' -> true
					else -> error("Invalid input")
				}
			},
			buttons = line.withIndex()
				.filter { it.value == '(' }
				.map { line.substring(it.index + 1, line.indexOf(')', startIndex = it.index + 1)) }
				.map { it.split(',').map { it.toInt() }.toSet() },
			joltages = line.substring(line.indexOf('{') + 1, line.indexOf('}'))
				.split(',').map { it.toInt() }
		)
	}

	data class Instruction(
		val target: List<Boolean>,
		val buttons: List<Set<Int>>,
		val joltages: List<Int>
	)


	override fun partOne(data: List<Instruction>): Long = data.sumOf { instruction ->
		generalAStar(
			from = instruction.target.map { false },
			goal = { state ->
				state.withIndex().all { it.value == instruction.target[it.index] }
			},
			heuristic = { 1 },
			neighbours = {
				instruction.buttons.asSequence()
					.map { button ->
						mapIndexed { i, light -> (if ((i) in button) !light else light) } to 1
					}
			}
		)!!.toLong()
	}

	override fun partTwo(data: List<Instruction>): Long = data.sumOf { instruction ->
		with (Context()) {
			val buttonPushes = instruction.buttons.map { mkIntConst(it.toString()) }

			val optimize = mkOptimize()
			val adds = mkAdd(*buttonPushes.toTypedArray())
			optimize.MkMinimize(adds)

			buttonPushes.forEach {
				optimize.Add(it ge 0.int)
			}

			instruction.joltages.forEachIndexed { i, joltage ->
				val relevantButtons = instruction.buttons
					.withIndex().filter { i in it.value }
					.map { buttonPushes[it.index] }
					.toTypedArray()
				optimize.Add(mkAdd(*relevantButtons) eq joltage.int)
			}

			when (optimize.Check()) {
				Status.UNSATISFIABLE -> error("UNSAT")
				null, Status.UNKNOWN -> error("UNKNOWN")
				Status.SATISFIABLE -> {
					(optimize.model.eval(adds, false) as IntNum).int64
				}
			}
		}
	}
}