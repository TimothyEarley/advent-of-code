package de.earley.adventofcode2024.day13

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.LongPoint
import de.earley.adventofcode.split
import kotlin.math.roundToLong


fun main() = Day13.start()

object Day13 : BaseSolution<List<Day13.Input>, Long, Long>() {

	val xRegex = "X=?([+-]?\\d+)".toRegex()
	val yRegex = "Y=?([+-]?\\d+)".toRegex()

	override fun parseInput(input: Sequence<String>): List<Input> = input.toList().split { it.isBlank() }.map {
		val ax = xRegex.find(it[0])!!.groupValues[1].toLong()
		val ay = yRegex.find(it[0])!!.groupValues[1].toLong()
		val bx = xRegex.find(it[1])!!.groupValues[1].toLong()
		val by = yRegex.find(it[1])!!.groupValues[1].toLong()
		val px = xRegex.find(it[2])!!.groupValues[1].toLong()
		val py = yRegex.find(it[2])!!.groupValues[1].toLong()
		Input(LongPoint(ax, ay), LongPoint(bx, by), LongPoint(px, py))
	}

	data class Input(
		val a : LongPoint,
		val b : LongPoint,
		val prize: LongPoint
	)

	override fun partOne(data: List<Input>): Long = data.sumOf { input ->
		combination(input)
	}

	override fun partTwo(data: List<Input>): Long = data.sumOf { input ->
		combination(input.copy(prize = input.prize + 10000000000000L))
	}

	private fun combination(input: Input): Long {
		val (ax, ay) = input.a
		val (bx, by) = input.b
		val (px, py) = input.prize

		// n * ax + m * bx = px
		// n * ay + m * by = py

		val m = (ay * px - ax * py) / (ay * bx - ax * by).toDouble()
		val n = py / ay.toDouble() - m * by / ay.toDouble()

		val nRounded = n.roundToLong()
		val mRounded = m.roundToLong()

		return if (input.a * nRounded + input.b * mRounded == input.prize) {
			nRounded * 3 + mRounded
		} else 0L
	}

}

