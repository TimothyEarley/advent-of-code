package de.earley.adventofcode2021

import de.earley.adventofcode2021.day1.Day1
import de.earley.adventofcode2021.day10.Day10
import de.earley.adventofcode2021.day11.Day11
import de.earley.adventofcode2021.day12.Day12
import de.earley.adventofcode2021.day13.Day13
import de.earley.adventofcode2021.day14.Day14
import de.earley.adventofcode2021.day15.Day15
import de.earley.adventofcode2021.day16.Day16
import de.earley.adventofcode2021.day17.Day17
import de.earley.adventofcode2021.day18.Day18
import de.earley.adventofcode2021.day19.Day19
import de.earley.adventofcode2021.day2.Day2
import de.earley.adventofcode2021.day20.Day20
import de.earley.adventofcode2021.day21.Day21
import de.earley.adventofcode2021.day22.Day22
import de.earley.adventofcode2021.day23.Day23
import de.earley.adventofcode2021.day24.Day24
import de.earley.adventofcode2021.day25.Day25
import de.earley.adventofcode2021.day3.Day3
import de.earley.adventofcode2021.day4.Day4
import de.earley.adventofcode2021.day5.Day5
import de.earley.adventofcode2021.day6.Day6
import de.earley.adventofcode2021.day7.Day7
import de.earley.adventofcode2021.day8.Day8
import de.earley.adventofcode2021.day9.Day9
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

@State(Scope.Benchmark)
class AllBench {

	@Benchmark fun day1() = Day1.start()
	@Benchmark fun day2() = Day2.start()
	@Benchmark fun day3() = Day3.start()
	@Benchmark fun day4() = Day4.start()
	@Benchmark fun day5() = Day5.start()
	@Benchmark fun day6() = Day6.start()
	@Benchmark fun day7() = Day7.start()
	@Benchmark fun day8() = Day8.start()
	@Benchmark fun day9() = Day9.start()
	@Benchmark fun day10() = Day10.start()
	@Benchmark fun day11() = Day11.start()
	@Benchmark fun day12() = Day12.start()
	@Benchmark fun day13() = Day13.start()
	@Benchmark fun day14() = Day14.start()
	@Benchmark fun day15() = Day15.start()
	@Benchmark fun day16() = Day16.start()
	@Benchmark fun day17() = Day17.start()
	@Benchmark fun day18() = Day18.start()
	@Benchmark fun day19() = Day19.start()
	@Benchmark fun day20() = Day20.start()
	@Benchmark fun day21() = Day21.start()
	@Benchmark fun day22() = Day22.start()
	@Benchmark fun day23() = Day23.start()
	@Benchmark fun day24() = Day24.start()
	@Benchmark fun day25() = Day25.start()

}