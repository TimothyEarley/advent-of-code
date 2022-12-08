package de.earley.adventofcode2021.day15

import de.earley.adventofcode.Point
import de.earley.adventofcode.grid
import de.earley.adventofcode2021.readResource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// ffmpeg -framerate 50 -i visualisations/day15/out%06d.png -c:v libx265 Day15vis.mp4

fun main() {

	val grid = Day15.readResource("input.txt").useLines(Day15::parseInput)
	val bigGrid = grid(grid.width * 5, grid.height * 5) {
		Day15.tiledGet(grid, 5)(it)
	}

	val image = BufferedImage(bigGrid.width, bigGrid.height, BufferedImage.TYPE_INT_RGB).apply {
		bigGrid.indices.forEach {
			setRGB(it.x, it.y, 0x000000)
		}
	}

	val dir = File("./visualisations/day15").apply {
		deleteRecursively()
		mkdirs()
	}

	val visitedMod = 100
	val visitedColor = IntArray(visitedMod) { i ->
		0xff0000 + 0x0000ff * i / (visitedMod - 1)
	}

	var i = 0
	aStar(bigGrid::get, Point(0, 0), Point(bigGrid.width - 1, bigGrid.height - 1)) {
		val c = visitedColor[(it.cost + it.heuristic) % visitedMod]
		image.setRGB(it.value.x, it.value.y, c)
		if (i % 100 == 0)
			ImageIO.write(image, "png", File(dir, "out${(i / 100).toString().padStart(6, '0')}.png"))
		i++
	}
	println(i)
}
