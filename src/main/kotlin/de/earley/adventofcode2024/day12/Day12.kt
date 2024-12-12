package de.earley.adventofcode2024.day12

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.floodFill
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day12.start()

object Day12 : BaseSolution<Grid<Char>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Char> = input.toGrid { it }

	override fun partOne(data: Grid<Char>): Long = sumRegions(data) {
		it.size * it.perimeter()
	}
	override fun partTwo(data: Grid<Char>): Long = sumRegions(data) {
		it.size * it.sides()
	}

	private fun sumRegions(grid: Grid<Char>, value: (Set<Point>) -> Long): Long {
		val done = grid.map { false }.toMutableGrid()

		var result = 0L
		for (p in grid.indices) {
			if (done[p] == true) continue
			val region = floodFill(grid, p)
			region.forEach {
				done[it] = true
			}
			result += value(region)
		}

		return result
	}

	private fun floodFill(grid: Grid<Char>, from: Point): Set<Point> {
		val type = grid[from]!!
		return grid.floodFill(from) { it == type }
	}

	private fun Set<Point>.perimeter(): Long = sumOf { p ->
		p.neighbours(false).count {
			it !in this
		}.toLong()
	}

	private fun Set<Point>.sides(): Long = sumOf { p ->
		Direction.entries.count { dir ->
			// is at edge and not left/up neighbour was on same edge
			(p + dir.point) !in this && !when (dir) {
				Direction.Left -> (p + Direction.Up.point) in this && (p + Point(-1, -1) !in this)
				Direction.Right -> (p + Direction.Up.point) in this && (p + Point(+1, -1) !in this)
				Direction.Up -> (p + Direction.Left.point) in this && (p + Point(-1, -1) !in this)
				Direction.Down -> (p + Direction.Left.point) in this && (p + Point(-1, +1) !in this)
			}
		}.toLong()
	}

}
