package de.earley.adventofcode2021.day23

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode2021.mapToList

fun main() = Day23.start()

object Day23 : BaseSolution<State, Int, Int>() {

	override fun parseInput(input: Sequence<String>): State {
		val (lineOne, lineTwo) = input.drop(2).take(2).mapToList {
			it.replace("#", "").replace(" ", "")
		}

		return State(
			lineOne.mapIndexed { index, c -> Amphipod(parseAmphipodType(c), SideRoom(index, 0)) } +
				lineTwo.mapIndexed { index, c -> Amphipod(parseAmphipodType(c), SideRoom(index, 1)) }
		)
	}

	override fun partOne(data: State): Int = findLeastEnergy(data, false)

	override fun partTwo(data: State): Int {
		/*
		 #D#C#B#A#
  		 #D#B#A#C#
		 */

		val unfoldedState = data.amphipods.map {
			Amphipod(it.type, (it.position as SideRoom).copy(space = if (it.position.space == 0) 0 else 3))
		} + listOf(
			Amphipod(AmphipodType.Desert, SideRoom(0, 1)),
			Amphipod(AmphipodType.Copper, SideRoom(1, 1)),
			Amphipod(AmphipodType.Bronze, SideRoom(2, 1)),
			Amphipod(AmphipodType.Amber, SideRoom(3, 1)),
			Amphipod(AmphipodType.Desert, SideRoom(0, 2)),
			Amphipod(AmphipodType.Bronze, SideRoom(1, 2)),
			Amphipod(AmphipodType.Amber, SideRoom(2, 2)),
			Amphipod(AmphipodType.Copper, SideRoom(3, 2))
		)

		return findLeastEnergy(State(unfoldedState), true)
	}

	private fun findLeastEnergy(state: State, unfolded: Boolean): Int = generalAStar(
		state,
		{ it.isSolved() },
		{ 0 },
		{ nextStates(unfolded) },
		false
	)

	private fun State.nextStates(unfolded: Boolean): Sequence<Pair<State, Int>> {
		// 1. prio - can an amphipod reach its final position
		fun isSettled(ampi: Amphipod) =
			ampi.position is SideRoom && ampi.position.room == ampi.type.desiredSideRoom &&
				(
					if (unfolded) {
						when (ampi.position.space) {
							0 -> getAtPosition(ampi.position.copy(space = 1))?.type == ampi.type &&
								getAtPosition(ampi.position.copy(space = 2))?.type == ampi.type &&
								getAtPosition(ampi.position.copy(space = 3))?.type == ampi.type

							1 -> getAtPosition(ampi.position.copy(space = 2))?.type == ampi.type &&
								getAtPosition(ampi.position.copy(space = 3))?.type == ampi.type

							2 -> getAtPosition(ampi.position.copy(space = 3))?.type == ampi.type
							3 -> true
							else -> error("Invalid position")
						}
					} else {
						ampi.position.space == 1 || getAtPosition(ampi.position.copy(space = 1))?.type == ampi.type
					}
					)

		// 1. prio: move to a final position
		for (ampi in amphipods) {
			if (isSettled(ampi)) continue

			// must have no other types in there
			if (getAtPosition(SideRoom(ampi.type.desiredSideRoom, 0))
					.let { it != null && it.type != ampi.type } ||
				getAtPosition(SideRoom(ampi.type.desiredSideRoom, 1))
					.let { it != null && it.type != ampi.type } ||
				getAtPosition(SideRoom(ampi.type.desiredSideRoom, 2))
					.let { it != null && it.type != ampi.type } ||
				getAtPosition(SideRoom(ampi.type.desiredSideRoom, 3))
					.let { it != null && it.type != ampi.type }
			) {
				continue
			}

			if (unfolded) {
				val pos3 = SideRoom(ampi.type.desiredSideRoom, 3)
				val cost3 = pathCost(this, unfolded, ampi, pos3)
				if (cost3 != null) {
					return sequenceOf(
						State(amphipods - ampi + Amphipod(ampi.type, pos3)) to cost3
					)
				}
				val pos2 = SideRoom(ampi.type.desiredSideRoom, 2)
				val cost2 = pathCost(this, unfolded, ampi, pos2)
				if (cost2 != null) {
					return sequenceOf(State(amphipods - ampi + Amphipod(ampi.type, pos2)) to cost2)
				}
			}

			val pos1 = SideRoom(ampi.type.desiredSideRoom, 1)
			val cost1 = pathCost(this, unfolded, ampi, pos1)
			if (cost1 != null) {
				return sequenceOf(State(amphipods - ampi + Amphipod(ampi.type, pos1)) to cost1)
			}
			val pos0 = SideRoom(ampi.type.desiredSideRoom, 0)
			val cost0 = pathCost(this, unfolded, ampi, pos0)
			if (cost0 != null) {
				return sequenceOf(State(amphipods - ampi + Amphipod(ampi.type, pos0)) to cost0)
			}
		}

		// 2. prio move an amphipod to the hallway
		return amphipods.asSequence()
			.filter { it.position is SideRoom && !isSettled(it) }
			.flatMap { amphi ->
				hallwayStops.map { it to pathCost(this, unfolded, amphi, it) }
					.filter { it.second != null }
					.map { (it, cost) ->
						State(amphipods - amphi + Amphipod(amphi.type, it)) to cost!!
					}
			}
	}

