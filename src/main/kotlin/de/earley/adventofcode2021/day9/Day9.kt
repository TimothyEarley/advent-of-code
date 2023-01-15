package de.earley.adventofcode2021.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours
import de.earley.adventofcode2021.mapToList

fun main() = Day9.start()

typealias Heightmap = Grid<Int>

object Day9 : BaseSolution<Heightmap, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Heightmap {
		val l = input.toList()
		val width = l.first().length
		val height = l.size
		val data = l.flatMap { it.map(Char::digitToInt) }
		return Heightmap(width, height, data)
	}

	override fun partOne(data: Heightmap): Int = data.localMinima().sumOf {
		data[it]!! + 1
	}

	override fun partTwo(data: Heightmap): Int {
		val basins = data.localMinima().map {
			data.floodFillBasin(emptySet(), setOf(it)).size
		}.sortedDescending()

		return basins.take(3).reduce(Int::times)
	}

	private fun Heightmap.localMinima(): List<Point> {
		return pointValues().filter { (p, v) ->
			(p.neighbours().all { v < (get(it) ?: 9) })
		}.mapToList { it.first }
	}

	private tailrec fun Heightmap.floodFillBasin(visited: Set<Point>, open: Set<Point>): Set<Point> {
		if (open.isEmpty()) return visited

		val next = open.first()
		val newOpens: List<Point> = next.neighbours().filter {
			it in this && get(it) != 9 && it !in visited
		}.toList()

		return floodFillBasin(visited + next, open - next + newOpens)
	}
}
