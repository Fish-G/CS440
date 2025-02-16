package A1A3

import kotlin.random.Random

data class Tile(
    val x: Int,
    val y: Int,
    var visited: Boolean = false,
    var blocked: Boolean = false,
    var isGoal: Boolean = false,
    var f: Pair<Int, Int>? = null // (g,c)
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Tile) return false
        return this.x == other.x && this.y == other.y
    }

    override fun toString(): String {
        return "(${x},${y})"
    }

    override fun hashCode(): Int {
        val t = y + ((x + 1) / 2)
        return x + t * t
    }

    companion object {
        fun compareLargeG(a: Tile, b: Tile): Int {
            if (a.f!!.first + a.f!!.second == b.f!!.first + b.f!!.second) return b.f!!.second.compareTo(a.f!!.second)
            return (a.f!!.first + a.f!!.second).compareTo(b.f!!.first + b.f!!.second)
        }

        fun compareSmallG(a: Tile, b: Tile): Int {
            if (a.f!!.first + a.f!!.second == b.f!!.first + b.f!!.second) return a.f!!.second.compareTo(b.f!!.second)
            return (a.f!!.first + a.f!!.second).compareTo(b.f!!.first + b.f!!.second)
        }
    }


}

class Maze {
    val maze: Array<Array<Tile>> = Array(101) { i -> Array(101) { j -> Tile(j, i) } }
    var goal: Tile = Tile(-1, -1)
    private val s: Pair<Int, Int> = Pair(Random.nextInt(0, 101), Random.nextInt(0, 101))
    var start: Tile = Tile(-1, -1)
    val traveledNodes = HashSet<Tile>()
    override fun toString(): String {
        val sb = StringBuilder()
        for (r in maze) {
            for (i in r) {
                if (i.blocked || !i.visited) {
                    sb.append("#")
                } else if (i.isGoal) {
                    sb.append("!")
                } else if (i == start) {
                    sb.append("O")
                } else {
                    sb.append("_")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    operator fun get(i: Int, j: Int): Tile {
        return maze[i][j]
    }

    fun generateMaze() {
        generateMaze(s.first, s.second)
        if (traveledNodes.isEmpty()) {
            goal = maze[s.second][s.first]
            start = maze[s.second][s.first]
        } else {
            val g = traveledNodes.random()
            g.isGoal = true
            goal = g
            start = traveledNodes.random()
        }

    }

    private fun generateMaze(x: Int, y: Int) {
        maze[y][x].visited = true
        var pM = calcPossibleNeighbors(x, y)
        while (pM.isNotEmpty()) {
            val tileSelection = pM.random()
            if (Random.nextDouble() <= 0.3) {
                tileSelection.blocked = true
                tileSelection.visited = true
            } else {
                traveledNodes.add(maze[y][x])
                generateMaze(tileSelection.x, tileSelection.y)
            }
            pM = calcPossibleNeighbors(x, y)
        }
    }

    private fun calcPossibleNeighbors(x: Int, y: Int): List<Tile> {
        val t = mutableListOf<Tile>()
        if (x < 100 && !maze[y][x + 1].visited) {
            t.add(maze[y][x + 1])
        }
        if (y < 100 && !maze[y + 1][x].visited) {
            t.add(maze[y + 1][x])
        }
        if (x > 0 && !maze[y][x - 1].visited) {
            t.add(maze[y][x - 1])
        }
        if (y > 0 && !maze[y - 1][x].visited) {
            t.add(maze[y - 1][x])
        }
        return t
    }
}

fun main() {
    val m = Maze()
    m.generateMaze()
    println(m)
}