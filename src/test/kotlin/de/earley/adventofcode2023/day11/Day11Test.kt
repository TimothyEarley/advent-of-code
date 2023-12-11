package de.earley.adventofcode2023.day11

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day11Test : WordSpec({
	include(testDay(Day11(100), 374, 8410))
})
