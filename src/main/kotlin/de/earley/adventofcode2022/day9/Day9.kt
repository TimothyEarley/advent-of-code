package de.earley.adventofcode2022.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Point
import de.earley.adventofcode.isNeighbourOrSameOf
import de.earley.adventofcode.mapToList
import java.math.RoundingMode

fun main() = Day9.start()

object Day9 : BaseSolution<List<Day9.Motion>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Motion> = input.mapToList {
		val (d, s) = it.split(" ")
		Motion(Direction.parseInitial(d.single()), s.toInt())
	}

	override fun partOne(data: List<Motion>): Int =
		simulateRope(data, 1)

	override fun partTwo(data: List<Motion>): Int = simulateRope(data, 9)

	fun simulateRope(data: List<Motion>, tails: Int, hook: (State) -> Unit = {}) = data.asSequence()
		.flatMap { motion ->
			sequence {
				repeat(motion.steps) {
					yield(motion.direction)
				}
			}
		}
		.fold(State.initial(tails) to setOf(Point(0, 0))) { (state, seen), direction ->
			val newHead = state.head + direction.point
			val newTails = state.tails.runningFold(newHead) { connectedKnot, myPosition ->
				if (myPosition.isNeighbourOrSameOf(connectedKnot, diagonal = true)) {
					myPosition
				} else {
					// tail needs to move by half the delta
					myPosition + (connectedKnot - myPosition).divRound(2) {
						this.toBigDecimal().setScale(0, RoundingMode.UP).toInt()
					}
				}
			}.drop(1) // first is head
			val newState = State(newHead, newTails)
			hook(newState)
			newState to (seen + newTails.last())
		}.second.size

	data class Motion(val direction: Direction, val steps: Int)
	data class State(val head: Point, val tails: List<Point>) {
		companion object {
			fun initial(tails: Int) = State(Point(0, 0), List(tails) { Point(0, 0) })
		}
	}
}
