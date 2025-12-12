package de.earley.adventofcode2025.day12

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Grid
import de.earley.adventofcode.MutableGrid
import de.earley.adventofcode.grid
import de.earley.adventofcode.split
import de.earley.adventofcode.toGrid
import de.earley.adventofcode.toMutableGrid

fun main() = Day12.start()

object Day12 : BaseSolution<Day12.Input, Int, Long>() {

	data class Input(
		val gifts: List<Shape>,
		val regions: List<Region>
	)

	data class Region(
		val width: Int,
		val length: Int,
		val count: List<Int>
	)

	data class Shape(
		val data: Grid<Boolean>
	) {
		val variants: List<Grid<Boolean>> = buildList {
			// the shapes are all square
			assert(data.width == data.height)

			add(data)
			add(grid(data.width, data.height) { p ->
				data[p.y, p.x]!!
			})
			add(grid(data.width, data.height) { p ->
				data[data.width - p.x - 1, data.height - p.y - 1]!!
			})
			add(grid(data.width, data.height) { p ->
				data[data.width - p.y - 1, data.height - p.x - 1]!!
			})

			// flip x
			add(grid(data.width, data.height) { p ->
				data[data.width - p.x - 1, p.y]!!
			})
			// flip y
			add(grid(data.width, data.height) { p ->
				data[p.x, data.height - p.y - 1]!!
			})

		}
	}

	override fun parseInput(input: Sequence<String>): Input = input.toList()
		.split { it.isBlank() }
		.let { sections ->
			Input(
				gifts = sections.dropLast(1).map { gift ->
					Shape(gift.drop(1).toGrid {
						when (it) {
							'#' -> true
							'.' -> false
							else -> error("Invalid!")
						}
					})
				},
				regions = sections.last().map { line ->
					Region(
						line.substringBefore('x').toInt(),
						line.substringAfter('x').substringBefore(':').toInt(),
						line.substringAfter(' ').split(' ').map { it.toInt() })
				}
			)
		}

	override fun partOne(data: Input): Int = data.regions.count { region ->
		val regionGrid = grid(region.width, region.length) { false }.toMutableGrid()
		val availableTiles = region.width * region.length
		val tiles = region.count.mapIndexed { i, v -> v * data.gifts[i].data.pointValues().count { it.second } }.sum()
		availableTiles >= tiles && canPlace(data.gifts, region.count.toIntArray(), regionGrid)
	}

	private fun canPlace(shapes: List<Shape>, shapesToPlace: IntArray, region: MutableGrid<Boolean>): Boolean {
		val shapeIndexToPlace = shapesToPlace.indexOfFirst { it > 0 }
		if (shapeIndexToPlace == -1) return true

		val current = shapes[shapeIndexToPlace]
		shapesToPlace[shapeIndexToPlace]--

		val result =
			region.pointValues()
				.filter { (_, v) -> !v }
				.any { (p, _) ->
					current.variants.any { shapeToPlace ->
						val pointsToPlace = shapeToPlace.pointValues()
							.filter { (_, v) -> v }
							.map {
								it.first + p
							}
						val canPlace = pointsToPlace.none { region[it] ?: true }
						if (canPlace) {
							pointsToPlace.forEach { point -> region[point] = true }
							val result = canPlace(shapes, shapesToPlace, region)
							pointsToPlace.forEach { point -> region[point] = false }
							result
						} else false
					}
				}

		shapesToPlace[shapeIndexToPlace]++
		return result
	}

	override fun partTwo(data: Input): Long = 0

}
