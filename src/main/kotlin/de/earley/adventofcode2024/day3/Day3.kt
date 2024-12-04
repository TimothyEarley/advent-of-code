package de.earley.adventofcode2024.day3

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day3.start()

object Day3 : BaseSolution<List<Day3.Instruction>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Instruction> = input.joinToString("\n").let { line ->
		val regex = "(?:mul\\((\\d{1,3}),(\\d{1,3})\\))|(?:don't\\(\\))|(?:do\\(\\))".toRegex()
		regex.findAll(line).mapToList { match ->
			when (val f = match.value.substringBefore('(')) {
				"mul" -> {
					val (x, y) = match.destructured
					Mul(x.toInt(), y.toInt())
				}

				"do" -> Do
				"don't" -> Dont
				else -> error("Unknown function $f")
			}
		}
	}

	sealed interface Instruction
	data class Mul(val x: Int, val y: Int) : Instruction {
		val result: Long = x * y.toLong()
	}

	data object Do : Instruction
	data object Dont : Instruction

	override fun partOne(data: List<Instruction>): Long = data.sumOf {
		when (it) {
			is Mul -> it.result
			else -> 0
		}
	}

	override fun partTwo(data: List<Instruction>): Long = data
		.fold(0L to true) { (r, enable), ins ->
			when (ins) {
				Do -> r to true
				Dont -> r to false
				is Mul -> if (enable) (r + ins.result) to true else r to false
			}
		}.first
}
