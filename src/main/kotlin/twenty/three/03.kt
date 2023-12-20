package twenty.three

import java.io.File
import utilities.andThen

fun main() {
    val characterMatrix: List<List<EngineSchematicCharacter>> = File("src/main/resources/twenty/three/03/1.txt")
        .readLines()
        .map { row -> row.toCharArray().map(EngineSchematicCharacter::from) }

    val symbolAdjacentIndicesMatrix: List<Set<Int>> = characterMatrix.flatMapIndexed { rowIndex, rowOfCharacters ->
        rowOfCharacters.flatMapIndexed { columnIndex, character ->
            (character is EngineSchematicCharacter.Symbol).andThen {
                calculateAdjacentIndices(rowIndex to columnIndex)
            }.orEmpty()
        }
    }.groupBy({ (rowIndex) -> rowIndex }) { (_, columnIndex) ->
        columnIndex
    }.map { (_, columnIndices) ->
        columnIndices.toSet()
    }

    val consecutiveDigitIndicesMatrix: List<List<IntRange>> = characterMatrix.map { rowOfCharacters ->
        rowOfCharacters.indicesOfConsecutive { character -> character is EngineSchematicCharacter.Digit }
    }

    val consecutiveDigitIndicesAdjacentToSymbols: List<List<IntRange>> =
        consecutiveDigitIndicesMatrix.mapIndexed { rowIndex, rowOfConsecutiveDigitIndices: List<IntRange> ->
            rowOfConsecutiveDigitIndices.filter { consecutiveDigitIndices: IntRange ->
                val rowOfSymbolAdjacentIndices: Set<Int> = symbolAdjacentIndicesMatrix.getOrNull(rowIndex) ?: emptySet()
                rowOfSymbolAdjacentIndices.any { it in consecutiveDigitIndices }
            }
        }

    val output = consecutiveDigitIndicesAdjacentToSymbols.mapIndexed { rowIndex, rowOfConsecutiveDigitIndices ->
        rowOfConsecutiveDigitIndices.sumOf { consecutiveDigitIndices ->
            val rowOfCharacters: List<EngineSchematicCharacter> = characterMatrix.getOrNull(rowIndex) ?: emptyList()

            val number: String = rowOfCharacters.slice(consecutiveDigitIndices)
                .filterIsInstance<EngineSchematicCharacter.Digit>()
                .joinToString(separator = "") { character -> character.value.toString() }

            number.toIntOrNull() ?: 0
        }
    }.sum()

    println(output)
}

fun <T> Collection<T>.indicesOfConsecutive(predicate: (T) -> Boolean): List<IntRange> =
    foldIndexed<T, List<IntRange>>(listOf()) { index, clustersSoFar, element ->
        val isClusterElement = predicate(element)
        val lastCluster = clustersSoFar.lastOrNull() ?: IntRange.EMPTY

        val isFirstElementInCluster = isClusterElement && lastCluster.isEmpty()
        val isRightAfterLastElementInCluster = index - lastCluster.last == 1

        when {
            isFirstElementInCluster -> clustersSoFar.dropLast(1).plusElement((index..index))
            isClusterElement -> clustersSoFar.dropLast(1).plusElement((lastCluster.first..index))
            isRightAfterLastElementInCluster -> clustersSoFar.plusElement(IntRange.EMPTY)
            else -> clustersSoFar
        }
    }.filterNot { cluster -> cluster.isEmpty() }

fun calculateAdjacentIndices(index: TwoDimensionalIndex): Set<TwoDimensionalIndex> {
    val (rowIndex, columnIndex) = index

    val horizontalIndices = setOf(
        rowIndex to columnIndex - 1, // left
        rowIndex to columnIndex + 1, // right
    )

    val verticalIndices = setOf(
        rowIndex - 1 to columnIndex, // top
        rowIndex + 1 to columnIndex, // bottom
    )

    val diagonalIndices = setOf(
        rowIndex - 1 to columnIndex - 1, // top left
        rowIndex + 1 to columnIndex - 1, // bottom left
        rowIndex + 1 to columnIndex + 1, // bottom right
        rowIndex - 1 to columnIndex + 1, // top right
    )

    return horizontalIndices + verticalIndices + diagonalIndices
}

sealed class EngineSchematicCharacter {
    companion object {
        fun from(value: Char): EngineSchematicCharacter = when {
            value == '.' -> Dot
            value.isDigit() -> Digit(value)
            else -> Symbol
        }
    }

    data object Dot : EngineSchematicCharacter()
    data object Symbol : EngineSchematicCharacter()
    data class Digit(val value: Char) : EngineSchematicCharacter()
}

typealias TwoDimensionalIndex = Pair<Int, Int>
