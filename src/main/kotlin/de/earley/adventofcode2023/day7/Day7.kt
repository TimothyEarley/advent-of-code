package de.earley.adventofcode2023.day7

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.mapToList

fun main() = Day7.start()

private typealias Card = Int

object Day7 : BaseSolution<List<Day7.Hand>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Hand> = input.mapToList { line ->
		val (cards, bid) = line.split(" ", limit = 2)

		Hand(cards.map { when (it) {
			'T' -> 10
			'J' -> 11
			'Q' -> 12
			'K' -> 13
			'A' -> 14
			else -> it.digitToInt()
		} }, bid.toInt())
	}

	private const val J = 11
	data class Hand(
		val cards: List<Card>,
		val bid: Int
	) {
		private val cardSet = cards.toSet()
		val cardsWithLowJoker = cards.map { if (it == J) 0 else it }

		val type : Int = when {
			cards.all { it == cards.first() } -> 7 // five of a kind
			cardSet.size == 2 && cardSet.any { c -> cards.count { it == c } == 4 } -> 6 // four of a kind
			cardSet.size == 2 && cardSet.any { c -> cards.count { it == c } == 3 } -> 5 // full house
			cardSet.size == 3 && cardSet.any { c -> cards.count { it == c } == 3 } -> 4 // three of a kind
			cardSet.size == 3 -> 3 // two pair
			cardSet.size == 4 -> 2 // one pair
			else -> 1 // high card
		}
	}

	override fun partOne(data: List<Hand>): Long {
		val sorted = data
			.sortedWith(compareBy({ it.type }, { it.cards[0] }, { it.cards[1] }, { it.cards[2] }, { it.cards[3] }, { it.cards[4] }))
		return sorted.mapIndexed { index, h -> (index + 1) * h.bid.toLong() }.sum()
	}


	override fun partTwo(data: List<Hand>): Long {
		val sorted = data
			.map { hand -> hand.jokerVariation() }
			.sortedWith(compareBy({ it.type }, { it.cardsWithLowJoker[0] }, { it.cardsWithLowJoker[1] }, { it.cardsWithLowJoker[2] }, { it.cardsWithLowJoker[3] }, { it.cardsWithLowJoker[4] }))
		return sorted.mapIndexed { index, h -> (index + 1) * h.bid.toLong() }.sum()
	}

	private fun Hand.jokerVariation(): Hand {
		val jokerCount = cards.count { it == J }
		if (jokerCount == 5) return Hand(listOf(1, 1, 1, 1, 1), bid)
		val cardsWithoutJoker = cards.filter { it != J }
		// the only place a joker is useful is if it is the same as another card,
		// and actually the highest count card
		val highestCount = cardsWithoutJoker.toSet().maxBy { c -> cardsWithoutJoker.count { it == c } }
		return Hand(
			cardsWithoutJoker + List(jokerCount) { highestCount }, bid
		)
	}

}

