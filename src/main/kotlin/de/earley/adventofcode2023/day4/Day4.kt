package de.earley.adventofcode2023.day4

import de.earley.adventofcode.BaseSolution
import kotlin.math.pow

fun main() = Day4.start()

object Day4 : BaseSolution<List<Day4.ScratchCard>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<ScratchCard> = input.mapIndexed { index, s ->
		val s2 = s.split(":")[1]
		val (winning, have) = s2.split("|", limit = 2)
		ScratchCard(
			winning.split(" ").map { it.trim() }.filter { it.isNotBlank() }.map { it.toInt() },
			have.split(" ").map { it.trim() }.filter { it.isNotBlank() }.map { it.toInt() },
		)
	}.toList()

	data class ScratchCard(val winning: List<Int>, val have: List<Int>) {
		val correct: Int = have.count { it in winning }
	}

	override fun partOne(data: List<ScratchCard>): Int = data.sumOf { card ->
		2.0.pow((card.correct - 1)).toInt()
	}

	override fun partTwo(data: List<ScratchCard>): Int = data.foldIndexed(data.map { 1 }) { index, countSoFar, scratchCard ->
		val myCount = countSoFar[index]
		countSoFar.mapIndexed { i, v ->
			if (index + 1 <= i && i <= index + scratchCard.correct) {
				v + myCount
			} else {
				v
			}
		}
	}.sum()
}
