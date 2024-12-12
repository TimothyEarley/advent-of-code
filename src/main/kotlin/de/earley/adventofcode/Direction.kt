package de.earley.adventofcode

enum class Direction(val point: Point) {
	Left(Point(-1, 0)), Right(Point(1, 0)), Up(Point(0, -1)), Down(Point(0, 1));

	companion object {
		fun parseArrow(c: Char) = when (c) {
			'<' -> Left
			'>' -> Right
			'^' -> Up
			'v' -> Down
			else -> error("Unknown direction $c")
		}

		fun parseLetter(c: Char) = when (c) {
			'L' -> Left
			'R' -> Right
			'U' -> Up
			'D' -> Down
			else -> error("Unknown direction $c")
		}

		fun fromPoint(p: Point): Direction? = Direction.entries.find { it.point == p }
	}
}