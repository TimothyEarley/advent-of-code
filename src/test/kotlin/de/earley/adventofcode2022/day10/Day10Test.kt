package de.earley.adventofcode2022.day10

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day10Test : WordSpec({
	include(testDay(Day10, 13140, """
		
		##..##..##..##..##..##..##..##..##..##..
		###...###...###...###...###...###...###.
		####....####....####....####....####....
		#####.....#####.....#####.....#####.....
		######......######......######......####
		#######.......#######.......#######.....
		
	""".trimIndent().replace('#', '█').replace('.', ' ')))
})
