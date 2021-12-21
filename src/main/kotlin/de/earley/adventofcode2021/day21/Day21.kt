package de.earley.adventofcode2021.day21

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.modStart1

fun main() = Day21.start()

object Day21 : BaseSolution<State, Long>() {

	override fun parseInput(input: Sequence<String>): State = input.toList().let { (one, two) ->
		State(
			Player.Player1,
			one.removePrefix("Player 1 starting position: ").toInt(),
			two.removePrefix("Player 2 starting position: ").toInt(),
			0,
			0
		)
	}

	override fun partOne(data: State): Long {
		val die = DeterministicDie()
		var state = data
		while (state.player1Score < 1000 && state.player2Score < 1000) {
			state = state.nextState(die.roll(3))
		}
		return (minOf(state.player1Score, state.player2Score)) * die.rollCount().toLong()
	}

	override fun partTwo(data: State): Long {
		val (player1Win, player2Win) = data.playDiracDie()
		return maxOf(player1Win, player2Win)
	}

	/**
	 * Count the winning universes of player 1 and of player 2
	 */
	private fun State.playDiracDie(): Pair<Long, Long> {
		if (player1Score >= 21) return 1L to 0
		if (player2Score >= 21) return 0L to 1

		// total of 3*3*3 = 27 options
		return listOf(
			// 3 (1+1+1)
			1 * nextState(3).playDiracDie(),
			// 4 (1+1+2)
			3 * nextState(4).playDiracDie(),
			// 5 (1+1+3, 1+2+2)
			6 * nextState(5).playDiracDie(),
			// 6 (1+2+3, 2+2+2)
			7 * nextState(6).playDiracDie(),
			// 7 (3+2+2, 1+3+3)
			6 * nextState(7).playDiracDie(),
			// (3+3+2) 8
			3 * nextState(8).playDiracDie(),
			// 9 (3+3+3)
			1 * nextState(9).playDiracDie(),
		).reduce { acc, pair -> acc.first + pair.first to acc.second + pair.second }
	}
}

private operator fun Int.times(p: Pair<Long, Long>): Pair<Long, Long> = this * p.first to this * p.second

enum class Player {
	Player1, Player2
}

data class State(
	val turn: Player,
	val player1: Int,
	val player2: Int,
	val player1Score: Int,
	val player2Score: Int,
)

private fun State.nextState(thrown: Int): State = when (turn) {
	Player.Player1 -> {
		val landOn = (player1 + thrown) modStart1 10
		State(Player.Player2, landOn, player2, player1Score + landOn, player2Score)
	}
	Player.Player2 -> {
		val landOn = (player2 + thrown) modStart1 10
		State(Player.Player1, player1, landOn, player1Score, player2Score + landOn)
	}
}

class DeterministicDie {
	private var rolls: Int = 0

	fun roll(): Int = (rolls++ % 100) + 1
	fun rollCount(): Int = rolls
}

fun DeterministicDie.roll(n: Int): Int = (1..n).sumOf { roll() }
