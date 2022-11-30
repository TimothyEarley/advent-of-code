package de.earley.adventofcode2021.day24

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList

fun main() = Day24.start()

object Day24 : BaseSolution<List<Instruction>, Long>() {

	override fun parseInput(input: Sequence<String>): List<Instruction> = input.mapToList {
		val (cmd, args) = it.split(' ', limit = 2)
		when (cmd) {
			"inp" -> InpInstruction(Variable(args))
			"add" -> args.parseArgs().let { (a, b) -> AddInstruction(a, b) }
			"mul" -> args.parseArgs().let { (a, b) -> MulInstruction(a, b) }
			"div" -> args.parseArgs().let { (a, b) -> DivInstruction(a, b) }
			"mod" -> args.parseArgs().let { (a, b) -> ModInstruction(a, b) }
			"eql" -> args.parseArgs().let { (a, b) -> EqlInstruction(a, b) }
			else -> error("Unknown command $cmd")
		}
	}

	private fun String.parseArgs(): Pair<Variable, Argument> = split(' ', limit = 2).let { (a, b) ->
		when (val n = b.toIntOrNull()) {
			null -> Variable(a) to Variable(b)
			else -> Variable(a) to Number(n)
		}
	}

	override fun partOne(data: List<Instruction>): Long =
		findMaxSolution(9 downTo 1, State(0, 0, 0, 0), 0, data, 0, mutableSetOf())!!

	override fun partTwo(data: List<Instruction>): Long =
		findMaxSolution(1..9, State(0, 0, 0, 0), 0, data, 0, mutableSetOf())!!

	private fun findMaxSolution(
		range: IntProgression,
		state: State,
		pc: Int,
		instructions: List<Instruction>,
		usedInput: Long,
		seen: MutableSet<Pair<Int, State>>
	): Long? {
		var currentState = state
		var i = pc
		while (i <= instructions.lastIndex) {
			// if (currentState.z > 260) return null
			// println("$i: $usedInput, $state")
			when (val ins = instructions[i++]) {
				is InpInstruction -> {
					// try all inputs
					return if (!seen.add(i to currentState)) {
						// already seen
						null
					} else {
						range.firstNotNullOfOrNull {
							findMaxSolution(
								range,
								currentState.set(ins.v, it),
								i,
								instructions,
								usedInput * 10 + it,
								seen
							)
						}
					}
				}

				else -> {
					currentState = (ins as OpInstruction).eval(currentState)
				}
			}
		}

		return usedInput.takeIf { currentState.z == 0 }
	}
}

sealed interface Argument
data class Variable(val x: String) : Argument
data class Number(val n: Int) : Argument

sealed interface Instruction

data class InpInstruction(val v: Variable) : Instruction

sealed interface OpInstruction : Instruction
data class AddInstruction(val a: Variable, val b: Argument) : OpInstruction
data class MulInstruction(val a: Variable, val b: Argument) : OpInstruction
data class DivInstruction(val a: Variable, val b: Argument) : OpInstruction
data class ModInstruction(val a: Variable, val b: Argument) : OpInstruction
data class EqlInstruction(val a: Variable, val b: Argument) : OpInstruction

data class State(
	val x: Int,
	val y: Int,
	val w: Int,
	val z: Int
)

operator fun State.set(x: Variable, value: Int): State = when (x.x) {
	"x" -> copy(x = value)
	"y" -> copy(y = value)
	"z" -> copy(z = value)
	"w" -> copy(w = value)
	else -> error("Unknown variable $x")
}

operator fun State.get(a: Argument): Int = when (a) {
	is Number -> a.n
	is Variable -> when (a.x) {
		"x" -> x
		"y" -> y
		"z" -> z
		"w" -> w
		else -> error("Unknown variable $x")
	}
}

fun OpInstruction.eval(s: State): State = when (this) {
	is AddInstruction -> s.set(a, s[a] + s[b])
	is DivInstruction -> s.set(a, s[a] / s[b])
	is EqlInstruction -> s.set(a, if (s[a] == s[b]) 1 else 0)
	is ModInstruction -> s.set(a, s[a] % s[b])
	is MulInstruction -> s.set(a, s[a] * s[b])
}
