package de.earley.adventofcode2022.day16

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStarNode
import de.earley.adventofcode.mapToList

fun main() = Day16.start()

object Day16 : BaseSolution<Map<String, Day16.Valve>, Int, Int>() {

	private val regex = "Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? ((?>\\w+,? ?)+)".toRegex()
	override fun parseInput(input: Sequence<String>): Map<String, Valve> = input.mapToList {
		val (name, rate, connections) = regex.matchEntire(it)?.destructured ?: error(
			it
		)
		Valve(name, rate.toInt(), connections.split(", "))
	}
		.associateBy(Valve::name)

	override fun partOne(data: Map<String, Valve>): Int = findBestPressureFlow(
		data,
		SingleState(1, "AA", emptySet(), 0, 0),
		30,
		1
	)
	override fun partTwo(data: Map<String, Valve>): Int = findBestPressureFlow(
		data,
		StateWithElephant(1, "AA", "AA", emptySet(), 0),
		26,
		2
	)

	private fun <T : State<T>> findBestPressureFlow(
		data: Map<String, Valve>,
		start: T,
		time: Int,
		helpers: Int,
	): Int {
		val simTime = time + 1
		val needToOpen = data.entries.filter { it.value.flowRate > 0 }.map { it.key }.toSet()

		val result = generalAStarNode<T>(
			from = start,
			goal = { it.minute == simTime },
			heuristic = { s ->
				// how much can we possibly release
				// upper bound, so open all valves in order of best flow rate
				val timeLeft = simTime - s.minute
				val currentFlowTotal = s.releasePerMinute * timeLeft
				val canOpenFlow = data.entries
					.filter { it.key !in s.open }
					.sortedByDescending { it.value.flowRate }
					.windowed(helpers, partialWindows = true)
					.take(timeLeft / 2)
					.fold(0 to timeLeft) { (flow, timeLeft), entries ->
						((flow + (timeLeft - 1) * entries.sumOf { it.value.flowRate }) to (timeLeft - 2))
					}.first

				-(currentFlowTotal + canOpenFlow)
			},
			neighbours = { nextStates(data, needToOpen, simTime) },
			useClosed = true
		)

		return if (result.parent!!.value.minute == simTime - 1) {
			// if we had a parent, use that
			-result.parent.cost + result.parent.value.releasePerMinute
		} else {
			// we skipped some, so in fact the last node is correct
			-result.cost
		}
	}

	data class Valve(
		val name: String,
		val flowRate: Int,
		val tunnels: List<String>,
	)

	interface State<SELF : State<SELF>> {
		val minute: Int
		val releasePerMinute: Int
		val open: Set<String>

		fun nextStates(
			data: Map<String, Valve>,
			needToOpen: Set<String>,
			totalTime: Int,
		): Sequence<Pair<SELF, Int>>
	}

	data class StateWithElephant(
		override val minute: Int,
		val atValve: String,
		val elephantAtValve: String,
		override val open: Set<String>,
		override val releasePerMinute: Int,
	) : State<StateWithElephant> {
		override fun nextStates(
			data: Map<String, Valve>,
			needToOpen: Set<String>,
			totalTime: Int,
		): Sequence<Pair<StateWithElephant, Int>> =
			if (open.containsAll(needToOpen)) {
				// we have opened all that we can
				val leftRelease = (releasePerMinute * (totalTime - minute))
				sequenceOf(
					StateWithElephant(
						totalTime,
						atValve,
						elephantAtValve,
						open,
						releasePerMinute
					) to -leftRelease
				)
			} else {
				sequence {
					val valve = data[atValve]!!
					val elephantValve = data[elephantAtValve]!!

					if (atValve in needToOpen && atValve !in open) {
						if (elephantAtValve != atValve && elephantAtValve in needToOpen && elephantAtValve !in open) {
							yield(
								StateWithElephant(
									minute + 1,
									atValve,
									elephantAtValve,
									open + atValve + elephantAtValve,
									releasePerMinute + valve.flowRate + elephantValve.flowRate
								)
							)
						}

						elephantValve.tunnels.forEach { elephantNeighbour ->
							yield(
								StateWithElephant(
									minute + 1,
									atValve,
									elephantNeighbour,
									open + atValve,
									releasePerMinute + valve.flowRate
								)
							)
						}
					}

					valve.tunnels.forEach { neighbour ->
						if (elephantAtValve in needToOpen && elephantAtValve !in open) {
							yield(
								StateWithElephant(
									minute + 1,
									neighbour,
									elephantAtValve,
									open + elephantAtValve,
									releasePerMinute + elephantValve.flowRate
								)
							)
						}

						elephantValve.tunnels.forEach { elephantNeighbour ->
							yield(
								StateWithElephant(
									minute + 1,
									neighbour,
									elephantNeighbour,
									open,
									releasePerMinute
								)
							)
						}
					}
				}.map { it to -releasePerMinute }
			}
	}

	data class SingleState(
		override val minute: Int,
		val atValve: String,
		override val open: Set<String>,
		val release: Int,
		override val releasePerMinute: Int,
	) : State<SingleState> {
		override fun nextStates(
			data: Map<String, Valve>,
			needToOpen: Set<String>,
			totalTime: Int,
		): Sequence<Pair<SingleState, Int>> =
			if (open.containsAll(needToOpen)) {
				// we have opened all that we can
				val leftRelease = (releasePerMinute * (totalTime - minute))
				sequenceOf(
					SingleState(
						totalTime,
						atValve,
						open,
						release + leftRelease,
						releasePerMinute
					) to -leftRelease
				)
			} else {
				sequence {
					val valve = data[atValve]!!

					if (atValve in needToOpen && atValve !in open) {
						val next = openValve(valve.flowRate)
						yield(next to -releasePerMinute)
					}

					valve.tunnels.forEach { neighbour ->
						yield(goto(neighbour) to -releasePerMinute)
					}
				}
			}

		private fun openValve(flow: Int) = SingleState(
			minute + 1,
			atValve,
			open + atValve,
			release + releasePerMinute,
			releasePerMinute + flow
		)

		private fun goto(neighbour: String) = SingleState(
			minute + 1,
			neighbour,
			open,
			release + releasePerMinute,
			releasePerMinute
		)
	}
}
