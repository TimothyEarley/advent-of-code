package de.earley.adventofcode2023.day1

import de.earley.adventofcode.BaseSolution

fun main() = Day1.start()

object Day1 : BaseSolution<List<String>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int = data.sumOf {
		val s = it.filter(Char::isDigit)
		val a = s.first().toString()
		val b = s.last().toString()
		(a + b).toInt()
	}

	private val numbers = listOf(
		"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
	)

	override fun partTwo(data: List<String>): Int = data.map { line ->
		numbers.foldIndexed(line) { i, acc, n ->
			acc.replace(n, "${n}${i + 1}$n")
		}
	}.let(::partOne)
}
