package de.earley.adventofcode2023.day19

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.split

fun main() = Day19.start()

private typealias WorkflowMap = Map<String, Workflow>
private typealias Workflow = List<Day19.Rule>

object Day19 : BaseSolution<Pair<WorkflowMap, List<Day19.Part>>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): Pair<WorkflowMap, List<Part>> =
		input.toList().split { it.isBlank() }.let { (rules, parts) ->
			rules.associate { rule ->
				val (key, rest) = rule.split("{")
				val ruleEntries: List<Rule> = rest.removeSuffix("}").split(",")
					.map {
						if (it.contains(':')) {
							val (c, v, d) = it.split('<', '>', ':')
							CheckRule(
								c.single(),
								if (it.contains('>')) Op.Greater else Op.Less,
								v.toInt(),
								d
							)
						} else {
							EndRule(it)
						}
					}
				key to ruleEntries
			} to
				parts.map { part ->
					val (x, m, a, s) = part.removePrefix("{").removeSuffix("}").split(",")
						.map { it.split("=")[1].toInt() }
					Part(x, m, a, s)
				}
		}

	sealed interface Rule
	data class CheckRule(
		val check: Char,
		val op: Op,
		val value: Int,
		val destination: String,
	) : Rule

	data class EndRule(val destination: String) : Rule

	enum class Op(val compare: (Int, Int) -> Boolean) {
		Less({ a, b -> a < b }),
		Greater({ a, b -> a > b }),
	}

	data class Part(
		val x: Int,
		val m: Int,
		val a: Int,
		val s: Int,
	)

	override fun partOne(data: Pair<WorkflowMap, List<Part>>): Long = data.second
		.filter { data.first.accepts(it, "in") }
		.sumOf { (it.x + it.m + it.a + it.s).toLong() }

	private tailrec fun WorkflowMap.accepts(part: Part, ruleName: String): Boolean {
		when (ruleName) {
			"A" -> return true
			"R" -> return false
			else -> {
				val rules = this[ruleName]!!
				var i = 0
				while (true) {
					return when (val rule = rules[i++]) {
						is EndRule -> accepts(part, rule.destination)
						is CheckRule ->
							if (rule.applies(part)) {
								accepts(part, rule.destination)
							} else {
								continue
							}
					}
				}
			}
		}
	}

	private fun CheckRule.applies(part: Part): Boolean {
		val v = when (check) {
			'x' -> part.x
			'm' -> part.m
			'a' -> part.a
			's' -> part.s
			else -> error("")
		}
		return op.compare(v, value)
	}

	override fun partTwo(data: Pair<WorkflowMap, List<Part>>): Long {
		val ranges = data.first.filterAccepts(
			listOf(PartRange(1..4000, 1..4000, 1..4000, 1..4000)),
			"in"
		)

		return ranges.sumOf {
			(it.x.last - it.x.first + 1).toLong() *
				(it.m.last - it.m.first + 1).toLong() *
				(it.a.last - it.a.first + 1).toLong() *
				(it.s.last - it.s.first + 1).toLong()
		}
	}

	data class PartRange(
		val x: IntRange,
		val m: IntRange,
		val a: IntRange,
		val s: IntRange,
	)

	private fun WorkflowMap.filterAccepts(parts: List<PartRange>, ruleName: String): List<PartRange> {
		return when (ruleName) {
			"A" -> parts
			"R" -> emptyList()
			else -> {
				val rules = this[ruleName]!!
				val result = mutableListOf<PartRange>()
				val current = rules.fold(parts) { current, rule ->
					when (rule) {
						is CheckRule -> {
							when (rule.op) {
								Op.Less -> {
									val less = current.mapNotNull { pr ->
										when (rule.check) {
											'x' -> pr.copy(x = pr.x.first..< rule.value).takeIf { !it.x.isEmpty() }
											'm' -> pr.copy(m = pr.m.first..< rule.value).takeIf { !it.m.isEmpty() }
											'a' -> pr.copy(a = pr.a.first..< rule.value).takeIf { !it.a.isEmpty() }
											's' -> pr.copy(s = pr.s.first..< rule.value).takeIf { !it.s.isEmpty() }
											else -> error("")
										}
									}
									val greaterOrEqual = current.mapNotNull { pr ->
										when (rule.check) {
											'x' -> pr.copy(x = rule.value..pr.x.last).takeIf { !it.x.isEmpty() }
											'm' -> pr.copy(m = rule.value..pr.m.last).takeIf { !it.m.isEmpty() }
											'a' -> pr.copy(a = rule.value..pr.a.last).takeIf { !it.a.isEmpty() }
											's' -> pr.copy(s = rule.value..pr.s.last).takeIf { !it.s.isEmpty() }
											else -> error("")
										}
									}
									// the less go to the new rule, the others continue
									result += filterAccepts(less, rule.destination)
									greaterOrEqual
								}

								Op.Greater -> {
									val lessOrEqual = current.mapNotNull { pr ->
										when (rule.check) {
											'x' -> pr.copy(x = pr.x.first..rule.value).takeIf { !it.x.isEmpty() }
											'm' -> pr.copy(m = pr.m.first..rule.value).takeIf { !it.m.isEmpty() }
											'a' -> pr.copy(a = pr.a.first..rule.value).takeIf { !it.a.isEmpty() }
											's' -> pr.copy(s = pr.s.first..rule.value).takeIf { !it.s.isEmpty() }
											else -> error("")
										}
									}
									val greater = current.mapNotNull { pr ->
										when (rule.check) {
											'x' -> pr.copy(x = rule.value + 1..pr.x.last).takeIf { !it.x.isEmpty() }
											'm' -> pr.copy(m = rule.value + 1..pr.m.last).takeIf { !it.m.isEmpty() }
											'a' -> pr.copy(a = rule.value + 1..pr.a.last).takeIf { !it.a.isEmpty() }
											's' -> pr.copy(s = rule.value + 1..pr.s.last).takeIf { !it.s.isEmpty() }
											else -> error("")
										}
									}
									// the greater go to the new rule, the others continue
									result += filterAccepts(greater, rule.destination)
									lessOrEqual
								}
							}
						}

						is EndRule -> {
							result += filterAccepts(current, rule.destination)
							emptyList()
						}
					}
				}
				assert(current.isEmpty())
				result
			}
		}
	}
}
