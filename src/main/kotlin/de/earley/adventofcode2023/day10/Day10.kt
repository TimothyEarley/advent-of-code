package de.earley.adventofcode2023.day10

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.grid
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.prettyPrint
import kotlin.math.min

fun main() = Day10.start()

object Day10 : BaseSolution<Grid<Day10.Pipe>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Pipe> = input.toList().let {
		Grid(
			it.first().length,
			it.size,
			it.flatMap { line ->
				line.toCharArray().map { c ->
					Pipe.entries.find { it.symbol == c } ?: error("Not found: $c")
				}.toList()
			}
		)
	}

	enum class Pipe(val symbol: Char) {
		Start('S'),
		Vertical('|'),
		Horizontal('-'),
		NorthEast('L'),
		NorthWest('J'),
		SouthWest('7'),
		SouthEast('F'),
		NoPipe('.'),
	}

	private fun Pipe.neighbours(from: Point): List<Point> = when (this) {
		Pipe.Start -> emptyList()
		Pipe.Vertical -> listOf(from + Direction.Up.point, from + Direction.Down.point)
		Pipe.Horizontal -> listOf(from + Direction.Left.point, from + Direction.Right.point)
		Pipe.NorthEast -> listOf(from + Direction.Up.point, from + Direction.Right.point)
		Pipe.NorthWest -> listOf(from + Direction.Up.point, from + Direction.Left.point)
		Pipe.SouthWest -> listOf(from + Direction.Down.point, from + Direction.Left.point)
		Pipe.SouthEast -> listOf(from + Direction.Down.point, from + Direction.Right.point)
		Pipe.NoPipe -> emptyList()
	}

	override fun partOne(data: Grid<Pipe>): Long {
		val start = data.pointValues().find { it.second == Pipe.Start }!!.first
		val next = data.pointValues().filter { (p, pipe) ->
			start in pipe.neighbours(p)
		}.mapToList { (p, _) -> start to p }
		require(next.size == 2)
		val dist = distance(data, emptyMap(), next, 1)
		return dist.maxOf { it.value }.toLong()
	}

	private tailrec fun distance(
		grid: Grid<Pipe>,
		map: Map<Point, Int>,
		next: List<Pair<Point, Point>>,
		dist: Int
	): Map<Point, Int> {
		if (next.isEmpty()) return map
		val newMap = map + next.map { it.second to dist }
		return distance(
			grid,
			newMap,
			next.mapNotNull { (prev, n) ->
				val neighbours = grid[n]!!.neighbours(n)
				neighbours.find { it != prev }?.let { n to it }
			}.filter {
				it.second !in map.keys
			},
			dist + 1
		)
	}

	override fun partTwo(data: Grid<Pipe>): Long {
		val start = data.pointValues().find { it.second == Pipe.Start }!!.first
		val next = data.pointValues().filter { (p, pipe) ->
			start in pipe.neighbours(p)
		}.mapToList { (p, _) -> start to p }
		require(next.size == 2)
		val loop = distance(data, emptyMap(), next, 1).map { it.key } + start
		val gridWithStartReplaced = grid(data.width, data.height) {
			if (it == start) {
				when (val foo = next.map { it.second - it.first }) {
					listOf(Point(-1, 0), Point(0, 1)) -> Pipe.SouthWest
					listOf(Point(-1, 0), Point(1, 0)) -> Pipe.Horizontal
					else -> TODO(foo.toString())
				}
			} else {
				data[it]
			}
		}

		val expandedGrid = grid(data.width * 3, data.height * 3) { p ->
			val middle = Point(p.x / 3, p.y / 3)
			if (middle !in loop) return@grid false
			val pipeAtMiddle = gridWithStartReplaced[middle]!!
			when (p - middle * 3) {
				Point(0, 0) -> false
				Point(1, 0) -> pipeAtMiddle in listOf(Pipe.NorthEast, Pipe.NorthWest, Pipe.Vertical)
				Point(2, 0) -> false
				Point(0, 1) -> pipeAtMiddle in listOf(Pipe.NorthWest, Pipe.SouthWest, Pipe.Horizontal)
				Point(1, 1) -> true
				Point(2, 1) -> pipeAtMiddle in listOf(Pipe.NorthEast, Pipe.SouthEast, Pipe.Horizontal)
				Point(0, 2) -> false
				Point(1, 2) -> pipeAtMiddle in listOf(Pipe.SouthEast, Pipe.SouthWest, Pipe.Vertical)
				Point(2, 2) -> false
				else -> error("Not possible")
			}
		}

		expandedGrid.map { if (it) '█' else ' ' }.prettyPrint()

		return data.indices.count { p ->
			p !in loop && generalAStar(
				p * 3,
				{ it !in expandedGrid },
				{ min(it.x, expandedGrid.width - it.x) + min(it.y, expandedGrid.height - it.y) },
				{ neighbours().filterNot { expandedGrid[it] ?: false }.map { it to 1 } },
				true
			) == null
		}.toLong()
	}
}