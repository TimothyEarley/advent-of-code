package de.earley.adventofcode2024.day22

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day22.start()

object Day22 : BaseSolution<List<Int>, Long, Int>() {

	override fun parseInput(input: Sequence<String>): List<Int> = input.mapToList { it.toInt() }

	override fun partOne(data: List<Int>): Long = data.sumOf { secret ->
		(1 .. 2000).fold(secret.toLong()) { acc, _ -> next(acc) }
	}

	override fun partTwo(data: List<Int>): Int = data.map { secret ->
		(1 .. 2000).runningFold(secret.toLong()) { acc, _ ->
			next(acc)
		}.map {
			price(it)
		}.windowed(5) { (a, b, c, d, e) ->
			e to (FourDiffs(b - a, c - b, d - c, e - d))
		}
	}.let { diffLists ->
		val lookups : List<Map<FourDiffs, Int>> = diffLists.map { diffs ->
			val lookup = mutableMapOf<FourDiffs, Int>()
			diffs.forEach {
				if (!lookup.contains(it.second)) {
					lookup[it.second] = it.first
				}
			}
			lookup
		}
		val sequences : Set<FourDiffs> = lookups.flatMapTo(HashSet(3_000)) { it.keys }
		return@let sequences.maxOf { sequence ->
			lookups.sumOf { lookup -> lookup[sequence] ?: 0 }
		}
	}

	data class FourDiffs(
		val a: Int,
		val b: Int,
		val d: Int,
		val e: Int,
	)

	private fun next(secret: Long): Long {
		val a = prune(mix(secret * 64, secret))
		val b = prune(mix(a / 32, a))
		val c = prune(mix(b * 2048, b))
		return c
	}

	private fun mix(i : Long, secret: Long) = i.xor(secret)

	private fun prune(i : Long): Long = i % 16777216

	private fun price(i : Long): Int = (i % 10).toInt()

}
