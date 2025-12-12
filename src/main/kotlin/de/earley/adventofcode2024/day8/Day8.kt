package de.earley.adventofcode2024.day8

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.runUntil
import de.earley.adventofcode.toGrid

fun main() = Day8.start()

object Day8 : BaseSolution<Grid<Char?>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Char?> = input.toGrid { char ->
		when (char) {
			'.' -> null
			else -> char
		}
	}

	override fun partOne(data: Grid<Char?>): Long {
		val antiNodes = mutableSetOf<Point>()

		data.pointValues()
			.filter { it.value != null }
			.forEach { (p1, char) ->
				// find others with char
				data.pointValues().filter { it.point != p1 && it.value == char }
					.forEach { (p2, _) ->
						val d = p2 - p1
						val antiOne = p1 - d
						val antiTwo = p2 + d
						if (data.contains(antiOne)) {
							antiNodes += antiOne
						}
						if (data.contains(antiTwo)) antiNodes += antiTwo
					}
			}

		return antiNodes.size.toLong()
	}

	override fun partTwo(data: Grid<Char?>): Long {
		val antiNodes = mutableSetOf<Point>()

		data.pointValues()
			.filter { it.value != null }
			.forEach { (p1, char) ->
				// find others with char
				data.pointValues().filter { it.point != p1 && it.value == char }
					.forEach { (p2, _) ->
						val d = p2 - p1
						runUntil(p1, { !data.contains(it) }) {
							antiNodes.add(it)
							it - d
						}
						runUntil(p2, { !data.contains(it) }) {
							antiNodes.add(it)
							it + d
						}

					}
			}
		return antiNodes.size.toLong()
	}

}
