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
}

fun <T> generalAStarNode(
	from: T,
	goal: (T) -> Boolean,
	heuristic: (T) -> Int,
	neighbours: T.() -> Sequence<Pair<T, Int>>,
	useClosed: Boolean,
	closedCheck: Set<T>.(T) -> Boolean = Set<T>::contains,
	newNodeCallback: ((Node<T>) -> Unit)? = null,
): Node<T> {
	val closed = mutableSetOf<T>()
	val open = PriorityQueue(compareBy<Node<T>> { it.cost + it.heuristic }).apply {
		add(Node(null, from, 0, heuristic(from)))
	}

	while (open.isNotEmpty()) {
// 		println(open)

		val current = open.remove()

		// if (useClosed) closed.add(current.value)

		if (goal(current.value)) {
			return current
		}

		// expand neighbours
		for ((next, costToEnter) in current.value.neighbours()) {
			// don't revisit closed nodes (the heuristic is admissible and consistent)
			if (useClosed) {
				if (closed.closedCheck(next)) continue
				closed.add(next)
			}

			val newNode = Node(current, next, current.cost + costToEnter, heuristic(next))
			newNodeCallback?.invoke(newNode)
			open.add(newNode)
		}
	}

	error("No path found!")
}

fun <T> generalAStar(
	from: T,
	goal: (T) -> Boolean,
	heuristic: (T) -> Int,
	neighbours: T.() -> Sequence<Pair<T, Int>>,
	useClosed: Boolean,
	closedCheck: Set<T>.(T) -> Boolean = Set<T>::contains,
	newNodeCallback: ((Node<T>) -> Unit)? = null,
): Int = generalAStarNode(from, goal, heuristic, neighbours, useClosed, closedCheck, newNodeCallback).cost
