import kotlin.random.Random

data class Tile(val x: Int, val y: Int, var visited: Boolean = false, var blocked: Boolean = false, var isGoal:Boolean = false)

class Maze {
    val maze: Array<Array<Tile>> = Array(101) { i -> Array(101) { j -> Tile(j, i) } }
    var goal: Pair<Int,Int> = Pair(0,0)
    val start: Pair<Int,Int> = Pair(Random.nextInt(0,101),Random.nextInt(0,101))

    override fun toString(): String {
        val sb = StringBuilder()
        for (r in maze){
            for (i in r) {
                if (i.blocked) {
                    sb.append("#")
                } else if(i.isGoal) {
                    sb.append("!")
                } else if(i.x == start.first && i.y == start.second) {
                    sb.append("O")
                } else {
                    sb.append("_")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
    fun generateMaze() {
        generateMaze(start.first,start.second)
        var x = Random.nextInt(0,101)
        var y = Random.nextInt(0,101)
        while(!maze[y][x].visited) {
            x = Random.nextInt(0,101)
            y = Random.nextInt(0,101)
        }
        maze[y][x].isGoal = true
        goal= Pair(x,y)
    }

    private fun generateMaze(x: Int, y: Int) {
        maze[y][x].visited = true
        var pM = calcPossibleNeighbors(x,y)
        while (pM.isNotEmpty()) {
            val tileSelection = pM.random()
            if (Random.nextDouble() <= 0.3) {
                tileSelection.blocked = true
                tileSelection.visited = true
            } else generateMaze(tileSelection.x,tileSelection.y)
            pM = calcPossibleNeighbors(x,y)
        }
    }

    private fun calcPossibleNeighbors(x:Int, y:Int) : List<Tile> {
        val t = mutableListOf<Tile>()
        if (x < 100 && !maze[y][x + 1].visited) {
            t.add(maze[y][x+1])
        }
        if (y < 100 && !maze[y+1][x].visited) {
            t.add(maze[y+1][x])
        }
        if (x > 0 && !maze[y][x - 1].visited) {
            t.add(maze[y][x-1])
        }
        if (y >0 && !maze[y-1][x].visited) {
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