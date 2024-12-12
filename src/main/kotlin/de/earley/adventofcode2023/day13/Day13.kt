package de.earley.adventofcode2023.day13

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.split
import de.earley.adventofcode.toGrid

fun main() = Day13.start()

object Day13 : BaseSolution<List<Grid<Boolean>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Grid<Boolean>> = input.toList().split {
		it.isBlank()
	}.map { pattern ->
		pattern.toGrid { it == '#' }
	}

	override fun partOne(data: List<Grid<Boolean>>): Long = data.sumOf { grid ->
		reflectionValue(grid, false)
	}

	private fun reflectionValue(grid: Grid<Boolean>, smudge: Boolean): Long {
		val reflectVertical = (0..<grid.width - 1).find { x ->
			reflectsAlong(x, grid.width, grid::getColumn, smudge)
		}

		return if (reflectVertical != null) {
			reflectVertical + 1L
		} else {
			val reflectHorizontal = (0..<grid.height - 1).find { y ->
				reflectsAlong(y, grid.height, grid::getRow, smudge)
			} ?: error("No reflection found")

			100L * (reflectHorizontal + 1)
		}
	}

	private fun <T> reflectsAlong(i: Int, total: Int, getColOrRow: (Int) -> List<T>, smudge: Boolean): Boolean {
		val leftOrUp = 0..i
		val rightOrDown = (i + 1) ..< total

		var smudged = !smudge
		val isReflection = leftOrUp.reversed().zip(rightOrDown).all { (i0, i1) ->
			when (getColOrRow(i0).zip(getColOrRow(i1)).count { it.first != it.second }) {
				0 -> true
				1 -> if (smudged) {
					false
				} else {
					smudged = true
					true
				}
				else -> false
			}
		}
		return isReflection && smudged
	}

	override fun partTwo(data: List<Grid<Boolean>>): Long = data.sumOf { grid ->
		reflectionValue(grid, true)
	}
}
