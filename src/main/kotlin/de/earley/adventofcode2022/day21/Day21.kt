package de.earley.adventofcode2022.day21

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2022.day21.Day21.Op.*
import space.kscience.kmath.functions.ListRationalFunction
import space.kscience.kmath.functions.ListRationalFunctionSpace
import space.kscience.kmath.functions.listRationalFunctionSpace
import space.kscience.kmath.operations.JBigIntegerField
import space.kscience.kmath.operations.Ring
import java.math.BigInteger

fun main() = Day21.start()

object Day21 : BaseSolution<Map<String, Day21.Monkey>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Map<String, Monkey> = input.map {
		val (name, maths) = it.split(": ")
		when (val n = maths.toLongOrNull()) {
			null -> {
				val (left, opSymbol, right) = maths.split(" ", limit = 3)
				val op = when (opSymbol) {
					"+" -> Plus
					"-" -> Minus
					"*" -> Times
					"/" -> Divide
					else -> error("Unknown op $opSymbol")
				}
				OpMonkey(name, op, left, right)
			}

			else -> NumberMonkey(name, n)
		}
	}.associateBy(Monkey::name)

	override fun partOne(data: Map<String, Monkey>): Long = data["root"]!!.eval(data)

	override fun partTwo(data: Map<String, Monkey>): Long {
		val root = data["root"] as OpMonkey

		@Suppress("UNCHECKED_CAST") // cast is correct, needed for context receivers
		return with(JBigIntegerField.listRationalFunctionSpace as ListRationalFunctionSpace<BigInteger, Ring<BigInteger>>) {

			val left = root.left(data).evalWithHuman(data, BigInteger::valueOf)
			val right = root.right(data).evalWithHuman(data, BigInteger::valueOf)

			val eqZero = left.numerator * right.denominator - right.numerator * left.denominator

			when (eqZero.degree) {
				0 -> error("Nothing for the human to do")
				1 -> -eqZero.coefficients[0] / eqZero.coefficients[1]
				else -> error("Not supporting degree ${eqZero.degree}")
			}
		}.toLong()
	}

	sealed interface Monkey {
		val name: String
	}

	data class NumberMonkey(override val name: String, val number: Long) : Monkey
	data class OpMonkey(override val name: String, val op: Op, private val left: String, private val right: String) :
		Monkey {
		fun left(data: Map<String, Monkey>): Monkey = data[left]!!
		fun right(data: Map<String, Monkey>): Monkey = data[right]!!
	}

	// simple recursion is enough, no memorization needed for input
	private fun Monkey.eval(data: Map<String, Monkey>): Long = when (this) {
		is NumberMonkey -> number
		is OpMonkey -> with(op) {
			left(data).eval(data) op right(data).eval(data)
		}
	}

	context(ListRationalFunctionSpace<C, Ring<C>>)
	private fun <C> Monkey.evalWithHuman(data: Map<String, Monkey>, fromLong: (Long) -> C): ListRationalFunction<C> =
		if (name == "humn") {
			// polynomial where "x", i.e. the human, is one
			ListRationalFunction(listOf(ring.zero, ring.one))
		} else when (this) {
			is NumberMonkey -> ListRationalFunction(listOf(fromLong(number)))
			is OpMonkey -> with(op) {
				val leftValue = left(data).evalWithHuman(data, fromLong)
				val rightValue = right(data).evalWithHuman(data, fromLong)

				when (op) {
					Plus -> leftValue + rightValue
					Minus -> leftValue - rightValue
					Times -> leftValue * rightValue
					Divide -> leftValue / rightValue
				}
			}
		}

	enum class Op(private val opRef: Long.(Long) -> Long) {
		Plus(Long::plus), Minus(Long::minus), Times(Long::times), Divide(Long::div);

		infix fun Long.op(other: Long) = this.opRef(other)
	}
}