package de.earley.adventofcode2024.day18

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day18Test : WordSpec({
	include(testDay(Day18(7, 7,  11), 22, "6,1"))
})
