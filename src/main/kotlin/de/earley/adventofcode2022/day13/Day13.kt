package de.earley.adventofcode2022.day13

import cc.ekblad.konbini.Parser
import cc.ekblad.konbini.ParserResult
import cc.ekblad.konbini.bracket
import cc.ekblad.konbini.chain
import cc.ekblad.konbini.char
import cc.ekblad.konbini.integer
import cc.ekblad.konbini.map
import cc.ekblad.konbini.oneOf
import cc.ekblad.konbini.parseToEnd
import cc.ekblad.konbini.parser
import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode2021.split

fun main() = Day13.start()

object Day13 : BaseSolution<List<Pair<Day13.Packet, Day13.Packet>>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<Pair<Packet, Packet>> = input
		.toList()
		.split { it.isBlank() }
		.map { (left, right) ->
			left.parsePacket() to right.parsePacket()
		}

	override fun partOne(data: List<Pair<Packet, Packet>>): Int = data.withIndex().sumOf {
		if (it.value.first < it.value.second) it.index + 1 else 0
	}

	override fun partTwo(data: List<Pair<Packet, Packet>>): Int {
		val driverA = ListPacket(listOf(ListPacket(listOf(IntPacket(2)))))
		val driverB = ListPacket(listOf(ListPacket(listOf(IntPacket(6)))))
		return data.flatMap { it.toList() }
			.let { it + driverA + driverB }
			.sortedWith { l, r -> l.compareTo(r) }
			.let { sorted ->
				(sorted.indexOf(driverA) + 1) * (sorted.indexOf(driverB) + 1)
			}
	}

	private fun String.parsePacket(): Packet = when (val result = PacketParser.packet.parseToEnd(this, ignoreWhitespace = true)) {
		is ParserResult.Ok -> result.result
		is ParserResult.Error -> error(result.reason)
	}

	object PacketParser {
		private val comma = char(',')
		private val openBracket = char('[')
		private val closedBracket = char(']')
		private val intPacket: Parser<IntPacket> = integer.map(::IntPacket)
		private val listPacket: Parser<ListPacket> = bracket(
			openBracket,
			closedBracket,
			parser { chain(packet, comma).terms }.map(::ListPacket)
		)
		val packet: Parser<Packet> = oneOf(intPacket, listPacket)
	}

	sealed interface Packet
	data class ListPacket(val list: List<Packet>) : Packet {
		override fun toString(): String = list.toString()
		override fun equals(other: Any?): Boolean {
			if (other !is ListPacket) return false
			if (list.size != other.list.size) return false
			return list.zip(other.list).all { it.first == it.second }
		}

		override fun hashCode(): Int = list.hashCode()
	}
	data class IntPacket(val int: Long) : Packet {
		override fun toString(): String = int.toString()
	}

	private operator fun Packet.compareTo(right: Packet): Int {
		return when (this) {
			is IntPacket -> when (right) {
				is IntPacket -> this.int.compareTo(right.int)
				is ListPacket -> ListPacket(listOf(IntPacket(this.int))).compareTo(right)
			}

			is ListPacket -> when (right) {
				is IntPacket -> this.compareTo(ListPacket(listOf(IntPacket(right.int))))
				is ListPacket -> {
					for ((l, r) in list.zip(right.list)) {
						l.compareTo(r).takeUnless { it == 0 }?.let { return it }
					}
					list.size.compareTo(right.list.size)
				}
			}
		}
	}
}
