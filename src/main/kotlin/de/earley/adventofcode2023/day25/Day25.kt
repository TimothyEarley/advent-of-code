package de.earley.adventofcode2023.day25

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStarNode

fun main() = Day25.start()

object Day25 : BaseSolution<Map<String, List<String>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Map<String, List<String>> = input.map {
		val (a, b) = it.split(": ")
		a to b.split(" ")
	}.toMap()

	override fun partOne(data: Map<String, List<String>>): Long {
		val complete = data.complete()
		val keys = complete.keys
		val flattened = complete.entries.flatMap { entry ->
			entry.value.map { entry.key to it }
		}.filter { it.first < it.second }

		// pick three
		flattened.forEach { a ->
			// next cut must be on the path from left to right
			val pathA = generalAStarNode(
				a.first,
				{ it == a.second },
				{ 0 },
				{
					complete[this]!!
						.asSequence()
						.filter { (it to this) != a && (this to it) != a }
						.map { it to 1 }
				},
				false,
			).first().toPath()

			pathA.zipWithNext().map(::order).forEach { b ->
				val pathB = generalAStarNode(
					a.first,
					{ it == a.second },
					{ 0 },
					{
						complete[this]!!
							.asSequence()
							.filter { (it to this) != a && (this to it) != a && (it to this) != b && (this to it) != b }
							.map { it to 1 }
					},
					false,
				).first().toPath()

				pathB.zipWithNext().map(::order).forEach { c ->

					// check
					val fillLeft = floodFill(setOf(a.first), complete, listOf(a, b, c), emptySet())
					val fillRight = floodFill(setOf(a.second), complete, listOf(a, b, c), emptySet())
					if (fillLeft.intersect(fillRight).isEmpty() && (fillLeft.union(fillRight) == keys)) {
						return fillLeft.size.toLong() * fillRight.size
					}
				}
			}
		}

		error("No solution found")
	}

	private fun order(it: (Pair<String, String>)): Pair<String, String> =
		if (it.first < it.second) it else (it.second to it.first)

	private fun Map<String, List<String>>.complete(): Map<String, List<String>> =
		(keys + values.flatten()).associateWith { key ->
			(this.getOrDefault(key, emptyList()) + this.filter { key in it.value }.map { it.key })
		}

	private tailrec fun floodFill(
		open: Set<String>,
		map: Map<String, List<String>>,
		removed: List<Pair<String, String>>,
		seen: Set<String>,
	): Set<String> =
		if (open.isEmpty()) {
			seen
		} else {
			val first = open.first()
			val rest = open.drop(1)
			val next = map[first]!!
				.filter { (first to it) !in removed && (it to first) !in removed }
				.filter { it !in seen }
			floodFill((rest + next).toSet(), map, removed, seen + first)
		}

	override fun partTwo(data: Map<String, List<String>>): Long = 0
}
