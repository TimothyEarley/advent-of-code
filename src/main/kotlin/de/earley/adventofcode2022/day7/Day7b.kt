@file:Suppress("DuplicatedCode")

package de.earley.adventofcode2022.day7

import de.earley.adventofcode.BaseSolution

fun main() = Day7a.start()

object Day7b : BaseSolution<List<Day7b.CommandExec>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<CommandExec> =
		input.joinToString("\n").split("$ ").map { it.trim() }.filterNot { it.isBlank() }.map { block ->
			val lines = block.lines()
			val command = lines.first().let {
				when {
					it == "ls" -> Ls
					it.startsWith("cd") -> Cd(it.substring(3))
					else -> error("Failed to parse command $it")
				}
			}
			val output = lines.drop(1).map {
				when {
					it.startsWith("dir") -> Dir(it.substring(4))
					else -> it.split(" ").let { (size, name) ->
						FileListing(size.toInt(), name)
					}
				}
			}
			CommandExec(command, output)
		}

	override fun partOne(data: List<CommandExec>): Int =
		createDirTree(data)
			.dirSizes()
			.filter { it <= 100000 }
			.sum()

	override fun partTwo(data: List<CommandExec>): Int = createDirTree(data).let { root ->
		val currentFreeSpace = 70000000 - root.size
		val needToDelete = 30000000 - currentFreeSpace
		root.dirSizes().filter { it >= needToDelete }.min()
	}

	private fun createDirTree(data: List<CommandExec>): SizedDir {
		val root = MutableDirNode(null, Dir("/"), mutableSetOf())

		data.fold(root) { currentDir, commandExec ->
			when (val command = commandExec.command) {
				is Cd -> {
					when (command.to) {
						"/" -> root
						".." -> currentDir.parent ?: root
						else -> {
							val existing = currentDir.children
								.filterIsInstance<MutableDirNode>()
								.find { it.dir.name == command.to }
							if (existing != null) {
								existing
							} else {
								val next = MutableDirNode(currentDir, Dir(command.to), mutableSetOf())
								currentDir.children.add(next)
								next
							}
						}
					}
				}

				Ls -> {
					val files = commandExec.result.filterIsInstance<FileListing>().map { FileNode(it) }
					currentDir.children.addAll(files)
					// dirs are added once we `cd` into them
					// stay in dir
					currentDir
				}
			}
		}
		return root.sized() as SizedDir
	}

	sealed interface Command
	object Ls : Command
	data class Cd(val to: String) : Command

	data class CommandExec(val command: Command, val result: List<Listing>)

	sealed interface Listing
	data class Dir(val name: String) : Listing
	data class FileListing(val size: Int, val name: String) : Listing

	sealed interface MutableFS
	class MutableDirNode(val parent: MutableDirNode?, val dir: Dir, val children: MutableSet<MutableFS>) : MutableFS {
		override fun toString(): String {
			return "$dir: $children"
		}
	}

	data class FileNode(val file: FileListing) : MutableFS

	sealed interface SizedFS {
		val size: Int
	}

	data class SizedDir(override val size: Int, val dir: Dir, val children: List<SizedFS>) : SizedFS
	data class SizedFileNode(val file: FileListing) : SizedFS {
		override val size: Int = file.size
	}

	private fun MutableFS.sized(): SizedFS = when (this) {
		is FileNode -> SizedFileNode(file)
		is MutableDirNode -> {
			val sizedChildren = children.map { it.sized() }
			SizedDir(sizedChildren.sumOf { it.size }, dir, sizedChildren)
		}
	}

	private fun SizedFS.dirSizes(): List<Int> = when (this) {
		is SizedDir -> children.flatMap { it.dirSizes() } + size
		is SizedFileNode -> emptyList()
	}
}
