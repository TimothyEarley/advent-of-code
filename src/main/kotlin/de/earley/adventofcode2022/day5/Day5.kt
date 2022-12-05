package de.earley.adventofcode2022.day5

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split

fun main() = Day5.start()

typealias Stack = List<Char>

object Day5 : BaseSolution<Day5.State, String>() {

	override fun parseInput(input: Sequence<String>): State = input.toList().split(String::isBlank).let { (conf, ins) ->
		val cols = conf.last().split(" ").last { it.isNotBlank() }.toInt()

		val config = Array<MutableList<Char>>(cols) { mutableListOf() }
		conf.dropLast(1).forEach { line ->
			for (i in (0 until cols)) {
				val item = line.getOrNull(i * 4 + 1)
				if (item != null && !item.isWhitespace()) config[i].add(item)
			}
		}


		State(Config(config.toList()), ins.map {
			val r = Regex("move (\\d+) from (\\d+) to (\\d+)")
			val (count, from, to) = r.matchEntire(it)!!.destructured
			Instruction(count.toInt(), from.toInt(), to.toInt())
		})
	}

	override fun partOne(data: State): String = data.runInstructions { config, instruction ->
		(1..instruction.count).fold(config) { acc, _ ->
			val mut = acc.stacks.map { it.toMutableList() }
			val moved = mut[instruction.from - 1].removeFirst()
			mut[instruction.to - 1].add(0, moved)
			Config(mut)
		}
	}.topCrates()

	override fun partTwo(data: State): String = data.runInstructions { config, instruction ->
		val mut = config.stacks.map { it.toMutableList() }
		val moved = mut[instruction.from - 1].take(instruction.count)
		repeat(instruction.count) { mut[instruction.from - 1].removeFirst() }
		mut[instruction.to - 1].addAll(0, moved)
		Config(mut)
	}.topCrates()


	private fun State.runInstructions(update: (Config, Instruction) -> Config): Config =
		instructions.fold(config, update)

	data class Config(val stacks: List<Stack>)
	data class Instruction(val count: Int, val from: Int, val to: Int)
	data class State(val config: Config, val instructions: List<Instruction>)


	private fun Config.topCrates() = stacks.map { it.first() }.joinToString("")
}
