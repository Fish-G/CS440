import kotlin.random.Random

data class Tile(val x: Int, val y: Int, var visited: Boolean = false, var blocked: Boolean = false)

class Maze {
    val maze: Array<Array<Tile>> = Array(101) { i -> Array(101) { j -> Tile(j, i) } }

    override fun toString(): String {
        val sb = StringBuilder()
        for (r in maze){
            for (i in r) {
                if (i.blocked) sb.append("#") else sb.append(" ")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
    fun generateMaze() {
        generateMaze(Random.nextInt(0,101).also(::print),Random.nextInt(0,101).also(::print))
    }

    private fun generateMaze(x: Int, y: Int) {
        maze[y][x].visited = true
        var pM = calcPossibleNeighbors(x,y)
        while (pM.isNotEmpty()) {
            val tileSelection = pM.random()
            if (Random.nextDouble() <= 0.3) tileSelection.blocked = true
            else generateMaze(tileSelection.x,tileSelection.y)
            pM = calcPossibleNeighbors(x,y)
        }
    }

    private fun calcPossibleNeighbors(x:Int, y:Int) : List<Tile> {
        val t = mutableListOf<Tile>()
        if (x < 99 && !maze[y][x + 1].visited) {
            t.add(maze[y][x+1])
        }
        if (y < 99 && !maze[y+1][x].visited) {
            t.add(maze[y+1][x])
        }
        if (x > 1 && !maze[y][x - 1].visited) {
            t.add(maze[y][x-1])
        }
        if (y >1 && !maze[y-1][x].visited) {
            t.add(maze[y-1][x])
        }
        return t
    }
}

fun main() {
    val m = Maze()
    m.generateMaze()
    println(m)
}