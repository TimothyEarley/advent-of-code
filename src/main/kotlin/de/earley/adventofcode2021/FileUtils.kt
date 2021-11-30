package de.earley.adventofcode2021

fun <T : Any> T.readResource(name: String) =
	this::class.java.getResourceAsStream(name)?.bufferedReader()
		?: error("Failed to read $name for ${this::class.java}")
