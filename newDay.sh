#! /usr/bin/env bash

year="2022"

set -euo pipefail

function usage() {
  echo "Usage: $0 [day]"
  exit 1
}

function require() {
  if ! command -v "$1" &>/dev/null; then
    echo "$1 could not be found, please install"
    exit 1
  fi
}

function createSolutionFile() {
  f="src/main/kotlin/de/earley/adventofcode${year}/day${day}/Day${day}.kt"
  if [ -f "$f" ]; then
    echo "Skipping solutions file (already exists)"
    return
  fi
  echo "Creating solution file"
  mkdir -p "$(dirname "$f")"
  cat <<EOF >"$f"
package de.earley.adventofcode${year}.day${day}

import de.earley.adventofcode.BaseSolution

fun main() = Day${day}.start()

object Day${day} : BaseSolution<List<String>, Int, Int>() {

	override fun parseInput(input: Sequence<String>): List<String> = input.toList()

	override fun partOne(data: List<String>): Int = 0

	override fun partTwo(data: List<String>): Int = 0

}
EOF
  idea "$f"
}

function createTestFile() {
  f="src/test/kotlin/de/earley/adventofcode${year}/day${day}/Day${day}Test.kt"
  if [ -f "$f" ]; then
    echo "Skipping test file (already exists)"
    return
  fi
  echo "Creating test file"
  mkdir -p "$(dirname "$f")"
  cat <<EOF >"$f"
package de.earley.adventofcode${year}.day${day}

import de.earley.adventofcode.testDay
import io.kotest.core.spec.style.WordSpec

class Day${day}Test : WordSpec({
	include(testDay(Day${day}, 0, 0))
})
EOF
  idea "$f"
}

function createInputFile() {
  f="src/main/resources/de/earley/adventofcode${year}/day${day}/input.txt"
  if [ -f "$f" ]; then
    echo "Skipping input file (already exists)"
    return
  fi
  echo "Creating input file"
  mkdir -p "$(dirname "$f")"
  "${curlCommand[@]}" "https://adventofcode.com/${year}/day/{$day}/input" -o "$f"
}

function createTestInputFile() {
  f="src/test/resources/de/earley/adventofcode${year}/day${day}/testInput.txt"
  mkdir -p "$(dirname "$f")"
  touch "$f"
}

function createTaskFile() {
  echo "Creating task file"

  f="src/main/kotlin/de/earley/adventofcode${year}/day${day}/task.md"
  "${curlCommand[@]}" "https://adventofcode.com/${year}/day/{$day}" |
    pup --pre '.day-desc' |
    pandoc --from=html --to=gfm >"$f"

  idea "$f"
}

function main() {
  require 'pup'
  require 'pandoc'

  createSolutionFile
  createInputFile
  createTestFile
  createTestInputFile
  createTaskFile
}

# get the input
if [ $# -ne 1 ]; then
  usage
fi
day=$1

cookieFile=adventofcode.com_cookies.txt
if [ ! -f "$cookieFile" ]; then
  echo "The cookie file is missing! ($cookieFile)"
  exit 1
fi

cookie=$(cat $cookieFile)
curlCommand=(curl -sS --user-agent github.com/TimothyEarley/advent-of-code -H "cookie: session=$cookie")

main
