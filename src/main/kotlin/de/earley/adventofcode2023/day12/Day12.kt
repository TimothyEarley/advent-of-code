package de.earley.adventofcode2023.day12

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day12.start()

object Day12 : BaseSolution<List<Day12.Line>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Line> = input.mapToList { line ->
		val (states, groups) = line.split(" ", limit = 2)
		Line(
			states = states.map {
				when (it) {
					'.' -> SpringState.Operational
					'#' -> SpringState.Damaged
					'?' -> SpringState.Unknown
					else -> error("Unknown $it")
				}
			},
			groups = groups.split(",").map { it.toInt() }
		)
	}

	data class Line(
		val states: List<SpringState>,
		val groups: List<Int>,
	)

	enum class SpringState {
		Operational, Damaged, Unknown
	}

	private fun Line.nextDamaged() = Line(states.drop(1), listOf(groups[0] - 1) + groups.drop(1))
	private fun Line.nextOperational() = Line(states.drop(1), groups)
	private fun Line.nextOperationalNewGroup() = Line(states.drop(1), groups.drop(1))

	override fun partOne(data: List<Line>): Long {
		return data.sumOf { it.possibleConfigurations(false) }
	}

	private val cache = mutableMapOf<Pair<Line, Boolean>, Long>()
	private fun Line.possibleConfigurations(lastDamaged: Boolean): Long = cache.getOrPut(this to lastDamaged) {
		when {
			// at end, check if correct
			states.isEmpty() -> if (groups.isEmpty() || groups.singleOrNull() == 0) 1 else 0
			// then the whole rest has to be operational
			groups.isEmpty() -> if (states.all { it != SpringState.Damaged }) 1 else 0
			// must be still damaged
			lastDamaged && groups.first() > 0 -> when (states.first()) {
				SpringState.Damaged, SpringState.Unknown -> nextDamaged().possibleConfigurations(true)
				SpringState.Operational -> 0
			}
			// must be operational
			lastDamaged && groups.first() == 0 -> when (states.first()) {
				SpringState.Damaged -> 0
				SpringState.Operational, SpringState.Unknown ->
					nextOperationalNewGroup().possibleConfigurations(false)
			}
			// can be either
			else -> when (states.first()) {
				SpringState.Operational -> nextOperational().possibleConfigurations(false)
				SpringState.Damaged -> nextDamaged().possibleConfigurations(true)
				SpringState.Unknown -> {
					// this is the only branching case
					nextDamaged().possibleConfigurations(true) +
						nextOperational().possibleConfigurations(false)
				}
			}
		}
	}

	override fun partTwo(data: List<Line>): Long = data.map { line ->
		Line(
			states = List(5) { line.states }.reduce { acc, springStates ->
				acc + SpringState.Unknown + springStates
			},
			groups = List(5) { line.groups }.reduce { acc, groups -> acc + groups }
		)
	}
		.sumOf { it.possibleConfigurations(false) }
}
