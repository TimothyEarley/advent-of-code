package de.earley.adventofcode2022.day7

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day7Test : WordSpec({
	include(testDay(Day7a, 95437, 24933642))
	include(testDay(Day7b, 95437, 24933642))
})
