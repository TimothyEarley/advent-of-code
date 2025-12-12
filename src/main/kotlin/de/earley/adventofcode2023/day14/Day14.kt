package de.earley.adventofcode2023.day14

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.MutableGrid
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid
import de.earley.adventofcode2023.day14.Day14.Type.Empty
import de.earley.adventofcode2023.day14.Day14.Type.MovingRock

fun main() = Day14.start()

object Day14 : BaseSolution<Grid<Day14.Type>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Type> = input.toGrid { c ->
		Type.entries.find { it.symbol == c }!!
	}

	enum class Type(val symbol: Char) {
		MovingRock('O'),
		FixedRock('#'),
		Empty('.'),
	}

	override fun partOne(data: Grid<Type>): Long {
		return data.toMutableGrid().tiltNorth().sumLoad()
	}

	private fun MutableGrid<Type>.tiltNorth(): MutableGrid<Type> {
		for (y in 0 ..< height) {
			for (x in 0 ..< width) {
				if (this[x, y] == MovingRock) {
					// move rock up as far as possible
					var yTarget = y
					this[x, y] = Empty
					while (yTarget > 0 && this[x, yTarget - 1] == Empty) {
						yTarget--
					}
					this[x, yTarget] = MovingRock
				}
			}
		}
		return this
	}
	private fun MutableGrid<Type>.tiltWest(): MutableGrid<Type> {
		for (x in 0 ..< width) {
			for (y in 0 ..< height) {
				if (this[x, y] == MovingRock) {
					// move rock left as far as possible
					var xTarget = x
					this[x, y] = Empty
					while (xTarget > 0 && this[xTarget - 1, y] == Empty) {
						xTarget--
					}
					this[xTarget, y] = MovingRock
				}
			}
		}
		return this
	}
	private fun MutableGrid<Type>.tiltSouth(): MutableGrid<Type> {
		for (y in height - 1 downTo 0) {
			for (x in 0 ..< width) {
				if (this[x, y] == MovingRock) {
					// move rock down as far as possible
					var yTarget = y
					this[x, y] = Empty
					while (yTarget < height - 1 && this[x, yTarget + 1] == Empty) {
						yTarget++
					}
					this[x, yTarget] = MovingRock
				}
			}
		}
		return this
	}
	private fun MutableGrid<Type>.tiltEast(): MutableGrid<Type> {
		for (x in width - 1 downTo 0) {
			for (y in 0 ..< height) {
				if (this[x, y] == MovingRock) {
					// move rock right as far as possible
					var xTarget = x
					this[x, y] = Empty
					while (xTarget < width - 1 && this[xTarget + 1, y] == Empty) {
						xTarget++
					}
					this[xTarget, y] = MovingRock
				}
			}
		}
		return this
	}

	private fun Grid<Type>.sumLoad(): Long = pointValues()
		.filter { it.value == MovingRock }
		.sumOf { height - it.point.y }
		.toLong()

	private fun MutableGrid<Type>.cycle(): MutableGrid<Type> = tiltNorth().tiltWest().tiltSouth().tiltEast()

	override fun partTwo(data: Grid<Type>): Long {
		val g = data.toMutableGrid()
		val cache = mutableMapOf<String, Int>()
		var i = 0
		val target = 1000000000
		var loopEnd = false
		while (i < target) {
			i++
			g.cycle()
			if (loopEnd) continue
			val hash = g.values().joinToString { it.symbol.toString() }
			if (cache.contains(hash)) {
				val loopLength = i - cache[hash]!!
				// go as far as possible to the target
				val remaining = target - i
				val leaving = remaining.mod(loopLength)
				i = target - leaving
				loopEnd = true
			}
			cache[hash] = i
		}

		return g.sumLoad()
	}
}
