package de.earley.adventofcode2024.day3

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.readResource

fun main() = Day3.start()

sealed interface Instruction
data class Mul(val x: Int, val y: Int) : Instruction
object Do : Instruction
object Dont : Instruction

object Day3 : BaseSolution<List<Instruction>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Instruction> = input
		.joinToString("\n").let { line ->
		val regex = "(?:mul\\((\\d{1,3}),(\\d{1,3})\\))|(?:don't\\(\\))|(?:do\\(\\))".toRegex()
		regex.findAll(line).mapToList { r ->
			if (r.value.startsWith("m")) {
				val (x, y) = r.destructured
				Mul(x.toInt(), y.toInt())
			} else if (r.value.startsWith("don't")) {
				Dont
			} else if (r.value.startsWith("do(")) {
				Do
			} else error("Err")
		}
	}

	override fun partOne(data: List<Instruction>): Long = data.sumOf {
		when (it) {
			is Mul -> it.x * it.y.toLong()
			Do -> 0
			Dont -> 0
		}
	}

	override fun partTwo(data: List<Instruction>): Long = data
		.also { println(it) }
		.fold(
		0L to true
	) { (r, enable) , ins->
		when (ins) {
			Do -> r to true
			Dont -> r to false
			is Mul -> if (enable) (r + ins.x * ins.y) to enable else r to enable
		}
	}.first

}
