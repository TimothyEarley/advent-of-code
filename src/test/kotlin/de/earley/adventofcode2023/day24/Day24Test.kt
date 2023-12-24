package de.earley.adventofcode2023.day24

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day24Test : WordSpec({
	include(testDay(Day24(7, 27), 2, 47))
})
