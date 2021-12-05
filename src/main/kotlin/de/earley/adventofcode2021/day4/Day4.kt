package de.earley.adventofcode2021.day4

import de.earley.adventofcode2021.BaseSolution

fun main() = Day4.start()

data class State(val numbers: List<Int>, val boards: List<Board>)

data class Board(val grid: List<Int>) // 5 x 5

data class MutableBingoBoard(private val grid: MutableList<Int?>) {
	constructor(board: Board) : this(board.grid.toMutableList())

	fun update(number: Int): Boolean {
		val pos = grid.indexOf(number)
		if (pos == -1) return false

		grid[pos] = null

		// pos = x + y * width
		// y = p
		val y = pos.floorDiv(5)
		val x = pos.mod(5)

		// check row
		var rowCheck = true
		for (x2 in 0..4) {
			if (grid[x2 + y * 5] != null) {
				rowCheck = false
				break
			}
		}
		if (rowCheck) return true

		// check col
		var colCheck = true
		for (y2 in 0..4) {
			if (grid[x + y2 * 5] != null) {
				colCheck = false
				break
			}
		}

		return colCheck
	}

	fun sumNumbersLeft(): Int = grid.filterNotNull().sum()
}

object Day4 : BaseSolution<State>() {

	override fun parseInput(input: Sequence<String>): State = input.toList().let {
		State(
			numbers = it.first().split(',').map(String::toInt),
			boards = it.drop(1).chunked(6).map { b ->
				Board(b.drop(1).joinToString(" ").split(" ").filter { it.isNotBlank() }.map { it.toInt() })
			}
		)
	}

	override fun partOne(data: State): Int {
		val boards = data.boards.map(::MutableBingoBoard)

		for (number in data.numbers) {
			for (board in boards) {
				if (board.update(number)) {
					// we have a winner
					return board.sumNumbersLeft() * number
				}
			}
		}

		return -1
	}

	override fun partTwo(data: State): Int {
		val boards = data.boards.map(::MutableBingoBoard).toMutableList()

		for (number in data.numbers) {
			val iter = boards.iterator()
			while (iter.hasNext()) {
				val board = iter.next()
				if (board.update(number)) {
					// check if it is last one
					if (boards.size == 1) {
						// we pick this one
						return board.sumNumbersLeft() * number
					} else {
						// remove it
						iter.remove()
					}
				}
			}
		}

		return -1
	}
}
