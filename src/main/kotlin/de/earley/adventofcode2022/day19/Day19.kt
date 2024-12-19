package de.earley.adventofcode2022.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStarNode
import de.earley.adventofcode.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil

fun main() = Day19.start()

object Day19 : BaseSolution<List<Day19.Blueprint>, Int, Int>() {

	private val regex =
		"Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

	override fun parseInput(input: Sequence<String>): List<Blueprint> = input.mapToList {
		val (id, ore, clay, obsidianOre, obsidianClay, geodeOre, geodeObsidian) = regex.matchEntire(it)!!.destructured
		Blueprint(
			id.toInt(),
			Resources(ore = ore.toInt()),
			Resources(ore = clay.toInt()),
			Resources(ore = obsidianOre.toInt(), clay = obsidianClay.toInt()),
			Resources(ore = geodeOre.toInt(), obsidian = geodeObsidian.toInt())
		)
	}

	override fun partOne(data: List<Blueprint>): Int = runBlocking(Dispatchers.Default) {
		data.sumOf {
			it.id * mostGeodesCollected(it, 24)
		}
	}

	override fun partTwo(data: List<Blueprint>): Int =
	data.take(3)
		.map { mostGeodesCollected(it, 32) }
		.fold(1, Int::times)

	private fun mostGeodesCollected(blueprint: Blueprint, totalTime: Int): Int {
		return -generalAStarNode(
			from = State(
				totalTime,
				Resources(0, 0, 0, 0),
				1,
				0,
				0,
				0
			),
			goal = { it.minutesLeft == 0 },
			heuristic = { - it.maxNewGeodeProduction() },
			neighbours = {
				nextStates(blueprint)
			}
		).first().cost
	}

	private fun State.maxNewGeodeProduction(): Int {
		// the best possible we can do is to create one geode robot every time step
		return (0 until minutesLeft - 1).sumOf { it + this.geodeRobot }
	}

	private fun State.nextStates(blueprint: Blueprint): Sequence<Pair<State, Int>> = sequence {
		if (blueprint.maxResources.ore > oreRobot) {
			yield(next(blueprint.oreRobot).copy(oreRobot = oreRobot + 1))
		}
		if (blueprint.maxResources.clay > clayRobot) {
			yield(next(blueprint.clayRobot).copy(clayRobot = clayRobot + 1))
		}
		if (blueprint.maxResources.obsidian > obsidianRobot) {
			yield(next(blueprint.obsidianRobot).copy(obsidianRobot = obsidianRobot + 1))
		}
		yield(next(blueprint.geodeRobot).copy(geodeRobot = geodeRobot + 1))
	}.filter {
		it.minutesLeft >= 0
	}.map {
		it to -(geodeRobot * (this.minutesLeft - it.minutesLeft))
	}

	private fun State.next(built: Resources): State {
		val minutes = timeToBuilt(built)
		return State(
			minutesLeft = minutesLeft - minutes,
			resources = Resources(
				ore = resources.ore + oreRobot * minutes - built.ore,
				clay = resources.clay + clayRobot * minutes - built.clay,
				obsidian = resources.obsidian + obsidianRobot * minutes - built.obsidian,
				geode = resources.geode + geodeRobot * minutes - built.geode,
			),
			oreRobot = oreRobot,
			clayRobot = clayRobot,
			obsidianRobot = obsidianRobot,
			geodeRobot = geodeRobot
		)
	}

	private fun State.timeToBuilt(buildingResources: Resources): Int {
		return maxOf(
			timeForResource(resources.ore, buildingResources.ore, oreRobot),
			timeForResource(resources.clay, buildingResources.clay, clayRobot),
			timeForResource(resources.obsidian, buildingResources.obsidian, obsidianRobot),
			timeForResource(resources.geode, buildingResources.geode, geodeRobot),
		) + 1
	}

	private fun timeForResource(inStock: Int, demand: Int, robots: Int): Int {
		val needed = (demand - inStock).coerceAtLeast(0)
		return ceil(needed / robots.toFloat()).toInt()
	}

	data class State(
		val minutesLeft: Int,
		val resources: Resources,
		val oreRobot: Int,
		val clayRobot: Int,
		val obsidianRobot: Int,
		val geodeRobot: Int,
	)

	data class Blueprint(
		val id: Int,
		val oreRobot: Resources,
		val clayRobot: Resources,
		val obsidianRobot: Resources,
		val geodeRobot: Resources,
	) {
		val maxResources: Resources = Resources(
			maxOf(oreRobot.ore, clayRobot.ore, obsidianRobot.ore, geodeRobot.ore),
			maxOf(oreRobot.clay, clayRobot.clay, obsidianRobot.clay, geodeRobot.clay),
			maxOf(oreRobot.obsidian, clayRobot.obsidian, obsidianRobot.obsidian, geodeRobot.obsidian),
			Int.MAX_VALUE
		)
	}

	data class Resources(
		val ore: Ore = 0,
		val clay: Clay = 0,
		val obsidian: Obsidian = 0,
		val geode: Geode = 0,
	)
}

typealias Ore = Int
typealias Clay = Int
typealias Obsidian = Int
typealias Geode = Int
