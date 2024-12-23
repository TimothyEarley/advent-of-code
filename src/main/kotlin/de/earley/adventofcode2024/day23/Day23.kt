package de.earley.adventofcode2024.day23

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day23.start()

object Day23 : BaseSolution<Set<Day23.Connection>, Long, String>() {

	data class Connection(
		val from: String, val to: String
	)

	override fun parseInput(input: Sequence<String>): Set<Connection> = input.mapToList {
		it.split('-', limit = 2).let { (from, to) -> Connection(from, to) }
	}.toSet()

	override fun partOne(data: Set<Connection>): Long {
		val nodes = data.flatMap { listOf(it.from, it.to) }.toSet()
		return nodes.filter { it.startsWith("t") }.flatMap {
			clique3(it, data)
		}.toSet().count().toLong()
	}

	private fun clique3(from: String, connections: Set<Connection>): Set<Set<String>> {
		val connected = connections.connected(from)
		return connected.flatMap { a ->
			connected.filter { b ->
				connections.contains(Connection(a, b)) || connections.contains(Connection(b, a))
			}.map { b ->
				setOf(a, b, from)
			}
		}.toSet()
	}

	override fun partTwo(data: Set<Connection>): String {
		val nodes = data.flatMap { listOf(it.from, it.to) }.toSet()
		val neighbours = nodes.associateWith { data.connected(it) }
		val largest = largestClique(neighbours, nodes)
		return largest.sorted().joinToString(",")
	}

	private fun Set<Connection>.connected(with: String) = mapNotNull {
		if (it.from == with) it.to
		else if (it.to == with) it.from
		else null
	}.toSet()

	private fun largestClique(
		neighbours: Map<String, Set<String>>, nodes: Set<String>
	): Set<String> {
		class Node(
			val included: Set<String>,
			val candidates: Set<String>
		) {
			val key = included.sorted().joinToString(",")
			override fun equals(other: Any?): Boolean = other is Node && key == other.key
			override fun hashCode(): Int = key.hashCode()
		}

		val open = LinkedHashSet<Node>()
		open.add(Node(emptySet(), nodes))
		var best: Set<String> = emptySet()
		while (open.isNotEmpty()) {
			val next : Node = open.removeFirst()
			if (next.included.size > best.size) {
				best = next.included
			}
			if (next.included.size + next.candidates.size <= best.size) continue
			next.candidates
				.map {
					val newCandidates = next.candidates.toMutableSet().apply {
						retainAll(neighbours[it]!!)
					}
					Node(next.included + it, newCandidates)
				}
				.forEach {
					open += it
				}
		}

		return best
	}

}
