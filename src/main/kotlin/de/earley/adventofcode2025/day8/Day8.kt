package de.earley.adventofcode2025.day8

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Point3
import de.earley.adventofcode.distanceSquared
import de.earley.adventofcode.mapToList

fun main() = Day8(1000).start()

class Day8(val iterations: Int) : BaseSolution<List<Point3>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Point3> = input.mapToList { Point3.parse(it) }

	override fun partOne(data: List<Point3>): Long {

		val circuits = data.map { mutableSetOf(it) }.toMutableList()

		data
			.pairs()
			.map { (a, b) ->
				a.distanceSquared(b) to (a to b)
			}
			.sortedBy { it.first }
			.take(iterations)
			.forEach { (_, connection) ->
				val circuitA = circuits.find { it.contains(connection.first) }!!
				val circuitB = circuits.find { it.contains(connection.second) }!!
				if (circuitA != circuitB) {
					circuitA.addAll(circuitB)
					circuits.remove(circuitB)
				}
			}

		return circuits.map { it.size.toLong() }.sortedDescending().take(3).reduce(Long::times)
	}

	override fun partTwo(data: List<Point3>): Long {
		val circuits = data.map { mutableSetOf(it) }.toMutableList()

		data
			.pairs()
			.map { (a, b) ->
				a.distanceSquared(b) to (a to b)
			}
			.sortedBy { it.first }
			.forEach { (_, connection) ->
				val circuitA = circuits.find { it.contains(connection.first) }!!
				val circuitB = circuits.find { it.contains(connection.second) }!!
				if (circuitA != circuitB) {
					circuitA.addAll(circuitB)
					circuits.remove(circuitB)
					if (circuits.size == 1) return connection.first.x.toLong() * connection.second.x
				}
			}

		error("Failed to connect all junction boxes")
	}

}

private fun <T> List<T>.pairs(): List<Pair<T, T>> = indices.flatMap { i ->
	val a = this[i]
	(i + 1..lastIndex).map { a to this[it] }
}
