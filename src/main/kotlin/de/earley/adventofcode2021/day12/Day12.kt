package de.earley.adventofcode2021.day12

import de.earley.adventofcode.BaseSolution

fun main() = Day12.start()

@JvmInline
value class Cave(val name: String) {
	val small: Boolean
		get() = name.all(Char::isLowerCase)
}

object Day12 : BaseSolution<Map<Cave, List<Cave>>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Map<Cave, List<Cave>> =
		input.flatMap {
			val (from, to) = it.split('-', limit = 2).map(::Cave)
			listOf(
				from to to,
				to to from
			)
		}.groupBy({ it.first }) { it.second }

	override fun partOne(data: Map<Cave, List<Cave>>): Int = data.countPaths(false)

	override fun partTwo(data: Map<Cave, List<Cave>>): Int = data.countPaths(true)

	private fun Map<Cave, List<Cave>>.countPaths(
		canVisitTwice: Boolean,
		visited: Set<Cave> = emptySet(),
		next: Cave = Cave("start"),
	): Int {
		// we cannot leave the end
		if (next.name == "end") return 1
		val previousVisit = visited.contains(next)

		// we cannot reenter the start
		if (next.name == "start" && previousVisit) return 0

		// we cannot reenter a small cave (unless [canVisitTwice] is set, in which case unset it)
		var newCanVisit = canVisitTwice
		if (next.small && previousVisit) {
			if (!canVisitTwice) {
				return 0
			} else {
				newCanVisit = false
			}
		}

		return getOrDefault(next, emptyList()).sumOf {
			countPaths(newCanVisit, visited + next, it)
		}
	}
}
