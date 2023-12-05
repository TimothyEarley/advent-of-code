package de.earley.adventofcode2023.day5

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split
import kotlin.math.max
import kotlin.math.min

fun main() = Day5.start()

private typealias ConversionMap = List<Day5.ConversionEntry>

object Day5 : BaseSolution<Day5.Almanac, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Almanac = input.toList().let { list ->
		val seeds = list.first().removePrefix("seeds: ").split(" ").map(String::toLong)
		val maps = list.drop(2).split { it.isBlank() }
		val conversionMaps = maps.map {
			it.drop(1).map { line ->
				val (dest, src, range) = line.trim().split(" ", limit = 3)
				ConversionEntry(dest.toLong(), src.toLong(), range.toInt())
			}
		}

		Almanac(
			seeds = seeds,
			maps = conversionMaps
		)
	}

	data class Almanac(
		val seeds: List<Long>,
		val maps: List<ConversionMap>,
	)

	data class ConversionEntry(
		val dest: Long,
		val src: Long,
		val rangeLength: Int,
	) {
		val srcRange: LongRange = src..< src + rangeLength
		val offset = dest - src
	}

	override fun partOne(data: Almanac): Long = data.seeds.minOf {
		it.lookupNext(data.maps)
	}

	override fun partTwo(data: Almanac): Long = data.seeds.chunked(2)
		.map { (start, length) -> start ..< start + length }
		.lookupNext(data.maps)
		.minOf { it.first }

	/**
	 * Single seed for part one
	 */
	private tailrec fun Long.lookupNext(maps: List<ConversionMap>): Long {
		if (maps.isEmpty()) return this
		val map = maps.first()
		val result = map.fold(this) { acc, conversionEntry ->
			if (this in conversionEntry.srcRange) {
				this + conversionEntry.offset
			} else {
				acc
			}
		}
		return result.lookupNext(maps.drop(1))
	}

	/**
	 * Applies the maps on ranges as a whole.
	 * When mapping a range it might split into multiple ranges, i.e.
	 * a range            xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
	 * might split into   xxxYYYYYYYxxxZZZZZZZHHHHHxxxxxx
	 */
	private tailrec fun List<LongRange>.lookupNext(maps: List<ConversionMap>): List<LongRange> {
		if (maps.isEmpty()) return this
		val map = maps.first().sortedBy { it.src }
		val result: List<LongRange> = this.flatMap { range ->
			val (list, last) = map.fold<ConversionEntry, Pair<List<LongRange>, LongRange?>>(
				listOf<LongRange>() to range
			) { (acc: List<LongRange>, current: LongRange?), conversionEntry ->
				// acc contains the processed chunks so far, current the last chunk and the only one we need to modify

				if (current == null) {
					// we are done already
					acc to null
				} else if (conversionEntry.srcRange.last < current.first || current.last < conversionEntry.srcRange.first) {
					// we do not affect the range at all
					acc to current
				} else {
					// split into potentially three parts (pre and post could be empty, the middle cannot
					// since we checked that already)
					val overlapStart = max(current.first, conversionEntry.srcRange.first)
					val overlapEnd = min(current.last, conversionEntry.srcRange.last)
					val pre = (current.first..< overlapStart).takeIf { !it.isEmpty() }
					val convert = overlapStart..overlapEnd
					val post = (overlapEnd + 1..current.last).takeIf { !it.isEmpty() }

					val converted =
						convert.first + conversionEntry.offset..convert.last + conversionEntry.offset

					acc + listOfNotNull(pre, converted) to post
				}
			}
			if (last != null) list.plusElement(last) else list
		}
		return result.lookupNext(maps.drop(1))
	}
}
