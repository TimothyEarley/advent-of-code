package de.earley.adventofcode2025.day11

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day11.start()

object Day11 : BaseSolution<List<Day11.Device>, Long, Long>() {

	data class Device(val name: String, val connected: List<String>)

	override fun parseInput(input: Sequence<String>): List<Device> = input.mapToList { line ->
		Device(line.substringBefore(':'), line.substringAfter(' ').split(' '))
	}

	override fun partOne(data: List<Device>): Long = countPaths(data, "you")

	private fun countPaths(data: List<Device>, current: String): Long {
		if (current == "out") return 1
		return data.find { it.name == current }!!.connected.sumOf { countPaths(data, it) }
	}

	override fun partTwo(data: List<Device>): Long = countPaths2(data, "svr", seenDac = false, seenFft = false)

	private val cache: MutableMap<Triple<String, Boolean, Boolean>, Long> = mutableMapOf()
	private fun countPaths2(data: List<Device>, current: String, seenDac: Boolean, seenFft: Boolean): Long =
		cache.getOrPut(Triple(current, seenDac, seenFft)) {
			when (current) {
				"out" -> if (seenDac && seenFft) 1 else 0
				else -> data.find { it.name == current }!!.connected.sumOf {
					countPaths2(data, it, seenDac || current == "dac", seenFft || current == "fft")
				}
			}
		}
}
