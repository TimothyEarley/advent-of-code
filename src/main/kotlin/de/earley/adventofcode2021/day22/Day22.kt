package de.earley.adventofcode2021.day22

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.mapToList

fun main() = Day22.start()

object Day22 : BaseSolution<List<RebootStep>, Long>() {

	private val lineRegex =
		Regex("(on|off) x=(-?\\d+)\\.\\.(-?\\d+),y=(-?\\d+)\\.\\.(-?\\d+),z=(-?\\d+)\\.\\.(-?\\d+)")

	override fun parseInput(input: Sequence<String>): List<RebootStep> = input.mapToList { line ->
		val (on, xStart, xEnd, yStart, yEnd, zStart, zEnd) =
			lineRegex.matchEntire(line)!!.destructured

		RebootStep(
			on == "on",
			Region(
				xStart.toInt()..xEnd.toInt(),
				yStart.toInt()..yEnd.toInt(),
				zStart.toInt()..zEnd.toInt()
			)
		)
	}

	override fun partOne(data: List<RebootStep>): Long =
		data.fold(emptyList<Region>(), this::step)
			.map { Region(it.x.coerce(-50, 50), it.y.coerce(-50, 50), it.z.coerce(-50, 50)) }
			.sumOf(Region::size)

	override fun partTwo(data: List<RebootStep>): Long =
		data.fold(emptyList<Region>(), this::step).sumOf(Region::size)

	private fun step(state: List<Region>, rs: RebootStep): List<Region> =
		state.flatMap { splitOverlap(it, rs.region) }.let {
			if (rs.on)
				it + rs.region // add the region
			else
				it // leave it at the removed
		}

	/**
	 * Split this cuboid into a max of 6 cuboids that result in cutting out the overlap
	 */
	private fun splitOverlap(toSplit: Region, overlapWith: Region): List<Region> {
		val overlap = toSplit.overlap(overlapWith) ?: return listOf(toSplit)

		// split the region into 6 volumes around the overlap
		val negX = Region(toSplit.x.coerce(Int.MIN_VALUE + 1, overlap.x.first - 1), toSplit.y, toSplit.z)
		val posX = Region(toSplit.x.coerce(overlap.x.last + 1, Int.MAX_VALUE - 1), toSplit.y, toSplit.z)

		// remember to also remove x regions covered by negX and posX
		val negY = Region(
			toSplit.x.coerce(negX.x.last + 1, posX.x.first - 1),
			toSplit.y.coerce(Int.MIN_VALUE + 1, overlap.y.first - 1),
			toSplit.z
		)
		val posY = Region(
			toSplit.x.coerce(negX.x.last + 1, posX.x.first - 1),
			toSplit.y.coerce(overlap.y.last + 1, Int.MAX_VALUE - 1),
			toSplit.z
		)

		// now consider x and y already covered
		val negZ = Region(
			toSplit.x.coerce(negX.x.last + 1, posX.x.first - 1),
			toSplit.y.coerce(negY.y.last + 1, posY.y.first - 1),
			toSplit.z.coerce(Int.MIN_VALUE + 1, overlap.z.first - 1)
		)
		val posZ = Region(
			toSplit.x.coerce(negX.x.last + 1, posX.x.first - 1),
			toSplit.y.coerce(negY.y.last + 1, posY.y.first - 1),
			toSplit.z.coerce(overlap.z.last + 1, Int.MAX_VALUE - 1)
		)

		// filter for empty regions
		return listOf(negX, posX, negY, posY, negZ, posZ).filterNot { it.isEmpty() }
	}
}

data class Region(
	val x: IntRange,
	val y: IntRange,
	val z: IntRange
)

fun Region.size(): Long = x.length.toLong() * y.length * z.length

fun Region.isEmpty(): Boolean =
	x.isEmpty() || y.isEmpty() || z.isEmpty()

fun Region.overlap(other: Region): Region? =
	Region(
		this.x.coerce(other.x.first, other.x.last),
		this.y.coerce(other.y.first, other.y.last),
		this.z.coerce(other.z.first, other.z.last)
	).takeIf { !it.isEmpty() }

private fun IntRange.coerce(low: Int, high: Int): IntRange =
	this.first.coerceIn(low, high + 1)..this.last.coerceIn(low - 1, high)

private val IntRange.length: Int
	get() = last - first + 1

data class RebootStep(
	val on: Boolean,
	val region: Region
)
