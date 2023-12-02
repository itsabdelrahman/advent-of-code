package twenty.three

import java.io.File

fun main() {
    val games = File("src/main/resources/twenty/three/02/1.txt")
        .readLines()
        .map(Game::from)

    val output1 = games.filter {
        it.isPossibleWith(
            totalRed = 12,
            totalGreen = 13,
            totalBlue = 14,
        )
    }.sumOf {
        it.id
    }

    println(output1)

    val output2 = games.sumOf {
        val (maximumRedCount, maximumGreenCount, maximumBlueCount) = Triple(
            it.maximumCountOf(CubeColor.RED),
            it.maximumCountOf(CubeColor.GREEN),
            it.maximumCountOf(CubeColor.BLUE),
        )

        maximumRedCount * maximumGreenCount * maximumBlueCount
    }

    println(output2)
}

data class Game(
    val id: Int,
    val sets: List<GameSet>,
) {
    fun isPossibleWith(
        totalRed: Int,
        totalGreen: Int,
        totalBlue: Int,
    ): Boolean = sets.all {
        it.isPossibleWith(
            red = totalRed,
            green = totalGreen,
            blue = totalBlue,
        )
    }

    fun maximumCountOf(color: CubeColor): Int = sets.maxOf { set ->
        when (color) {
            CubeColor.RED -> set.red
            CubeColor.GREEN -> set.green
            CubeColor.BLUE -> set.blue
        }
    }

    companion object {
        // Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
        fun from(text: String): Game {
            val (gameWithId, gameSets) = text.split(": ")

            val id = gameWithId.replace("Game ", "").toInt()
            val sets = gameSets.split("; ").map(GameSet::from)

            return Game(id = id, sets = sets)
        }
    }
}

data class GameSet(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    fun isPossibleWith(
        red: Int,
        green: Int,
        blue: Int,
    ): Boolean = this.red <= red && this.green <= green && this.blue <= blue

    companion object {
        // 3 blue, 4 red
        fun from(text: String): GameSet {
            val cubes = text.split(", ").map(Cube::from)
            val cubesIndexedByColor = cubes.associateBy { it.color }

            val redCube = cubesIndexedByColor[CubeColor.RED]
            val greenCube = cubesIndexedByColor[CubeColor.GREEN]
            val blueCube = cubesIndexedByColor[CubeColor.BLUE]

            return GameSet(
                red = redCube?.count ?: 0,
                green = greenCube?.count ?: 0,
                blue = blueCube?.count ?: 0,
            )
        }
    }
}

data class Cube(
    val count: Int,
    val color: CubeColor,
) {
    companion object {
        // 3 blue
        fun from(text: String): Cube {
            val (count, color) = text.split(" ")
            return Cube(count = count.toInt(), CubeColor.from(color))
        }
    }
}

enum class CubeColor {
    RED,
    GREEN,
    BLUE;

    companion object {
        fun from(text: String): CubeColor = valueOf(text.uppercase())
    }
}
