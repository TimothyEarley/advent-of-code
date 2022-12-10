package de.earley.adventofcode2021.day13

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day13Test : WordSpec({
	include(testDay(Day13, 17, """
		#####
		#...#
		#...#
		#...#
		#####
		
	""".trimIndent().replace('#', '█').replace('.', ' ')))
})
