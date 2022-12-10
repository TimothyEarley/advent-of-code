package de.earley.adventofcode2022.day9

import de.earley.adventofcode.Point
import de.earley.adventofcode2021.readResource
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// ffmpeg -framerate 50 -i visualisations/2022/day9/out%06d.png -c:v libx265 Day9vis.mp4

fun main() {

	val motions = Day9.readResource("input.txt").useLines(Day9::parseInput)

	val minX = -200
	val maxX = 200
	val minY = -200
	val maxY = 200
	val width = maxX - minX
	val height = maxY - minY
	val adjust = Point(204, 204)

	fun BufferedImage.clear() {
		(0 until width).forEach { x ->
			(0 until height).forEach { y ->
				setRGB(x, y, 0)
			}
		}
	}

	val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB).apply {
		clear()
	}



	val dir = File("./visualisations/2022/day9").apply {
		deleteRecursively()
		mkdirs()
	}

	var i = 0
	Day9.simulateRope(motions, 9) { state ->
		image.clear()
		val headOff = state.head + adjust
		image.setRGB(headOff.x, headOff.y, 0xff00ff)
		state.tails.forEach {
			val off = it + adjust
			image.setRGB(off.x, off.y, 0xff00ff)
		}
		ImageIO.write(image, "png", File(dir, "out${(i++).toString().padStart(6, '0')}.png"))
	}
}
