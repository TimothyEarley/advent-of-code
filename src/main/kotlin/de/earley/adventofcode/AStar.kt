package de.earley.adventofcode

import java.util.PriorityQueue

class Node<T>(
	val parent: Node<T>?,
	val value: T,
	val cost: Int,
	val heuristic: Int,
) {
	override fun toString(): String =
		"Node(parent=${parent?.hashCode()}, value=$value, cost=$cost, heuristic=$heuristic)"

	fun toPath(): List<T> = (parent?.toPath() ?: emptyList()) + this.value
}

fun <T> generalAStarNode(
	from: T,
	goal: (T) -> Boolean,
	heuristic: (T) -> Int,
	neighbours: T.() -> Sequence<Pair<T, Int>>,
	newNodeCallback: ((Node<T>) -> Unit)? = null,
): Sequence<Node<T>> = sequence {
	val closed = mutableMapOf<T, Int>()
	val open = PriorityQueue(compareBy<Node<T>> { it.cost + it.heuristic }).apply {
		add(Node(null, from, 0, heuristic(from)))
	}
	var min = Int.MAX_VALUE

	while (open.isNotEmpty()) {
		val current = open.remove()

		// don't revisit closed nodes (works if the heuristic is admissible and consistent) -> e.g. constant 0
		if (closed.contains(current.value) && closed[current.value]!! < current.cost) continue
		closed[current.value] = current.cost

		if (goal(current.value)) {
			if (current.cost <= min) {
				// new (equal) best
				min = current.cost
				yield(current)
			} else {
				// the next best is worse than what we have already
				return@sequence
			}
		}

		// expand neighbours
		for ((next, costToEnter) in current.value.neighbours()) {
			val nextCost = current.cost + costToEnter
			val newNode = Node(current, next, nextCost, heuristic(next))
			newNodeCallback?.invoke(newNode)
			open.add(newNode)
		}
	}
}

fun <T> generalAStar(
	from: T,
	goal: (T) -> Boolean,
	heuristic: (T) -> Int,
	neighbours: T.() -> Sequence<Pair<T, Int>>,
	newNodeCallback: ((Node<T>) -> Unit)? = null,
): Int? = generalAStarNode(from, goal, heuristic, neighbours, newNodeCallback).firstOrNull()?.cost
