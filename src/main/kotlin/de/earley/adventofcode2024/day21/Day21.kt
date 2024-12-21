package de.earley.adventofcode2024.day21

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.Direction
import de.earley.adventofcode.Point
import de.earley.adventofcode.generalAStarNodeLong
import de.earley.adventofcode.manhattanDistanceTo

fun main() = Day21.start()

object Day21 : BaseSolution<List<String>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Long = data.sumOf { code ->
		code.filter { it.isDigit() }.toLong() * shortestSequence(code, 2)
	}

	override fun partTwo(data: List<String>): Long = data.sumOf { code ->
		code.filter { it.isDigit() }.toLong() * shortestSequence(code, 25)
	}

	private fun shortestSequence(targetCode: String, steps: Int): Long {
		// find the distances on the first directional pad
		val directionalDistance1: Map<Pair<Directional, Directional>, Long> =
			mutableMapOf<Pair<Directional, Directional>, Long>().apply {
				Directional.entries.forEach { from ->
					Directional.entries.forEach { to ->
						// +1 for punching A
						this[from to to] = from.point.manhattanDistanceTo(to.point) + 1L
					}
				}
			}

		// find the distances on the rest of the directional pads
		// we can do this by iteratively using the distances from the one before
		data class DirectionalState(
			val directional: Directional, val pressed: Boolean, val prevInput: Directional
		)

		val directionalDistanceN: List<Map<Pair<Directional, Directional>, Long>> =
			mutableListOf<Map<Pair<Directional, Directional>, Long>>().apply {
				repeat(steps - 1) { i ->
					val prev = if (i == 0) directionalDistance1 else this.last()
					add(mutableMapOf<Pair<Directional, Directional>, Long>().apply {
						Directional.entries.forEach { from ->
							Directional.entries.forEach { to ->
								this[from to to] =
									generalAStarNodeLong(from = DirectionalState(from, false, Directional.A),
										goal = { it.pressed },
										heuristic = { it.directional.point.manhattanDistanceTo(to.point).toLong() },
										neighbours = {
											Directional.entries.asSequence().map {
												it to prev[prevInput to it]!!
											}.mapNotNull { (dir, cost) ->
												when (dir) {
													Directional.Up -> directional.move(Direction.Up)
														?.let { copy(directional = it) }

													Directional.Down -> directional.move(Direction.Down)
														?.let { copy(directional = it) }

													Directional.Left -> directional.move(Direction.Left)
														?.let { copy(directional = it) }

													Directional.Right -> directional.move(Direction.Right)
														?.let { copy(directional = it) }

													Directional.A -> if (directional != to) null else copy(pressed = true)
												}?.let { newState ->
													newState.copy(prevInput = dir) to cost
												}
											}
										}).first().cost
							}
						}
					})
				}
			}

		// find the shortest numerical sequence by using the costs from the last directional pad
		data class NumericalState(
			val numerical: Numerical, val codeLLeft: String, val lastDirectional: Directional
		)
		return generalAStarNodeLong(from = NumericalState(Numerical.A, targetCode, Directional.A),
			goal = { it.codeLLeft.isEmpty() },
			heuristic = { it.codeLLeft.length.toLong() },
			neighbours = {
				Directional.entries.asSequence().map {
					it to directionalDistanceN.last()[lastDirectional to it]!!
				}.mapNotNull { (dir, cost) ->
					when (dir) {
						Directional.Up -> numerical.move(Direction.Up)?.let { copy(numerical = it) }
						Directional.Down -> numerical.move(Direction.Down)?.let { copy(numerical = it) }
						Directional.Left -> numerical.move(Direction.Left)?.let { copy(numerical = it) }
						Directional.Right -> numerical.move(Direction.Right)?.let { copy(numerical = it) }
						Directional.A -> if (!codeLLeft.startsWith(numerical.entered)) null
						else copy(codeLLeft = codeLLeft.drop(1))
					}?.let {
						it.copy(lastDirectional = dir) to cost
					}
				}
			}).first().cost
	}

	enum class Directional(val point: Point) {
		Up(Point(1, 0)), Down(Point(1, 1)), Left(Point(0, 1)), Right(Point(2, 1)), A(Point(2, 0));

		fun move(direction: Direction): Directional? {
			val newPoint = point + direction.point
			return Directional.entries.find { it.point == newPoint }
		}
	}

	enum class Numerical(val entered: Char, val point: Point) {
		Zero('0', Point(1, 3)), One('1', Point(0, 2)), Two('2', Point(1, 2)), Three('3', Point(2, 2)), Four(
			'4',
			Point(0, 1)
		),
		Five('5', Point(1, 1)), Six('6', Point(2, 1)), Seven('7', Point(0, 0)), Eight('8', Point(1, 0)), Nine(
			'9',
			Point(2, 0)
		),
		A('A', Point(2, 3));

		fun move(direction: Direction): Numerical? {
			val newPoint = point + direction.point
			return Numerical.entries.find { it.point == newPoint }
		}
	}
}
