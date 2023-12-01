package de.earley.adventofcode

fun <T : Any> T.readResource(name: String) =
	this::class.java.getResourceAsStream(name)?.bufferedReader()
		?: error("Failed to read $name for ${this::class.java}")

fun <A, B> ((A) -> B).cache(): (A) -> B {
	val cache = mutableMapOf<A, B>()

	return {
		cache.getOrPut(it) {
			this(it)
		}
	}
}
