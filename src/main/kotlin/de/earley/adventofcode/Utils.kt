package de.earley.adventofcode

fun <A, B> ((A) -> B).cache(): (A) -> B {
	val cache = mutableMapOf<A, B>()

	return {
		cache.getOrPut(it) {
			this(it)
		}
	}
}
