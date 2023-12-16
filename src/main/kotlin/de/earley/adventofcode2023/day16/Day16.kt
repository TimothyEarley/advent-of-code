package de.earley.adventofcode2023.day16

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Grid
import de.earley.adventofcode.Point
import de.earley.adventofcode.runUntil
import de.earley.adventofcode.toMutableGrid

fun main() = Day16.start()

object Day16 : BaseSolution<Grid<Day16.Tile>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Grid<Tile> = input.toList().let {  lines ->
		Grid(lines.first().length, lines.size, lines.flatMap { line -> line.map { c  -> Tile.entries.find { it.symbol == c }!! } })
	}

	enum class Tile(val symbol: Char) {
		Empty('.'), MirrorSlash('/'), MirrorBack('\\'), SpliiterVertical('|'), SPlitterHorizontal('-')
	}
	data class Beam(val p: Point, val d: Direction)

	override fun partOne(data: Grid<Tile>): Long {
		return energizedTiles(data, Beam(Point(-1, 0), Direction.Right))
	}

	private fun energizedTiles(data: Grid<Tile>, start: Beam): Long {
		val cache = data.map { false to emptySet<Beam>() }.toMutableGrid()

		runUntil(listOf(start), { it.isEmpty() }) {
			it.flatMap { beam ->
				val next = beam.p + beam.d.point

				when (data[next]) {
					Tile.Empty -> listOf(beam.copy(p = next))
					Tile.MirrorSlash -> listOf(
						when (beam.d) {
							Direction.Left -> beam.copy(p = next, d = Direction.Down)
							Direction.Right -> beam.copy(p = next, d = Direction.Up)F
							Direction.Up -> beam.copy(p = next, d = Direction.Right)
							Direction.Down -> beam.copy(p = next, d = Direction.Left)
						}
					)

					Tile.MirrorBack -> listOf(
						when (beam.d) {
							Direction.Left -> beam.copy(p = next, d = Direction.Up)
							Direction.Right -> beam.copy(p = next, d = Direction.Down)
							Direction.Up -> beam.copy(p = next, d = Direction.Left)
							Direction.Down -> beam.copy(p = next, d = Direction.Right)
						}
					)

					Tile.SpliiterVertical -> when (beam.d) {
						Direction.Left, Direction.Right -> listOf(
							beam.copy(p = next, d = Direction.Up),
							beam.copy(p = next, d = Direction.Down)
						)

						Direction.Up, Direction.Down -> listOf(beam.copy(p = next))
					}

					Tile.SPlitterHorizontal -> when (beam.d) {
						Direction.Left, Direction.Right -> listOf(beam.copy(p = next))
						Direction.Up, Direction.Down -> listOf(
							beam.copy(p = next, d = Direction.Left),
							beam.copy(p = next, d = Direction.Right)
						)
					}

					null -> emptyList()
				}
			}.filterNot { beam ->
				cache[beam.p]!!.second.any { it == beam }
			}.onEach { beam ->
				cache[beam.p] = true to (cache[beam.p]!!.second + beam)
			}
		}

		return cache.values().count { it.first }.toLong()
	}

	override fun partTwo(data: Grid<Tile>): Long = sequence {
		// left edge
		(0..<data.height).forEach { y ->
			yield(Beam(Point(-1, y), Direction.Right))
		}
		// top edge
		(0..<data.width).forEach { x ->
			yield(Beam(Point(x, -1), Direction.Down))
		}
		// right edge
		(0..<data.height).forEach { y ->
			yield(Beam(Point(data.width, y), Direction.Left))
		}
		// bottom edge
		(0..<data.width).forEach { x ->
			yield(Beam(Point(x, data.height), Direction.Up))
		}
	}.maxOf { energizedTiles(data, it) }

}
