package de.earley.adventofcode2023.day24

import com.microsoft.z3.Context
import com.microsoft.z3.Status
import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.LongPoint3
import de.earley.adventofcode.eq
import de.earley.adventofcode.get
import de.earley.adventofcode.mapToList
import de.earley.adventofcode.plus
import de.earley.adventofcode.real
import de.earley.adventofcode.times
import space.kscience.kmath.functions.ListPolynomial
import kotlin.math.sign

fun main() = Day24(200000000000000, 400000000000000).start()

class Day24(
	searchMin: Long,
	searchMax: Long,
) : BaseSolution<List<Day24.Hailstone>, Long, Long>() {

	private val searchRange = searchMin.toDouble()..searchMax.toDouble()

	override fun parseInput(input: Sequence<String>): List<Hailstone> = input.mapToList { line ->
		val (a, b) = line.split(" @ ")
		Hailstone(LongPoint3.parse(a), LongPoint3.parse(b))
	}

	data class Hailstone(
		val position: LongPoint3,
		val velocity: LongPoint3,
	) {
		/*
		 x = p.x + v.x * t ==> t = (x - p.x) / v.x
		 y = p.y + v.y * t ==> y = p.y + v.y * (x - p.x) / v.x
		                   ===> y = p.y - v.y * p.x / v.x + v.y/v.x * x
		 */
		val xyGraph = ListPolynomial(
			position.y - (position.x * velocity.y) / velocity.x.toDouble(),
			velocity.y / velocity.x.toDouble()
		)
	}

	override fun partOne(data: List<Hailstone>): Long = data.sumOf { hailstone ->
		data.filter { it != hailstone }
			.count {
				crosses(hailstone, it)
			}.toLong()
	} / 2

	private fun crosses(h1: Hailstone, h2: Hailstone): Boolean {
		val a = h1.xyGraph.coefficients[1]
		val b = h2.xyGraph.coefficients[1]
		val c = h1.xyGraph.coefficients[0]
		val d = h2.xyGraph.coefficients[0]
		val x = (d - c) / (a - b)
		val y = a * (d - c) / (a - b) + c

		val inRange = x in searchRange && y in searchRange
		val inFutureH1 = when (h1.velocity.x.sign) {
			-1 -> x < h1.position.x
			1 -> x > h1.position.x
			else -> error("Can't handle this case")
		}
		val inFutureH2 = when (h2.velocity.x.sign) {
			-1 -> x < h2.position.x
			1 -> x > h2.position.x
			else -> error("Can't handle this case")
		}

		return inRange && inFutureH1 && inFutureH2
	}

	override fun partTwo(data: List<Hailstone>): Long = with(Context()) {
		val solver = mkSolver()

		val x = mkRealConst("x")
		val y = mkRealConst("y")
		val z = mkRealConst("z")
		val vx = mkRealConst("vx")
		val vy = mkRealConst("vy")
		val vz = mkRealConst("vz")

		// three stones are enough to get all variables (6 + 3 variables).
		// So if there is a solution, that will be it
		data.take(3).forEachIndexed { i, hailstone ->
			val t = mkRealConst("t$i")
			solver.add(mkGe(t, mkReal(0))) // t > 0
			solver.add(x + vx * t eq hailstone.position.x.real + hailstone.velocity.x.real * t)
			solver.add(y + vy * t eq hailstone.position.y.real + hailstone.velocity.y.real * t)
			solver.add(z + vz * t eq hailstone.position.z.real + hailstone.velocity.z.real * t)
		}

		return when (solver.check()) {
			Status.UNSATISFIABLE -> error("UNSAT")
			null, Status.UNKNOWN -> error("UNKNOWN")
			Status.SATISFIABLE -> {
				solver.model[x].toLong() + solver.model[y].toLong() + solver.model[z].toLong()
			}
		}
	}
}
