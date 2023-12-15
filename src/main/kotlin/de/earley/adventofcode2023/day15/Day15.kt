package de.earley.adventofcode2023.day15

import de.earley.adventofcode.BaseSolution

fun main() = Day15.start()

private typealias Label = String
private typealias FocalLength = Int

object Day15 : BaseSolution<List<String>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.flatMap { line ->
		line.split(',')
	}.toList()

	override fun partOne(data: List<String>): Long = data.sumOf {
		hash(it).toLong()
	}

	private tailrec fun hash(s : String, current : Int = 0): Int {
		if (s.isEmpty()) return current
		val ascii = s.first().code
		val next = ((current + ascii) * 17).rem(256)
		return hash(s.drop(1), next)
	}

	override fun partTwo(data: List<String>): Long {
		val boxes = Array(256) { mutableListOf<Pair<Label, FocalLength>>() }

		data.forEach { step ->
			val (label, focal) = step.split('=', '-')
			val eq = step.contains('=')
			val box = hash(label)
			when (eq) {
				false -> {
					// remove from box
					boxes[box].removeIf { it.first == label }
				}
				true -> {
					val focalLength = focal.toInt()
					val indexOf = boxes[box].indexOfFirst { it.first == label }
					if (indexOf >= 0) {
						boxes[box][indexOf] = label to focalLength
					} else {
						boxes[box].add(label to focalLength)
					}
				}
			}
		}

		return boxes.withIndex().sumOf { (box, lenses) ->
			lenses.withIndex().sumOf { (slot, lens) ->
				(1 + box) * (slot + 1) * lens.second.toLong()
			}
		}
	}

}
