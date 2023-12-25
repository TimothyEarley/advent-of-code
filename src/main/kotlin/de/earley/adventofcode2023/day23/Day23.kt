package de.earley.adventofcode2023.day23

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
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
		val end = data.pointValues().last { it.second == Tile.Path }.first
		return data.toGraph(true).longestPath(end)
	}

	override fun partTwo(data: Grid<Tile>): Long {
		val end = data.pointValues().last { it.second == Tile.Path }.first
		return data.toGraph(false).longestPath(end)
	}

	private class Node(
		val point: Point,
		val neighbours: MutableSet<Pair<Node, Int>>,
	)

	private fun Point.pathNeighbours(data: Grid<Tile>, slopes: Boolean): Sequence<Point> =
		(
			if (slopes) {
				when (data[this]) {
					Tile.Path -> neighbours()
					Tile.Forest, null -> sequenceOf()
					Tile.SlopeDown -> sequenceOf(this + Direction.Down.point)
					Tile.SlopeRight -> sequenceOf(this + Direction.Right.point)
					Tile.SlopeLeft -> sequenceOf(this + Direction.Left.point)
					Tile.SlopeUp -> sequenceOf(this + Direction.Up.point)
				}
			} else {
				neighbours()
			}
			)
			.filter { data[it] != Tile.Forest }

	private fun Grid<Tile>.toGraph(slopes: Boolean): Node {
		val start = pointValues().first { it.second == Tile.Path }.first
		val end = pointValues().last { it.second == Tile.Path }.first

		val nodes: Map<Point, Node> = this.pointValues()
			.filter { (p, tile) ->
				p == start || p == end || (tile == Tile.Path && p.pathNeighbours(this, slopes).count() >= 3)
			}
			.associate { it.first to Node(it.first, mutableSetOf()) }

		tailrec fun toNextJunction(current: Point, prev: Point, distance: Int): Pair<Node, Int>? =
			if (current in nodes.keys) {
				nodes[current]!! to distance
			} else {
				val next = current.pathNeighbours(this, slopes).singleOrNull { it != prev }
				if (next == null) null else toNextJunction(next, current, distance + 1)
			}

		nodes.forEach { (p, node) ->
			// travel along the paths to next junction
			p.pathNeighbours(this, slopes)
				.mapNotNull { toNextJunction(it, p, 1) }
				.forEach {
					node.neighbours.add(it)
				}
		}

		return nodes[start]!!
	}

	private fun Node.longestPath(end: Point, visited: Set<Node> = emptySet()): Long =
		if (point == end) {
			0
		} else {
			neighbours
				.filter { it.first !in visited }
				.maxOfOrNull { it.second + it.first.longestPath(end, visited + it.first) }
				?: Long.MIN_VALUE
		}
}
