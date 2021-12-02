#! /bin/bash

set -euo pipefail

# Create the files for a new day

if [ -z "$1" ]; then
  echo "Usage: $0 [day]"
  exit 1
fi

day=$1

if [ -f "src/main/kotlin/de/earley/adventofcode2021/day${day}/Day${day}.kt" ]; then
  echo "Refusing to override files"
  exit 1
fi

install -D /dev/null src/main/kotlin/de/earley/adventofcode2021/day"${day}"/Day"${day}".kt
install -D /dev/null src/main/resources/de/earley/adventofcode2021/day"${day}"/input.txt
install -D /dev/null src/test/kotlin/de/earley/adventofcode2021/day"${day}"/Day"${day}Test".kt
install -D /dev/null src/test/resources/de/earley/adventofcode2021/day"${day}"/testInput.txt


cat <<EOF > src/main/kotlin/de/earley/adventofcode2021/day"${day}"/Day"${day}".kt
package de.earley.adventofcode2021.day${day}

import de.earley.adventofcode2021.BaseSolution

fun main() = Day${day}.start()

object Day${day} : BaseSolution<List<String>>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int = 0

	override fun partTwo(data: List<String>): Int = 0

}
EOF

cat <<EOF > src/test/kotlin/de/earley/adventofcode2021/day"${day}"/Day"${day}Test".kt
package de.earley.adventofcode2021.day${day}

import de.earley.adventofcode2021.testDay
import io.kotest.core.spec.style.WordSpec

class Day${day}Test : WordSpec({
	include(testDay(Day${day}, 0, 0))
})

EOF