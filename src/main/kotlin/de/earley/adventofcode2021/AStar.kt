package de.earley.adventofcode2021

import java.util.*

class Node<T>(
	val value: T,
	val cost: Int,
	val heuristic: Int
)

fun <T> generalAStar(
	from: T,
	goal: (T) -> Boolean,
	heuristic: (T) -> Int,
	neighbours: T.() -> Sequence<Pair<T, Int>>,
	useClosed: Boolean,
	newNodeCallback: ((Node<T>) -> Unit)? = null
): Int {

	val closed = mutableSetOf<T>()
	val open = PriorityQueue(compareBy<Node<T>> { it.cost + it.heuristic }).apply {
		add(Node(from, 0, heuristic(from)))
	}

	while (open.isNotEmpty()) {
		val current = open.remove()

		if (useClosed) closed.add(current.value)

		if (goal(current.value))
			return current.cost

		// expand neighbours
		for ((next, costToEnter) in current.value.neighbours()) {
			// don't revisit closed nodes (the heuristic is admissible and consistent)
			if (useClosed && next in closed) continue

			val newNode = Node(next, current.cost + costToEnter, heuristic(next))
			newNodeCallback?.invoke(newNode)
			open.add(newNode)
		}
	}

	error("No path found!")
}
