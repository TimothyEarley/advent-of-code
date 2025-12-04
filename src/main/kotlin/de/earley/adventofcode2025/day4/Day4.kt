package de.earley.adventofcode2025.day4

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day4.start()

object Day4 : BaseSolution<Grid<Day4.Tile>, Int, Int>() {

	enum class Tile {
		Space, Roll
	}

	override fun parseInput(input: Sequence<String>): Grid<Tile> = input.toGrid {
		when (it) {
			'.' -> Tile.Space
			'@' -> Tile.Roll
			else -> error("Invalid input")
		}
	}

	override fun partOne(data: Grid<Tile>): Int = data.removableRolls().count()

	override fun partTwo(data: Grid<Tile>): Int {
		var count = 0
		val state = data.toMutableGrid()
		while (true) {
			val canBeRemoved = state.removableRolls().toList()
			if (canBeRemoved.isEmpty()) {
				return count
			}
			count += canBeRemoved.size
			canBeRemoved.forEach { p -> state[p] = Tile.Space }
		}
	}

	private fun Grid<Tile>.removableRolls() : Sequence<Point> = pointValues()
		.filter { (_, v) -> v == Tile.Roll }
		.filter { (p, _) -> p.neighbours(diagonal = true).count { get(it) == Tile.Roll } < 4 }
		.map { (p, _) -> p }
}
