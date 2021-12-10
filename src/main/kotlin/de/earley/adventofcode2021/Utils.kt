package de.earley.adventofcode2021

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

fun <A, B> Sequence<A>.mapToList(f: (A) -> B): List<B> = map(f).toList()

fun <T : Comparable<T>> List<T>.median(): T = sorted()[size / 2]
