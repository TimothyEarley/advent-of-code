package de.earley.adventofcode2024.day17

import com.microsoft.z3.BitVecExpr
import com.microsoft.z3.BitVecNum
import com.microsoft.z3.BitVecSort
import com.microsoft.z3.BoolSort
import com.microsoft.z3.Context
import com.microsoft.z3.Expr
import com.microsoft.z3.Status
import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.and
import de.earley.adventofcode.bv
import de.earley.adventofcode.eq
import de.earley.adventofcode.runUntil
import de.earley.adventofcode.split
import de.earley.adventofcode.xor
import kotlin.experimental.and

private typealias SymbolicNum = Expr<BitVecSort>

fun main() = Day17.start()

object Day17 : BaseSolution<Day17.Input, String, Long>() {

	private const val BIT_SIZE = 64

	override fun parseInput(input: Sequence<String>): Input =
		input.toList().split { it.isBlank() }.let { (regs, program) ->
			val a = regs[0].substringAfter("Register A: ").toLong()
			val b = regs[1].substringAfter("Register B: ").toLong()
			val c = regs[2].substringAfter("Register C: ").toLong()

			Input(
				state = State(0, a, b, c, emptyList()),
				program = program.single().substringAfter("Program: ")
					.split(',')
					.map { it.toInt() }
			)

		}

	data class Input(
		val state: State,
		val program: List<Int>
	)

	data class State(
		val pc: Int,
		val regA: Long,
		val regB: Long,
		val regC: Long,
		val out: List<Byte>
	) {
		fun inc() = copy(pc = pc + 2)
		fun regA(value: Long) = copy(regA = value)
		fun regB(value: Long) = copy(regB = value)
		fun regC(value: Long) = copy(regC = value)
	}

	data class SymbolicState(
		val pc: Int,
		val regA: SymbolicNum,
		val regB: SymbolicNum,
		val regC: SymbolicNum,
		val out: List<SymbolicNum>
	) {
		fun inc() = copy(pc = pc + 2)
		fun regA(value: SymbolicNum) = copy(regA = value)
		fun regB(value: SymbolicNum) = copy(regB = value)
		fun regC(value: SymbolicNum) = copy(regC = value)
	}

	@Suppress("EnumEntryName")
	enum class Instruction(val opCode: Int) {
		adv(0),
		bxl(1),
		bst(2),
		jnz(3),
		bxc(4),
		`out`(5),
		bdv(6),
		cdv(7),
	}

	override fun partOne(data: Input): String {
		val state = runUntil(data.state, { (it.pc + 1) !in data.program.indices }) { s ->
			val insCode = (data.program[s.pc])
			val operand = (data.program[s.pc + 1])
			val ins = Instruction.entries.find { it.opCode == insCode }!!
			ins.step(operand, s)
		}
		return state.out.joinToString(",")
	}

	override fun partTwo(data: Input): Long = with(Context()) {
		val a = mkBVConst("a", BIT_SIZE)
		val todo = data.program.toMutableList()
		val equations = mutableListOf<Expr<BoolSort>>()
		val start = SymbolicState(
			pc = 0,
			regA = a,
			regB = mkBV(data.state.regB, BIT_SIZE),
			regC = mkBV(data.state.regC, BIT_SIZE),
			out = emptyList()
		)
		runUntil(start, { todo.isEmpty() }) { state ->
			val insCode = (data.program[state.pc])
			val operand = (data.program[state.pc + 1])
			val ins = Instruction.entries.find { it.opCode == insCode }!!
			ins.step(operand, state).also {
				if (ins == Instruction.out) {
					val next = it.out.last()
					val nextExpected = todo.removeFirst()
					equations.add(next eq mkBV(nextExpected, BIT_SIZE))
				}
			}
		}

		return@with findMinimum(equations, a)
	}

	context(Context)
	private fun findMinimum(equations: List<Expr<BoolSort>>, a : BitVecExpr): Long {
		// find any solution
		var max = solutionSmallerThan(equations, a, Long.MAX_VALUE)!!
		var min = 0L
		// try to reduce
		while (min < max) {
			val half = min + (max - min) / 2L
			val solution = solutionSmallerThan(equations, a, half)
			if (solution == null) {
				min = half + 1
			} else {
				max = solution
			}
		}
		return max
	}

	context(Context)
	private fun solutionSmallerThan(equations: List<Expr<BoolSort>>, a : BitVecExpr, max: Long): Long? {
		val solver = mkSolver().apply {
			equations.forEach { add(it) }
			add(mkBVULT(a, max.bv(BIT_SIZE)))
		}
		return when (solver.check()) {
			Status.SATISFIABLE -> (solver.model.getConstInterp(a) as BitVecNum).long
			Status.UNSATISFIABLE -> null
			Status.UNKNOWN, null -> error("Failed to solve")
		}
	}

	private fun Instruction.step(operand: Int, state: State): State = when (this) {
		Instruction.adv -> state.inc().regA(dv(state, operand))
		Instruction.bxl -> state.inc().regB(state.regB xor operand.toLong())
		Instruction.bst -> state.inc().regB(comboValue(operand, state) and 0b111)
		Instruction.jnz ->
			if (state.regA == 0L) state.inc()
			else state.copy(pc = operand)
		Instruction.bxc -> state.inc().regB(state.regB xor state.regC)
		Instruction.out -> state.inc().copy(
			out = state.out + (comboValue(operand, state).toByte() and 0b111)
		)
		Instruction.bdv -> state.inc().regB(dv(state, operand))
		Instruction.cdv -> state.inc().regC(dv(state, operand))
	}

	private fun dv(state: State, operand: Int): Long =
		state.regA shr comboValue(operand, state).toInt()

	private fun comboValue(operand: Int, state: State): Long = when (operand) {
		0, 1, 2, 3 -> operand.toLong()
		4 -> state.regA
		5 -> state.regB
		6 -> state.regC
		else -> error("Invalid operand $operand")
	}

	context(Context)
	private fun Instruction.step(operand: Int, state: SymbolicState): SymbolicState = when (this) {
		Instruction.adv -> state.inc().regA(dv(state, operand))
		Instruction.bxl -> state.inc().regB(state.regB xor operand.toLong().bv(BIT_SIZE))
		Instruction.bst -> state.inc().regB(comboValue(operand, state) and 0b111L.bv(BIT_SIZE))
		Instruction.jnz -> {
			// dirty hack: we assume the program loops until we have all output, so always branch here
			// and handle exit in parent function
			require(operand == 0)
			state.copy(pc = 0)
		}
		Instruction.bxc -> state.inc().regB(state.regB xor state.regC)
		Instruction.out -> state.inc().copy(
			out = state.out + (comboValue(operand, state) and 0b111L.bv(BIT_SIZE))
		)
		Instruction.bdv -> state.inc().regB(dv(state, operand))
		Instruction.cdv -> state.inc().regC(dv(state, operand))
	}

	context(Context)
	private fun dv(state: SymbolicState, operand: Int): SymbolicNum =
		mkBVASHR(state.regA, comboValue(operand, state))

	context(Context)
	private fun comboValue(operand: Int, state: SymbolicState): SymbolicNum = when (operand) {
		0, 1, 2, 3 -> mkBV(operand, BIT_SIZE)
		4 -> state.regA
		5 -> state.regB
		6 -> state.regC
		else -> error("Invalid operand $operand")
	}

}