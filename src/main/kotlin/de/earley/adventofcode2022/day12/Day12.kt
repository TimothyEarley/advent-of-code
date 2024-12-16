package de.earley.adventofcode2022.day12

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.cache
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode.manhattanDistanceTo
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.toGrid

fun main() = Day12.start()

object Day12 : BaseSolution<Grid<Char>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Char> = input.toGrid { it }

	override fun partOne(data: Grid<Char>): Int {
		val start = data.indexOf('S')!!
		val end = data.indexOf('E')!!
		return generalAStar(
			from = start,
			goal = { it == end },
			heuristic = { it.manhattanDistanceTo(end) },
			neighbours = {
				neighbours(diagonal = false).filter {
					checkCanStep(data, this, it)
				}.map { it to 1 }
			}
		)!!
	}

	override fun partTwo(data: Grid<Char>): Int {
		val end = data.indexOf('E')!!

		val starts = data.indices.filter {
			data[it] == 'S' || data[it] == 'a'
		}.toList()

		return generalAStar(
			from = end,
			goal = { it in starts },
			heuristic = { p: Point -> starts.minOf { p.manhattanDistanceTo(it) } }.cache(),
			neighbours = {
				neighbours(diagonal = false).filter {
					checkCanStep(data, it, this)
				}.map { it to 1 }
			}
		)!!
	}

	private fun Char.height(): Int = when (this) {
		'S' -> 'a'
		'E' -> 'z'
		else -> this
	} - 'a'

	private fun checkCanStep(data: Grid<Char>, from: Point, to: Point): Boolean =
		if (from !in data || to !in data) {
			false
		} else {
			data[from]!!.height() + 1 >= data[to]!!.height()
		}
}
