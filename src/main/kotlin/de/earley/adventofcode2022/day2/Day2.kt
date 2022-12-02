package de.earley.adventofcode2022.day2

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList

fun main() = Day2.start()

object Day2 : BaseSolution<List<Day2.Round>, Int>() {

	override fun parseInput(input: Sequence<String>): List<Round> = input.mapToList {
		it.split(" ").let { (a, b) ->
			val opponent = when (a) {
				"A" -> Type.Rock
				"B" -> Type.Paper
				"C" -> Type.Scissors
				else -> error("Unknown type $a")
			}
			val strategy = when (b) {
				"X" -> Strategy.X
				"Y" -> Strategy.Y
				"Z" -> Strategy.Z
				else -> error("Unknown strategy $b")
			}
			Round(opponent, strategy)
		}
	}

	data class Round(val opponent: Type, val strategy: Strategy) {
		fun playWith(type: Type): PlayedRound = PlayedRound(opponent, type)
	}

	data class PlayedRound(val opponent: Type, val you: Type)


	enum class Type(val score: Int) {
		Rock(1), Paper(2), Scissors(3)
	}

	enum class Strategy {
		X, Y, Z
	}

	override fun partOne(data: List<Round>): Int = data.map {
		it.playWith(
			when (it.strategy) {
				Strategy.X -> Type.Rock
				Strategy.Y -> Type.Paper
				Strategy.Z -> Type.Scissors
			}
		)
	}.score()

	override fun partTwo(data: List<Round>): Int = data.map {
		it.playWith(
			when (it.strategy) {
				Strategy.X -> it.opponent.lose()
				Strategy.Y -> it.opponent.draw()
				Strategy.Z -> it.opponent.win()
			}
		)
	}.score()

	private fun List<PlayedRound>.score(): Int = sumOf(::score)

	private fun score(it: PlayedRound) = it.you.score + when (it.you) {
		it.opponent.lose() -> 0
		it.opponent.draw() -> 3
		it.opponent.win() -> 6
		else -> error("impossible: $it has to be one of win, draw or lose")
	}

	private fun Type.draw(): Type = this
	private fun Type.win(): Type = when (this) {
		Type.Rock -> Type.Paper
		Type.Paper -> Type.Scissors
		Type.Scissors -> Type.Rock
	}

	private fun Type.lose(): Type = when (this) {
		Type.Rock -> Type.Scissors
		Type.Paper -> Type.Rock
		Type.Scissors -> Type.Paper
	}

}
