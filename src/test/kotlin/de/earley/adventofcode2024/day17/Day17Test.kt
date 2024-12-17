package de.earley.adventofcode2024.day17

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day17Test : WordSpec({
	include(testDay(Day17, "4,6,3,5,6,3,5,2,1,0", 117440, separateInput = true))
})
