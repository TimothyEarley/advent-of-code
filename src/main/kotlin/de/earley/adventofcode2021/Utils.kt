package de.earley.adventofcode2021

fun <T : Comparable<T>> List<T>.median(): T = sorted()[size / 2]

infix fun Int.modStart1(m: Int): Int = (this + m - 1) % m + 1
