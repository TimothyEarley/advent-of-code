package de.earley.adventofcode2021.day18

import de.earley.adventofcode2021.BaseSolution
import de.earley.adventofcode2021.mapToList
import kotlin.math.ceil

fun main() = Day18.start()

object Day18 : BaseSolution<List<SnailfishNumber>, Int>() {

	override fun parseInput(input: Sequence<String>): List<SnailfishNumber> = input.mapToList {
		Parser(it).parse()
	}

	override fun partOne(data: List<SnailfishNumber>): Int =
		data.reduce { acc, snailfishNumber -> acc + snailfishNumber }
			.magnitude()

	override fun partTwo(data: List<SnailfishNumber>): Int = data.maxOf { a ->
		data.maxOf { b ->
			if (a == b) Int.MIN_VALUE
			(a + b).magnitude()
		}
	}
}

private class Parser(private val input: String) {
	var index = 0

	fun parse(): SnailfishNumber = when (val c = input[index]) {
		'[' -> {
			index++
			val left = parse()
			require(input[index++] == ',')
			val right = parse()
			require(input[index++] == ']')
			PairNumber(left, right)
		}

		else -> {
			// parse number
			require(c.isDigit())
			index++
			RegularNumber(c.digitToInt())
		}
	}
}

sealed interface SnailfishNumber

private class PairNumber(
	var left: SnailfishNumber,
	var right: SnailfishNumber
) : SnailfishNumber {
	override fun toString(): String = "[$left,$right]"
}

private class RegularNumber(
	var value: Int
) : SnailfishNumber {
	override fun toString(): String = value.toString()
}

private operator fun SnailfishNumber.plus(other: SnailfishNumber): SnailfishNumber = PairNumber(this, other).reduce()

private fun SnailfishNumber.reduce(): SnailfishNumber {
	var result = this.deepCopy() // ugh, var and mutable type
	while (true) {
		// check if explodes
		val nested = result.findNested(4)
		if (nested != null) {
			val exploding = nested.last()
			val left = exploding.left as RegularNumber
			val right = exploding.right as RegularNumber

			// add to left
			if (nested[3].right == exploding) {
				nested[3].left.findRegularNumberToRight().value += left.value
			} else if (nested[2].right == nested[3]) {
				nested[2].left.findRegularNumberToRight().value += left.value
			} else if (nested[1].right == nested[2]) {
				nested[1].left.findRegularNumberToRight().value += left.value
			} else if (nested[0].right == nested[1]) nested[0].left.findRegularNumberToRight().value += left.value

			// add to right
			if (nested[3].left == exploding) {
				nested[3].right.findRegularNumberToLeft().value += right.value
			} else if (nested[2].left == nested[3]) {
				nested[2].right.findRegularNumberToLeft().value += right.value
			} else if (nested[1].left == nested[2]) {
				nested[1].right.findRegularNumberToLeft().value += right.value
			} else if (nested[0].left == nested[1]) nested[0].right.findRegularNumberToLeft().value += right.value

			// replace with 0
			if (nested[3].left == exploding) {
				nested[3].left = RegularNumber(0)
			} else {
				nested[3].right = RegularNumber(0)
			}

			continue
		}

		// check if splits
		val tenOrGreater = result.findTenOrGreater()
		if (tenOrGreater != null) {
			val (parent, splitter) = tenOrGreater

			val replace = PairNumber(
				RegularNumber(splitter.value / 2),
				RegularNumber(ceil(splitter.value / 2f).toInt())
			)

			if (parent == null) {
				result = replace
			} else if (parent.left == splitter) {
				parent.left = replace
			} else {
				parent.right = replace
			}

			continue
		}

		return result
	}
}

private fun SnailfishNumber.deepCopy(): SnailfishNumber = when (this) {
	is PairNumber -> PairNumber(left.deepCopy(), right.deepCopy())
	is RegularNumber -> RegularNumber(value)
}

private fun SnailfishNumber.findTenOrGreater(parent: PairNumber? = null): Pair<PairNumber?, RegularNumber>? =
	when (this) {
		is PairNumber -> left.findTenOrGreater(this) ?: right.findTenOrGreater(this)
		is RegularNumber ->
			if (value >= 10) {
				parent to this
			} else {
				null
			}
	}

private fun SnailfishNumber.findRegularNumberToLeft(): RegularNumber = when (this) {
	is PairNumber -> left.findRegularNumberToLeft()
	is RegularNumber -> this
}

private fun SnailfishNumber.findRegularNumberToRight(): RegularNumber = when (this) {
	is PairNumber -> right.findRegularNumberToRight()
	is RegularNumber -> this
}

private fun SnailfishNumber.findNested(i: Int): List<PairNumber>? = when (this) {
	is PairNumber -> {
		if (i == 0) {
			listOf(this)
		} else {
			(left.findNested(i - 1) ?: right.findNested(i - 1))?.let {
				listOf(this) + it
			}
		}
	}

	is RegularNumber -> null
}

private fun SnailfishNumber.magnitude(): Int = when (this) {
	is PairNumber -> 3 * left.magnitude() + 2 * right.magnitude()
	is RegularNumber -> value
}
