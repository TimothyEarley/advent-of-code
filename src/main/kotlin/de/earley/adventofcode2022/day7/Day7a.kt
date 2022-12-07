package de.earley.adventofcode2022.day7

import de.earley.adventofcode.BaseSolution

fun main() = Day7a.start()

object Day7a : BaseSolution<List<Day7a.CommandExec>, Int>() {

	override fun parseInput(input: Sequence<String>): List<CommandExec> =
		input.joinToString("\n").split("$ ").map { it.trim() }.filterNot { it.isBlank() }.map { block ->
			val lines = block.lines()
			val command = lines.first().let {
				if (it == "ls") Ls
				else if (it.startsWith("cd")) Cd(it.substring(3))
				else error("Failed to parse command $it")
			}
			val output = lines.drop(1).map {
				if (it.startsWith("dir")) Dir(it.substring(4))
				else it.split(" ").let { (size, name) ->
					FileListing(size.toInt(), name)
				}
			}
			CommandExec(command, output)
		}

	override fun partOne(data: List<CommandExec>): Int = createDirTree(data).sumSizesAtMost(100000)

	override fun partTwo(data: List<CommandExec>): Int = createDirTree(data).let { root ->
		val currentFreeSpace = 70000000 - root.size
		val needToDelete = 30000000 - currentFreeSpace
		root.findSmallestWithSizeAtLeast(needToDelete)!!
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
							if (existing != null) existing
							else {
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

	private fun SizedDir.sumSizesAtMost(limit: Int): Int {
		val contributingToTotal = if (size <= limit) size else 0
		val childrenContributing = children.filterIsInstance<SizedDir>().sumOf { it.sumSizesAtMost(limit) }
		return contributingToTotal + childrenContributing
	}


	private fun SizedDir.findSmallestWithSizeAtLeast(needToDelete: Int): Int? {
		if (size < needToDelete) return null

		// we can definitely satisfy the requirement. First check if any child can already do it
		val childResult =
			children.filterIsInstance<SizedDir>().mapNotNull { it.findSmallestWithSizeAtLeast(needToDelete) }
				.minOrNull()

		return childResult ?: size
	}
}