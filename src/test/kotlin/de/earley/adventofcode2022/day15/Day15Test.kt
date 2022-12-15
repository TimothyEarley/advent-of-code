package de.earley.adventofcode2022.day15

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day15Test : WordSpec({
	include(testDay(Day15(20), 26, 56000011))
})
