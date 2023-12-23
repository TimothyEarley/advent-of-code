package de.earley.adventofcode2023.day23

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours

fun main() = Day23.start()

object Day23 : BaseSolution<Grid<Day23.Tile>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Tile> = input.toList().let { lines ->
		Grid(
			lines.first().length,
			lines.size,
			lines.flatMap { line -> line.map { c -> Tile.entries.find { it.symbol == c }!! } }
		)
	}

	enum class Tile(val symbol: Char) {
		Path('.'), Forest('#'), SlopeDown('v'), SlopeRight('>'), SlopeLeft('<'), SlopeUp('^')
	}

	override fun partOne(data: Grid<Tile>): Long {
		val start = data.pointValues().first { it.second == Tile.Path }.first
		val end = data.pointValues().last { it.second == Tile.Path }.first

		return longestPath(start, end, data)
	}

	private fun longestPath(current: Point, end: Point, data: Grid<Tile>): Long {
		val f = DeepRecursiveFunction<Pair<Point, Set<Point>>, Long> { (current, visited) ->
			if (current == end) {
				return@DeepRecursiveFunction 0
			}
			val newVisited = visited + current
			val nexts = when (data[current]!!) {
				Tile.Path -> current.neighbours()
				Tile.Forest -> sequenceOf()
				Tile.SlopeDown -> sequenceOf(current.copy(y = current.y + 1))
				Tile.SlopeRight -> sequenceOf(current.copy(x = current.x + 1))
				Tile.SlopeLeft -> sequenceOf(current.copy(x = current.x - 1))
				Tile.SlopeUp -> sequenceOf(current.copy(y = current.y - 1))
			}
				.filter { it in data }
				.filter { data[it] != Tile.Forest }
				.filter { it !in visited }
				.toList()

			return@DeepRecursiveFunction nexts.maxOfOrNull { 1 + callRecursive(it to newVisited) } ?: Long.MIN_VALUE
		}

		return f(current to emptySet())
	}

	override fun partTwo(data: Grid<Tile>): Long {
		val startNode = data.toGraph()
		val end = data.pointValues().last { it.second == Tile.Path }.first
		return longestPath2(startNode, end, emptySet()) - 1
	}

	private class Node(
		val point: Point,
		val neighbours: MutableSet<Pair<Node, Int>>,
	)

	private fun Grid<Tile>.toGraph(): Node {
		val start = pointValues().first { it.second == Tile.Path }.first
		val end = pointValues().last { it.second == Tile.Path }.first
		val nodes = mutableMapOf<Point, Node>()
		val visited: MutableSet<Point> = mutableSetOf()

		class Arg(
			val current: Point,
			val previous: Node,
			val distance: Int,
		)

		val traverse = DeepRecursiveFunction<Arg, Unit> { arg ->
			visited.add(arg.current)
			val nexts = arg.current.neighbours()
				.filter { it in this@toGraph }
				.filter { this@toGraph[it] != Tile.Forest }
				.filter { it !in visited }
				.toList()

			if (nexts.size == 1 && arg.current != end) {
				callRecursive(Arg(nexts.single(), arg.previous, arg.distance + 1))
			} else {
				// this is a junction (or end)
				val node = nodes.getOrPut(arg.current) { Node(arg.current, mutableSetOf()) }
				node.neighbours.add(arg.previous to arg.distance)
				arg.previous.neighbours.add(node to arg.distance)
				nexts.forEach {
					callRecursive(Arg(it, node, 1))
				}
			}
		}

		val startNode = Node(start, mutableSetOf())
		nodes[start] = startNode
		traverse(Arg(start, startNode, 1))

		return startNode
	}

	private fun longestPath2(current: Node, end: Point, visited: Set<Node>): Long =
		if (current.point == end) {
			0
		} else {
			current.neighbours
				.filter { it.first !in visited }
				.maxOfOrNull { it.second + longestPath2(it.first, end, visited + it.first) }
				?: Long.MIN_VALUE
		}
}
