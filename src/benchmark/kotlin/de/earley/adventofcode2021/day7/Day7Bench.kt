package de.earley.adventofcode2021.day7

import de.earley.adventofcode.cache
import de.earley.adventofcode.readResource
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import kotlin.math.roundToInt

@State(Scope.Benchmark)
class Day7Bench {

	/*
	benchmark summary:
	Benchmark                        Mode  Cnt     Score    Error  Units
	Day7Bench.partOneBruteForce      avgt    5  2267.309 ± 37.064  us/op
	Day7Bench.partOneStartAvg        avgt    5   299.140 ±  3.936  us/op
	Day7Bench.partOneStartAvgCached  avgt    5   188.491 ±  4.662  us/op
	Day7Bench.partTwoBruteForce      avgt    5  3392.379 ± 77.733  us/op
	Day7Bench.partTwoStartAvg        avgt    5    10.899 ±  0.480  us/op
	Day7Bench.partTwoStartAvgCached  avgt    5     7.106 ±  0.116  us/op
	 */

	val data = Day7.readResource("input.txt").useLines(Day7::parseInput)

	@Benchmark
	fun partOneBruteForce(): Int = findBestForce(data, Day7::totalDistance)

	@Benchmark
	fun partOneStartAvg(): Int = findBestStartAvg(data, Day7::totalDistance)

	@Benchmark
	fun partOneStartAvgCached(): Int = findeBestStartAvgCached(data, Day7::totalDistance)

	@Benchmark
	fun partTwoBruteForce(): Int = findBestForce(data, Day7::totalDistance2)

	@Benchmark
	fun partTwoStartAvg(): Int = findBestStartAvg(data, Day7::totalDistance2)

	@Benchmark
	fun partTwoStartAvgCached(): Int = findeBestStartAvgCached(data, Day7::totalDistance2)

	private fun findBestForce(data: List<Int>, f: (List<Int>, Int) -> Int): Int {
		// brute force solution
		val max = data.maxOf { it }
		return (0..max).minOf { f(data, it) }
	}

	private fun findBestStartAvg(data: List<Int>, f: (List<Int>, Int) -> Int): Int {
		val avg = data.average().roundToInt()
		return Day7.findMin(avg) { f(data, it) }
	}

	private fun findeBestStartAvgCached(data: List<Int>, f: (List<Int>, Int) -> Int): Int {
		val avg = data.average().roundToInt()
		val cachedF = { i: Int -> f(data, i) }.cache()
		return Day7.findMin(avg, cachedF)
	}
}
