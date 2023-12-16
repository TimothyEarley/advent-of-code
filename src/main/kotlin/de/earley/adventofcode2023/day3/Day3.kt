package de.earley.adventofcode2023.day3

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.neighbours
import de.earley.adventofcode.runUntil

fun main() = Day3.start()

object Day3 : BaseSolution<Grid<Char>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): Grid<Char> = input.toList().let {
		val w = it.first().length
		val h = it.size
		val a: List<Char> = it.flatMap { it.toCharArray().toList() }
		Grid(w, h, a)
	}
	override fun partOne(data: Grid<Char>): Int = data.pointValues().sumOf { (p, c) ->
		when {
			!c.isDigit() -> 0.toInt()
			// we have a digit, check if first digit in number
			data[p + Direction.Left.point]?.isDigit() == true -> 0
			// we are the start of a number
			else -> {
				var number = 0
				var p2 = p
				var adjacentToSymbol = false
				while (data[p2]?.isDigit() == true) {
					number = number * 10 + data[p2]!!.digitToInt()
					adjacentToSymbol = adjacentToSymbol || (
						p2.neighbours(diagonal = true).any {
							data[it] != null && data[it]?.isDigit() == false && data[it] != '.'
						}
						)
					p2 += Direction.Right.point
				}
				if (adjacentToSymbol) number else 0
			}
		}
	}

	override fun partTwo(data: Grid<Char>): Int = data.pointValues().sumOf { (p, c) ->
		if (c != '*') {
			0
		} else {
			// find the two adjacent numbers
			val digitNeighbours = p.neighbours(diagonal = true).filter {
				data[it]?.isDigit() == true
			}.toList()
			val uniqueDigitNeighbours = digitNeighbours.filter {
				(it + Direction.Left.point) !in digitNeighbours
			}
			if (uniqueDigitNeighbours.size != 2) {
				// not a gear, not adjacent to exactly 2
				0
			} else {
				uniqueDigitNeighbours.map {
					// go as far left and right to make number
					var number = ""
					var pGoingRight = it
					while (data[pGoingRight]?.isDigit() == true) {
						number += data[pGoingRight]?.digitToInt()
						pGoingRight += Direction.Right.point
					}
					var pGoingLeft = it + Direction.Left.point
					while (data[pGoingLeft]?.isDigit() == true) {
						number = data[pGoingLeft]?.digitToInt().toString() + number
						pGoingLeft += Direction.Left.point
					}
					number.toInt()
				}.fold(1, Int::times)
			}
		}
	}
}
