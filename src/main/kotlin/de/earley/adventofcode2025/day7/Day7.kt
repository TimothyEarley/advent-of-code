package de.earley.adventofcode2025.day7

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.toGrid

fun main() = Day7.start()

object Day7 : BaseSolution<Grid<Day7.Tile>, Long, Long>() {

	enum class Tile {
		Start, Air, Splitter
	}

	override fun parseInput(input: Sequence<String>): Grid<Tile> = input.toGrid { when (it) {
		'S' -> Tile.Start
		'.' -> Tile.Air
		'^' -> Tile.Splitter
		else -> error("Invalid tile $it")
	} }

	override fun partOne(data: Grid<Tile>): Long {
		val start = data.pointValues().first { it.second == Tile.Start }.first
		var splits = 0L

		(0 .. data.height).fold(setOf(start.x)) { beams, y ->
			beams.flatMap { x ->
				when (data[x, y + 1]) {
					Tile.Start, Tile.Air -> listOf(x)
					Tile.Splitter -> {
						splits++
						listOf(x - 1, x + 1)
					}
					null -> emptyList()
				}
			}.toSet()
		}

		return splits
	}

	override fun partTwo(data: Grid<Tile>): Long {
		val start = data.pointValues().first { it.second == Tile.Start }.first
		val cache = mutableMapOf<Point, Long>()

		fun recurse(p : Point) : Long = cache.getOrPut(p) {
			val below = Point(p.x, p.y + 1)
			when (data[below]) {
				Tile.Start, Tile.Air -> recurse(below)
				Tile.Splitter -> recurse(Point(p.x - 1, p.y + 1)) + recurse(Point(p.x + 1, p.y + 1))
				null -> 1
			}
		}

		return recurse(Point(start.x, 0))
	}

}
