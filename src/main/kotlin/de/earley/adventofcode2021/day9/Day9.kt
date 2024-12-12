package de.earley.adventofcode2021.day9

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.floodFill
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.neighbours

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
			data.floodFillBasin(it).size
		}.sortedDescending()

		return basins.take(3).reduce(Int::times)
	}

	private fun Heightmap.localMinima(): List<Point> {
		return pointValues().filter { (p, v) ->
			(p.neighbours().all { v < (get(it) ?: 9) })
		}.mapToList { it.first }
	}

	private fun Heightmap.floodFillBasin(from: Point): Set<Point> {
		return floodFill(from) { it != 9 }
	}
}
