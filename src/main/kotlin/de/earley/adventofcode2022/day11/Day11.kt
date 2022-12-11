package de.earley.adventofcode2022.day11

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() = Day11.start()

typealias Worry = Long
typealias MonkeyId = Int

object Day11 : BaseSolution<List<Day11.Monkey>, Int, Long>() {

	override fun parseInput(input: Sequence<String>): List<Monkey> = input
		.toList()
		.split { it.isBlank() }
		.mapIndexed { index, monkeyDescription ->
			assert(monkeyDescription.size == 6)
			val (id, startString, op, test, ifTrue, ifFalse) = monkeyDescription
			assert(id == "Monkey $index:") { "id was $id should be $index" }

			val opFunction: (Worry) -> Worry =
				op.substringAfter(" Operation: new =").split(" ").filterNot { it.isBlank() }.let { (left, type, right) ->
					val typeF: Worry.(Worry) -> Worry = when (type) {
						"+" -> Long::plus
						"*" -> Long::times
						else -> error("Unknown op $type")
					}
					val leftF: (Worry) -> Worry = when (left) {
						"old" -> { old: Worry -> old }
						else -> { _: Worry -> left.toLong() }
					}
					val rightF: (Worry) -> Worry = when (right) {
						"old" -> { old: Worry -> old }
						else -> { _: Worry -> right.toLong() }
					}
					{ old -> leftF(old).typeF(rightF(old)) }
				}


			Monkey(
				startString.substringAfter("  Starting items: ").split(", ").map(String::toLong),
				opFunction,
				test.substringAfter("  Test: divisible by ").toInt(),
				ifTrue.substringAfter("    If true: throw to monkey ").toInt(),
				ifFalse.substringAfter("    If false: throw to monkey ").toInt(),
			)

		}

	override fun partOne(data: List<Monkey>): Int = run(data, true, 20).toInt()
	override fun partTwo(data: List<Monkey>): Long = run(data, false, 10000)

	private fun run(
		data: List<Monkey>,
		reduceWorry: Boolean,
		times: Int
	): Long {
		val monkeys = data.map(Monkey::toMutable).toTypedArray()

		/*
		 * Since we use the worry level only for divisibility checks, we can
		 * perform any operation that keeps this outcome the same but lowers
		 * the number, so it doesn't overflow. The most efficient such operation
		 * is dividing by the least common multiple.
		 */
		val lcm = monkeys.map(MutableMonkey::test).reduce(::lcm)

		repeat(times) {
			for (monkey in monkeys) {
				for (item in monkey.items) {
					monkey.inspectionCount++
					val opItem = monkey.op(item)
					val reducedItem = if (reduceWorry) opItem / 3 else opItem
					val modItem = reducedItem.rem(lcm)
					val index = if (modItem % monkey.test == 0L) monkey.ifTrue else monkey.ifFalse
					monkeys[index].items.add(modItem)
				}
				monkey.items.clear()
			}
		}

		return monkeys.map { it.inspectionCount }.sortedDescending().take(2).reduce(Long::times)
	}

	fun lcm(a : Int, b: Int): Int {
		if (a == 0 || b == 0) return 0
		val higher = max(abs(a), abs(b))
		val lower = min(abs(a), abs(b))
		var lcm = higher
		while (lcm % lower != 0) {
			lcm += higher
		}
		return lcm
	}

	data class Monkey(
		val items: List<Worry>,
		val op: (Worry) -> Worry,
		val test: Int,
		val ifTrue: MonkeyId,
		val ifFalse: MonkeyId,
	) {
		fun toMutable(): MutableMonkey = MutableMonkey(items.toMutableList(), op, test, ifTrue, ifFalse, 0)
	}

	data class MutableMonkey(
		val items: MutableList<Worry>,
		val op: (Worry) -> Worry,
		val test: Int,
		val ifTrue: MonkeyId,
		val ifFalse: MonkeyId,
		var inspectionCount: Long = 0
	)

}

private operator fun <E> List<E>.component6(): E = get(5)
