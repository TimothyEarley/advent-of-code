package de.earley.adventofcode2022.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.generalAStarNode
import de.earley.adventofcode.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.util.PriorityQueue
import kotlin.math.ceil
import kotlin.math.max

// heavily inspired by https://github.com/ClouddJR/advent-of-code-2022/blob/main/src/main/kotlin/com/clouddjr/advent2022/Day19.kt

fun main() = Day19.start()

object Day19 : BaseSolution<List<Day19.Blueprint>, Int, Int>() {

	private val regex =
		"Blueprint (\\d+): Each ore robot costs (\\d+) ore. Each clay robot costs (\\d+) ore. Each obsidian robot costs (\\d+) ore and (\\d+) clay. Each geode robot costs (\\d+) ore and (\\d+) obsidian.".toRegex()

	override fun parseInput(input: Sequence<String>): List<Blueprint> = input.mapToList {
		val (id, ore, clay, obsidianOre, obsidianClay, geodeOre, geodeObsidian) = regex.matchEntire(it)!!.destructured
		Blueprint(
			id.toInt(),
			Robot(needs = Resources(ore = ore.toInt()), provides = Resources(ore = 1)),
			Robot(needs = Resources(ore = clay.toInt()), provides = Resources(clay = 1)),
			Robot(needs = Resources(ore = obsidianOre.toInt(), clay = obsidianClay.toInt()), provides = Resources(obsidian = 1)),
			Robot(needs = Resources(ore = geodeOre.toInt(), obsidian = geodeObsidian.toInt()), provides = Resources(geode = 1))
		)
	}

	override fun partOne(data: List<Blueprint>): Int = data.sumOf {
		it.id * mostGeodesCollected(it, 24)
	}

	override fun partTwo(data: List<Blueprint>): Int =
		data.take(3)
			.map { mostGeodesCollected(it, 32) }
			.fold(1, Int::times)

	private fun mostGeodesCollected(blueprint: Blueprint, totalTime: Int): Int {
		val start = State(
			totalTime,
			Resources(0, 0, 0, 0),
			Resources(1, 0, 0, 0)
		)
		val open = PriorityQueue<State>(compareBy { it.resources.geode })
		open += start
		var best = 0
		while (open.isNotEmpty()) {
			val next = open.remove()
			// only consider this node if it has the potential to outperform our current best
			if (next.resources.geode + next.maxNewGeodeProduction() > best) {
				best = max(best, next.resources.geode + (next.minutesLeft) * next.robots.geode)
				if (next.minutesLeft == 0) {
					break
				}
				open.addAll(next.nextStates(blueprint))
			}
		}
		return best
	}

	private fun State.maxNewGeodeProduction(): Int {
		// the best possible we can do is to create one geode robot every time step
		return (0 until minutesLeft).sumOf { it + this.robots.geode }
	}

	private fun State.nextStates(blueprint: Blueprint): Sequence<State> = sequence {
		// if we can build a geode robot now, do it
		if (timeToBuilt(blueprint.geodeRobot.needs) == 1) {
			yield(next(blueprint.geodeRobot))
		} else {
			// otherwise build what is needed
			if (blueprint.maxResources.ore > robots.ore) {
				yield(next(blueprint.oreRobot))
			}
			if (blueprint.maxResources.clay > robots.clay) {
				yield(next(blueprint.clayRobot))
			}
			if (blueprint.maxResources.obsidian > robots.obsidian) {
				yield(next(blueprint.obsidianRobot))
			}
			yield(next(blueprint.geodeRobot))
		}
	}.filter {
		it.minutesLeft >= 0
	}

	private fun State.next(robot: Robot): State {
		val minutes = timeToBuilt(robot.needs)
		return State(
			minutesLeft = minutesLeft - minutes,
			resources = resources + (robots * minutes) - robot.needs,
			robots = robots + robot.provides
		)
	}

	private fun State.timeToBuilt(buildingResources: Resources): Int {
		return maxOf(
			timeForResource(resources.ore, buildingResources.ore, robots.ore),
			timeForResource(resources.clay, buildingResources.clay, robots.clay),
			timeForResource(resources.obsidian, buildingResources.obsidian, robots.obsidian),
			timeForResource(resources.geode, buildingResources.geode, robots.geode),
		) + 1
	}

	private fun timeForResource(inStock: Int, demand: Int, robots: Int): Int {
		val needed = (demand - inStock).coerceAtLeast(0)
		return ceil(needed / robots.toFloat()).toInt()
	}

	data class State(
		val minutesLeft: Int,
		val resources: Resources,
		val robots: Resources,
	)

	data class Blueprint(
		val id: Int,
		val oreRobot: Robot,
		val clayRobot: Robot,
		val obsidianRobot: Robot,
		val geodeRobot: Robot,
	) {
		val maxResources: Resources = Resources(
			maxOf(oreRobot.needs.ore, clayRobot.needs.ore, obsidianRobot.needs.ore, geodeRobot.needs.ore),
			maxOf(oreRobot.needs.clay, clayRobot.needs.clay, obsidianRobot.needs.clay, geodeRobot.needs.clay),
			maxOf(
				oreRobot.needs.obsidian,
				clayRobot.needs.obsidian,
				obsidianRobot.needs.obsidian,
				geodeRobot.needs.obsidian
			),
			Int.MAX_VALUE
		)
	}

	data class Robot(
		val needs: Resources,
		val provides: Resources
	)

	data class Resources(
		val ore: Ore = 0,
		val clay: Clay = 0,
		val obsidian: Obsidian = 0,
		val geode: Geode = 0,
	) {
		operator fun plus(other: Resources) = Resources(
			ore = ore + other.ore,
			clay = clay + other.clay,
			obsidian = obsidian + other.obsidian,
			geode = geode + other.geode
		)

		operator fun times(i: Int) = Resources(
			ore = i * ore,
			clay = i * clay,
			obsidian = i * obsidian,
			geode = i * geode
		)

		operator fun minus(other: Resources) = Resources(
			ore = ore - other.ore,
			clay = clay - other.clay,
			obsidian = obsidian - other.obsidian,
			geode = geode - other.geode
		)
	}
}

typealias Ore = Int
typealias Clay = Int
typealias Obsidian = Int
typealias Geode = Int
