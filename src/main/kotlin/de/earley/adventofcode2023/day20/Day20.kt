package de.earley.adventofcode2023.day20

import de.earley.adventofcode.BaseSolution
import de.earley.adventofcode.lcm
import de.earley.adventofcode.mapToList

fun main() = Day20.start()

private typealias ModuleName = String

object Day20 : BaseSolution<List<Day20.Module>, Long, Long>() {

	override fun parseInput(input: Sequence<String>): List<Module> = input.mapToList { line ->
		val (l, r) = line.split(" -> ")
		val type = when {
			l == "broadcaster" -> ModuleType.Broadcast
			l.startsWith('%') -> ModuleType.FlipFlop
			l.startsWith('&') -> ModuleType.Conjunction
			else -> error("")
		}
		Module(
			type,
			l.trimStart('%', '&'),
			r.split(", ")
		)
	}

	data class Module(
		val type: ModuleType,
		val name: ModuleName,
		val connectedTo: List<String>,
	)

	enum class ModuleType {
		FlipFlop, Conjunction, Broadcast
	}

	enum class Signal {
		High, Low
	}

	data class SendSignal(
		val from: ModuleName?,
		val to: ModuleName,
		val signal: Signal,
	)

	data class SignalCount(
		val low: Long,
		val high: Long,
	) {
		operator fun plus(signal: Signal): SignalCount = when (signal) {
			Signal.High -> SignalCount(low, high + 1)
			Signal.Low -> SignalCount(low + 1, high)
		}
	}

	sealed interface ModuleState
	data class BroadcastModuleState(
		val module: Module,
	) : ModuleState

	data class FlipFlopModuleState(
		val module: Module,
		var on: Boolean,
	) : ModuleState

	data class ConjunctionModuleState(
		val module: Module,
		val inputs: MutableMap<ModuleName, Signal>,
	) : ModuleState

	override fun partOne(data: List<Module>): Long {
		val map: Map<ModuleName, ModuleState> = toStateMap(data)
		val signals = (1..1000).fold(SignalCount(0, 0)) { acc, _ ->
			map.handleSignals(
				listOf(SendSignal(null, "broadcaster", Signal.Low)),
				acc
			).first
		}

		return signals.low * signals.high
	}

	private fun toStateMap(data: List<Module>) = data.associate { mod ->
		mod.name to when (mod.type) {
			ModuleType.FlipFlop -> FlipFlopModuleState(mod, false)
			ModuleType.Conjunction -> ConjunctionModuleState(
				mod,
				data.filter { mod.name in it.connectedTo }.map { it.name }.associateWith { Signal.Low }
					.toMutableMap()
			)
			ModuleType.Broadcast -> BroadcastModuleState(mod)
		}
	}

	private tailrec fun Map<ModuleName, ModuleState>.handleSignals(
		sendSignals: List<SendSignal>,
		count: SignalCount,
		allSignals: List<SendSignal> = sendSignals,
	): Pair<SignalCount, List<SendSignal>> {
		if (sendSignals.isEmpty()) {
			return count to allSignals
		}
		val sendSignal = sendSignals.first()
		val newSignals = this[sendSignal.to]?.signals(sendSignal) ?: emptyList()
		return handleSignals(
			sendSignals.drop(1) + newSignals,
			count + sendSignal.signal,
			allSignals + newSignals
		)
	}

	private fun ModuleState.signals(sendSignal: SendSignal): List<SendSignal> = when (this) {
		is BroadcastModuleState -> module.sendAll(sendSignal.signal)
		is ConjunctionModuleState -> {
			inputs[sendSignal.from!!] = sendSignal.signal
			if (inputs.all { it.value == Signal.High }) {
				module.sendAll(Signal.Low)
			} else {
				module.sendAll(Signal.High)
			}
		}
		is FlipFlopModuleState -> when (sendSignal.signal) {
			Signal.High -> emptyList()
			Signal.Low -> {
				on = !on
				module.sendAll(if (on) Signal.High else Signal.Low)
			}
		}
	}
	private fun Module.sendAll(signal: Signal): List<SendSignal> =
		connectedTo.map { SendSignal(name, it, signal) }

	/**
	 * Assumption: rx is connected to a single conjunction module.
	 * To find it is low we check the period of its inputs (again assuming a lot),
	 * which allows us to use the lcm.
	 *
	 * This is a pretty terrible solution, there must be a way to track the period of all
	 * modules easily.
	 */
	override fun partTwo(data: List<Module>): Long {
		val map: Map<ModuleName, ModuleState> = toStateMap(data)

		var timesPressed = 0L
		val rxPrev = data.single { "rx" in it.connectedTo }
		val connectedToRx = data.count { rxPrev.name in it.connectedTo }
		val delta = mutableMapOf<ModuleName, Long>()
		while (true) {
			timesPressed++
			val result = map.handleSignals(
				listOf(SendSignal(null, "broadcaster", Signal.Low)),
				SignalCount(0, 0)
			)
			result.second
				.filter { it.to == rxPrev.name && it.signal == Signal.High }
				.forEach {
					requireNotNull(it.from)
					if (delta[it.from] == null) {
						delta[it.from] = timesPressed
					}
				}
			// we have found all loops
			if (delta.size == connectedToRx) break
		}
		return delta.values.reduce(::lcm)
	}
}
