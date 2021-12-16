package de.earley.adventofcode2021.day16

import de.earley.adventofcode2021.BaseSolution

fun main() {
	Day16.start()
}

object Day16 : BaseSolution<Packet, Long>() {

	override fun parseInput(input: Sequence<String>): Packet = parsePacket(BitsReader(input.single()))

	private fun parsePacket(bitsReader: BitsReader): Packet {
		val version = bitsReader.read(3)
		return when (val type = bitsReader.read(3)) {
			4 -> {
				// literal
				var number = 0L
				while (bitsReader.read(1) == 1) {
					val add = bitsReader.read(4).toLong()
					number = number.shl(4).or(add)
				}
				// last block
				number = number.shl(4).or(bitsReader.read(4).toLong())
				LiteralPacket(version, number)
			}
			else -> {
				// operator
				val lengthId = bitsReader.read(1)
				OperatorPacket(
					version, typeToOperator(type),
					when (lengthId) {
						0 -> {
							// next 15 bits are total sub-packet length
							val length = bitsReader.read(15)
							val finalIndex = bitsReader.currentIndex() + length
							buildList {
								while (bitsReader.currentIndex() < finalIndex) {
									add(parsePacket(bitsReader))
								}
							}
						}
						1 -> {
							// next 11 bits are number of sub-packets
							val count = bitsReader.read(11)
							(1..count).map {
								parsePacket(bitsReader)
							}
						}
						else -> error("Invalid length ID")
					}
				)
			}
		}
	}

	override fun partOne(data: Packet): Long = data.sumVersion().toLong()

	private fun Packet.sumVersion(): Int = when (this) {
		is LiteralPacket -> version
		is OperatorPacket -> version + subPackets.sumOf { it.sumVersion() }
	}

	override fun partTwo(data: Packet): Long = data.eval()
}

sealed interface Packet {
	val version: Int
}

data class LiteralPacket(
	override val version: Int,
	val number: Long
) : Packet

enum class Operator {
	Sum, Product, Minimum, Maximum, GreaterThan, LessThan, EqualTo
}

fun typeToOperator(type: Int): Operator = when (type) {
	0 -> Operator.Sum
	1 -> Operator.Product
	2 -> Operator.Minimum
	3 -> Operator.Maximum
	5 -> Operator.GreaterThan
	6 -> Operator.LessThan
	7 -> Operator.EqualTo
	else -> error("Not a valid type")
}

data class OperatorPacket(
	override val version: Int,
	val type: Operator,
	val subPackets: List<Packet>
) : Packet

fun Packet.eval(): Long = when (this) {
	is LiteralPacket -> this.number
	is OperatorPacket -> when (type) {
		Operator.Sum -> subPackets.sumOf { it.eval() }
		Operator.Product -> subPackets.fold(1) { acc, p -> acc * p.eval() }
		Operator.Minimum -> subPackets.minOf { it.eval() }
		Operator.Maximum -> subPackets.maxOf { it.eval() }
		Operator.GreaterThan -> if (subPackets[0].eval() > subPackets[1].eval()) 1 else 0
		Operator.LessThan -> if (subPackets[0].eval() < subPackets[1].eval()) 1 else 0
		Operator.EqualTo -> if (subPackets[0].eval() == subPackets[1].eval()) 1 else 0
	}
}

class BitsReader private constructor(
	/**
	 * Actually an array of nibbles, aka 4 bits.
	 * Ignore the first four bits (since I am too
	 * lazy to compact the array)
	 */
	private val backing: ByteArray
) {

	private var index: Int = 0

	private val nibbleIndex: Int
		get() = index / 4

	private val inNibbleIndex: Int
		get() = index.rem(4)

	fun read(count: Int): Int {
		var number = 0
		repeat(count) {
			val bit = backing[nibbleIndex].toInt().shr(3 - inNibbleIndex).and(1)
			number = number.shl(1).or(bit)
			index++
		}
		return number
	}

	constructor(input: String) : this(
		input.map { it.digitToInt(16).toByte() }
			.toByteArray()
	)

	override fun toString(): String =
		"Index: $index, data: " + backing.joinToString("") { it.toString(2).padStart(4, '0') }

	fun currentIndex() = index
}