	// valid attempts are any non exit hallway
	private val hallwayStops: List<Hall> = listOf(
		Hall(0),
		Hall(1),
		Hall(3),
		Hall(5),
		Hall(7),
		Hall(9),
		Hall(10),
	)

	private fun pathCost(data: State, unfolded: Boolean, from: Amphipod, to: Position): Int? =
		pathCost(data, unfolded, from.position, to)?.times(from.type.energyPerStep)

	private fun pathCost(
		data: State,
		unfolded: Boolean,
		from: Position,
		to: Position,
		visited: Set<Position> = emptySet(),
	): Int? {
		if (from == to) return 0

		// find neighbours
		val neighbours = when (from) {
			is Hall -> listOfNotNull(
				Hall(from.pos - 1).takeIf { it.pos >= 0 },
				Hall(from.pos + 1).takeIf { it.pos <= 10 },
				SideRoom(0, 0).takeIf { from.pos == 2 },
				SideRoom(1, 0).takeIf { from.pos == 4 },
				SideRoom(2, 0).takeIf { from.pos == 6 },
				SideRoom(3, 0).takeIf { from.pos == 8 }
			)

			is SideRoom -> listOfNotNull(
				SideRoom(from.room, from.space - 1).takeIf { it.space >= 0 },
				SideRoom(from.room, from.space + 1).takeIf { it.space <= 1 || (unfolded && it.space <= 3) },
				Hall((from.room + 1) * 2).takeIf { from.space == 0 }
			)
		}.filter {
			data.getAtPosition(it) == null && it !in visited
		}

		return neighbours.mapNotNull {
			pathCost(data, unfolded, it, to, visited + neighbours)?.plus(1)
		}.minOrNull()
	}
}

class State(
	val amphipods: List<Amphipod>,
) {

	private fun symbolAt(p: Position): Char = getAtPosition(p)?.type?.symbol ?: ' '

	override fun toString(): String = """
#############
#${String((0..10).map { symbolAt(Hall(it)) }.toCharArray())}#
###${symbolAt(SideRoom(0, 0))}#${symbolAt(SideRoom(1, 0))}#${symbolAt(SideRoom(2, 0))}#${symbolAt(SideRoom(3, 0))}###
  #${symbolAt(SideRoom(0, 1))}#${symbolAt(SideRoom(1, 1))}#${symbolAt(SideRoom(2, 1))}#${symbolAt(SideRoom(3, 1))}#
  #########
"""
}

class Amphipod(
	val type: AmphipodType,
	val position: Position,
)

enum class AmphipodType(val symbol: Char, val desiredSideRoom: Int, val energyPerStep: Int) {
	Amber('A', 0, 1),
	Bronze('B', 1, 10),
	Copper('C', 2, 100),
	Desert('D', 3, 1000),
}

fun parseAmphipodType(c: Char): AmphipodType = when (c) {
	'A' -> AmphipodType.Amber
	'B' -> AmphipodType.Bronze
	'C' -> AmphipodType.Copper
	'D' -> AmphipodType.Desert
	else -> error("Failed to parse amphipod type $c")
}

sealed interface Position
data class Hall(val pos: Int) : Position
data class SideRoom(val room: Int, val space: Int) : Position

fun Amphipod.isCorrectRoom(pos: Position) = pos is SideRoom && pos.space == this.type.desiredSideRoom

fun State.isSolved(): Boolean =
	amphipods.all {
		when (it.type) {
			AmphipodType.Amber -> it.position is SideRoom && it.position.room == 0
			AmphipodType.Bronze -> it.position is SideRoom && it.position.room == 1
			AmphipodType.Copper -> it.position is SideRoom && it.position.room == 2
			AmphipodType.Desert -> it.position is SideRoom && it.position.room == 3
		}
	}

fun State.getAtPosition(p: Position): Amphipod? = amphipods.find { it.position == p }
