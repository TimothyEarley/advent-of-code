package de.earley.adventofcode2023.day8

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.lcm

fun main() = Day8.start()

object Day8 : BaseSolution<Day8.Data, Int, Long>() {

	override fun parseInput(input: Sequence<String>): Data = input.toList().let { list ->
		val ins = list.first().map {
			when (it) {
				'L' -> LeftRight.Left
				'R' -> LeftRight.Right
				else -> error("")
			}
		}
		val map = list.drop(2).associate { line ->
			val (from, to) = line.split(" = ", limit = 2)
			val (l, r) = to.removePrefix("(").removeSuffix(")").split(", ")
			from to (l to r)
		}
		Data(ins, map)
	}

	data class Data(
		val instructions: List<LeftRight>,
		val map: Map<String, Pair<String, String>>,
	)
	enum class LeftRight {
		Left, Right
	}

	override fun partOne(data: Data): Int = "AAA".cyclesTo(0, data) { it == "ZZZ" }

	private tailrec fun String.cyclesTo(i: Int, data: Data, cond: (String) -> Boolean): Int =
		if (cond(this)) {
			i
		} else {
			next(i, data).cyclesTo(i + 1, data, cond)
		}

	private fun String.next(i: Int, data: Data): String = when (data.instructions[i % data.instructions.size]) {
		LeftRight.Left -> data.map[this]!!.first
		LeftRight.Right -> data.map[this]!!.second
	}

	override fun partTwo(data: Data): Long {
		val start = data.map.keys.filter { it.endsWith("A") }.toSet()
		// each goes through a sequence of Zs at certain intervals
		// since there is a finite amount of Zs, it must repeat at some point
		// MASSIVE assumption: each reaches exactly one ...Z and the A-Z cyle and Z-Z cycles have the same length
		val reachedAfter = start.map {
			it.cyclesTo(0, data) { c -> c.endsWith("Z") }
				.toLong()
		}
		return reachedAfter.reduce(::lcm)
	}
}
