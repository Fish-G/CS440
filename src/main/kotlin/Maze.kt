import kotlin.random.Random

data class Tile(
    val x: Int,
    val y: Int,
    var visited: Boolean = false,
    var blocked: Boolean = false,
    var isGoal: Boolean = false,
    var f: Pair<Int,Int>? = null // (g,c)
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Tile) return false
        return this.x == other.x && this.y == other.y
    }

    override fun toString(): String {
        return "(${x},${y})"
    }

    companion object {
        fun compareLargeG(a:Tile, b:Tile) :Int {
            if (a.f!!.first + a.f!!.second == b.f!!.first + b.f!!.second) return  a.f!!.first.compareTo(b.f!!.first)
            return (a.f!!.first + a.f!!.second).compareTo(b.f!!.first + b.f!!.second)
        }

        fun compareSmallG(a:Tile, b:Tile) : Int {
            if (a.f!!.first + a.f!!.second == b.f!!.first + b.f!!.second) return -a.f!!.first.compareTo(b.f!!.first)
            return (a.f!!.first + a.f!!.second).compareTo(b.f!!.first + b.f!!.second)
        }
    }


}

class Maze {
    val maze: Array<Array<Tile>> = Array(101) { i -> Array(101) { j -> Tile(j, i) } }
    var goal: Tile = Tile(-1,-1)
    val start: Pair<Int, Int> = Pair(Random.nextInt(0, 101), Random.nextInt(0, 101))

    override fun toString(): String {
        val sb = StringBuilder()
        for (r in maze) {
            for (i in r) {
                if (i.blocked) {
                    sb.append("#")
                } else if (i.isGoal) {
                    sb.append("!")
                } else if (i.x == start.first && i.y == start.second) {
                    sb.append("O")
                } else {
                    sb.append("_")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    operator fun get(i:Int, j:Int) : Tile {
        return maze[i][j]
    }

    fun generateMaze() {
        generateMaze(start.first, start.second)

        var x = Random.nextInt(0, 101)
        var y = Random.nextInt(0, 101)
        while (!maze[y][x].visited || maze[y][x].blocked || Pair(x,y) == start) {
            x = Random.nextInt(0, 101)
            y = Random.nextInt(0, 101)
        }
        maze[y][x].isGoal = true
        goal = maze[y][x]
    }

    private fun generateMaze(x: Int, y: Int) {
        maze[y][x].visited = true
        var pM = calcPossibleNeighbors(x, y)
        while (pM.isNotEmpty()) {
            val tileSelection = pM.random()
            if (Random.nextDouble() <= 0.3) {
                tileSelection.blocked = true
                tileSelection.visited = true
            } else generateMaze(tileSelection.x, tileSelection.y)
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