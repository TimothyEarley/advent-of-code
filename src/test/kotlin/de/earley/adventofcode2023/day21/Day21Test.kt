package de.earley.adventofcode2023.day21

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day21Test : WordSpec({
	include(testDay(Day21(6), 16, 620348631910321L, true))
})
