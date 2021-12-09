package de.earley.adventofcode2021.day4

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.Grid
import de.earley.adventofcode2021.MutableGrid
import de.earley.adventofcode2021.toMutableGrid

fun main() = Day4.start()

data class State(val numbers: List<Int>, val boards: List<Board>)

typealias Board = Grid<Int>
typealias MutableBingoBoard = MutableGrid<Int?>

fun MutableBingoBoard.update(number: Int): Boolean {

	val pos = indexOf(number) ?: return false

	this[pos] = null

	// check row
	var rowCheck = true
	for (x2 in 0..4) {
		if (this[x2, pos.y] != null) {
			rowCheck = false
			break
		}
	}
	if (rowCheck) return true

	// check col
	var colCheck = true
	for (y2 in 0..4) {
		if (this[pos.x, y2] != null) {
			colCheck = false
			break
		}
	}

	return colCheck
}

fun MutableBingoBoard.sumNumbersLeft(): Int = values().filterNotNull().sum()

object Day4 : BaseSolution<State, Int>() {

	override fun parseInput(input: Sequence<String>): State = input.toList().let {
		State(
			numbers = it.first().split(',').map(String::toInt),
			boards = it.drop(1).chunked(6).map { b ->
				Board(
					5,
					5,
					b.drop(1).joinToString(" ")
						.split(" ").filter(String::isNotBlank).map(String::toInt)
				)
			}
		)
	}

	override fun partOne(data: State): Int {
		val boards: List<MutableBingoBoard> = data.boards.map(Board::toMutableGrid)

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
		val boards = data.boards.map<Board, MutableBingoBoard>(Board::toMutableGrid).toMutableList()

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
