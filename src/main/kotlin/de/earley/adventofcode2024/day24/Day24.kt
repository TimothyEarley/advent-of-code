package de.earley.adventofcode2024.day24

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split
import java.util.*

fun main() = Day24.start()

object Day24 : BaseSolution<Day24.Input, Long, String>() {

	data class Input(
		val initial: Map<String, Boolean>, val gates: List<Gate>
	)

	data class Gate(
		val inputs: Set<String>, val op: Op, val out: String
	) {
		fun replaceOut(v: String, with: String) = Gate(
			inputs, op, if (out == v) with else out
		)
		fun replace(v: String, with: String) = Gate(
			inputs.map { if (it == v) with else it }.toSet(), op, if (out == v) with else out
		)
	}

	enum class Op {
		AND, OR, XOR
	}

	override fun parseInput(input: Sequence<String>): Input =
		input.toList().split { it.isBlank() }.let { (initial, gates) ->
			Input(initial = initial.associate {
				it.split(": ").let { (name, value) -> name to (value == "1") }
			}, gates = gates.map {
				it.split(" ").let { (a, op, b, _, out) ->
					Gate(
						setOf(a, b), op = when (op) {
							"AND" -> Op.AND
							"OR" -> Op.OR
							"XOR" -> Op.XOR
							else -> error("Unknown op: $op")
						}, out = out
					)
				}
			})
		}

	override fun partOne(data: Input): Long {
		val state = data.initial.toMutableMap()
		val remainingGates = data.gates.toMutableList()
		while (remainingGates.isNotEmpty()) {
			val next = remainingGates.first {
				it.inputs.all { it in state.keys }
			}
			remainingGates.remove(next)
			val result = when (next.op) {
				Op.AND -> next.inputs.fold(true) { acc, s -> acc && state[s]!! }
				Op.OR -> next.inputs.fold(true) { acc, s -> acc || state[s]!! }
				Op.XOR -> next.inputs.fold(false) { acc, s -> acc xor state[s]!! }
			}
			state[next.out] = result
		}
		return state.filter { it.key.startsWith("z") }.toList()
			.sortedByDescending { it.first.removePrefix("z").toInt() }.fold(0L) { acc, (_, bit) ->
				(acc shl 1) or (if (bit) 1 else 0)
			}
	}

	override fun partTwo(data: Input): String {
		// pattern is
		// xi XOR yi  => temp
		// prevCarry XOR temp => zi
		// temp AND prevCarry => carry1
		// xi AND yi => carry2
		// carry1 OR carry2 => nextCarry

		var gates = data.gates
		val zs = gates.filter { it.out.startsWith("z") }

		// found by running till errors and manually fixing them
		val swaps = listOf(
			"z12" to "qdg",
			"z19" to "vvf",
			"dck" to "fgn",
			"z37" to "nvh",
		)

		val placeholder = UUID.randomUUID().toString()
		swaps.forEach { (a, b) ->
			gates = gates.map {
				it.replaceOut(a, placeholder)
					.replaceOut(b, a)
					.replaceOut(placeholder, b)
			}
		}

		for (i in (1..< zs.lastIndex)) {
			val xi = "x%2d".format(i).replace(' ', '0')
			val yi = "y%2d".format(i).replace(' ', '0')
			val zi = "z%2d".format(i).replace(' ', '0')

			val outputGate = gates.find { it.out == zi } ?:
				error("Could not find output gate for $zi")
			val (a, b) = gates.filter { it.out in outputGate.inputs }.also {
				if (it.size != 2)
					error("Inputs for output gate not found!")
			}
			val (temp, prevCarry) = if (a.inputs == setOf(xi, yi)) a to b else b to a
			require(temp.op == Op.XOR) {
				"Failed temp XOR for outputGate=$outputGate, temp=$temp, prevCarry=$prevCarry"
			}

			val carry1 = gates.find { it.op == Op.AND && it.inputs.contains(temp.out) } ?:
				error("Failed to find carry1 for outputGate=$outputGate, temp=$temp, prevCarry=$prevCarry")
			require(carry1.inputs.contains(prevCarry.out))
			val carry2 = gates.find { it.op == Op.AND && it.inputs == setOf(xi, yi) }!!

			val nextCarry = gates.find { it.op == Op.OR && it.inputs == setOf(carry1.out, carry2.out) }
				?: error("Could not find next carry for outputGate=$outputGate, temp=$temp, carry1=$carry1, carry2=$carry2, prevCarry=$prevCarry")


			gates = gates.map {
				it
					.replace(temp.out, "temp$i-${temp.out}")
					.replace(carry1.out, "carry1_$i-${carry1.out}")
					.replace(carry2.out, "carry2_$i-${carry2.out}")
					.replace(nextCarry.out, "carry_$i-${nextCarry.out}")
			}

		}

		return swaps.flatMap { listOf(it.first, it.second) }.sorted().joinToString(",")
	}

}
