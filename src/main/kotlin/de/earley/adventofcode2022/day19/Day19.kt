package de.earley.adventofcode2022.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStar
import de.earley.adventofcode2021.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

fun main() = Day19.start()

object Day19 : BaseSolution<List<Day19.Blueprint>, Int, Int>() {

	private val regex =
		"Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

	override fun parseInput(input: Sequence<String>): List<Blueprint> = input.mapToList {
		val (id, ore, clay, obsidianOre, obsidianClay, geodeOre, geodeClay) = regex.matchEntire(it)!!.destructured
		Blueprint(
			id.toInt(),
			ore.toInt(),
			clay.toInt(),
			obsidianOre.toInt() to obsidianClay.toInt(),
			geodeOre.toInt() to geodeClay.toInt(),
		)
	}

	override fun partOne(data: List<Blueprint>): Int = runBlocking(Dispatchers.Default) {
		data
			.map {
				async {
					it.id * mostGeodesCollected(it, 24)
				}
			}.sumOf { it.await() }
	}

	@OptIn(ExperimentalTime::class)
	override fun partTwo(data: List<Blueprint>): Int = measureTimedValue {
		data.take(3)
			.map { mostGeodesCollected(it, 32) }
			.fold(1, Int::times)
	}.let {
		println(it.duration)
		it.value
	}

	private fun mostGeodesCollected(blueprint: Blueprint, totalTime: Int): Int {
		val maxResources = Resources(
			maxOf(blueprint.oreRobot, blueprint.clayRobot, blueprint.obsidianRobot.first, blueprint.geodeRobot.first),
			maxOf(blueprint.obsidianRobot.second),
			maxOf(blueprint.geodeRobot.second),
			Int.MAX_VALUE
		)

		return -generalAStar(
			from = State(0, 0, 0, 0, 0, 1, 0, 0, 0),
			goal = { it.time == totalTime },
			heuristic = {
				// the best possible we can do is to create one geode robot every time step
				val time = totalTime - it.time
				-(time * it.geodeRobot + time * (time - 1) / 2)
			},
			useClosed = true,
			neighbours = { nextStates(blueprint, maxResources) }
		)
	}

	private fun State.nextStates(blueprint: Blueprint, maxResources: Resources): Sequence<Pair<State, Int>> {

		val buildOptions = buildList {
			// Nothing, but if we can build the next tier of robots, we should always do so
			var canDoNothing = true

			/*
			 * Heuristics:
			 * - It is always better to build a robot type we don't have yet instead of none
			 * - It is always better to build a geode robot than any other robot
			 * - If we have enough robots to fulfill any need per round, don't build any more of that
			 */

			// Geode Robot
			if (ore >= blueprint.geodeRobot.first && obsidian >= blueprint.geodeRobot.second) {
				add(
					copy(
						ore = ore - blueprint.geodeRobot.first,
						obsidian = obsidian - blueprint.geodeRobot.second,
						geodeRobot = geodeRobot + 1
					)
				)
				if (geodeRobot == 0) canDoNothing = false
			} else {
				// Obsidian Robot
				if (ore >= blueprint.obsidianRobot.first && clay >= blueprint.obsidianRobot.second && obsidianRobot < maxResources.obsidian) {
					add(
						copy(
							ore = ore - blueprint.obsidianRobot.first,
							clay = clay - blueprint.obsidianRobot.second,
							obsidianRobot = obsidianRobot + 1
						)
					)
					if (obsidianRobot == 0) canDoNothing = false
				}

				// Clay Robot
				if (canDoNothing && ore >= blueprint.clayRobot && clayRobot < maxResources.clay) {
					add(copy(ore = ore - blueprint.clayRobot, clayRobot = clayRobot + 1))
					if (clayRobot == 0) canDoNothing = false
				}

				// Ore Robot
				if (ore >= blueprint.oreRobot && oreRobot < maxResources.clay) {
					add(copy(ore = ore - blueprint.oreRobot, oreRobot = oreRobot + 1))
					if (oreRobot == 0) canDoNothing = false
				}
			}

			if (canDoNothing)
				add(this@nextStates)

		}

		val resourcesThisRound = buildOptions.map {
			it.copy(
				time = time + 1,
				ore = it.ore + oreRobot,
				clay = it.clay + clayRobot,
				obsidian = it.obsidian + obsidianRobot,
				geode = it.geode + geodeRobot,
			) to -(geodeRobot)
		}

		return resourcesThisRound.asSequence()

	}

	data class State(
		val time: Int,
		val ore: Ore,
		val clay: Clay,
		val obsidian: Obsidian,
		val geode: Geode,
		val oreRobot: Int,
		val clayRobot: Int,
		val obsidianRobot: Int,
		val geodeRobot: Int
	) {
		operator fun compareTo(other: State): Int = if (other == this) 0
		else if (time <= other.time && ore >= other.ore && clay >= other.clay &&
			obsidian >= other.obsidian && geode >= other.geode &&
			oreRobot >= other.oreRobot && clayRobot >= other.clayRobot &&
			obsidian >= other.obsidianRobot && geodeRobot >= other.geodeRobot
		) 1
		else -1
	}

	data class Blueprint(
		val id: Int,
		val oreRobot: Ore,
		val clayRobot: Ore,
		val obsidianRobot: Pair<Ore, Clay>,
		val geodeRobot: Pair<Ore, Obsidian>
	)

	data class Resources(
		val ore: Ore,
		val clay: Clay,
		val obsidian: Obsidian,
		val geode: Geode,
	)

}

typealias Ore = Int
typealias Clay = Int
typealias Obsidian = Int
typealias Geode = Int