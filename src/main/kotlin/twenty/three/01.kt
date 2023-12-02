package twenty.three

import java.io.File

fun main() {
    val stringsToSearchFor = wordsToDigits.keys + wordsToDigits.values

    val output = File("src/main/resources/twenty/three/01/1.txt")
        .readLines()
        .mapNotNull { line ->
            val (_, firstOccurrence) = line.findAnyOf(stringsToSearchFor) ?: return@mapNotNull null
            val (_, lastOccurrence) = line.findLastAnyOf(stringsToSearchFor) ?: return@mapNotNull null

            val firstDigit = wordsToDigits[firstOccurrence] ?: firstOccurrence
            val lastDigit = wordsToDigits[lastOccurrence] ?: lastOccurrence

            (firstDigit + lastDigit).toInt()
        }.sum()

    println(output)
}

val wordsToDigits = mapOf(
    "zero" to "0",
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)
