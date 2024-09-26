import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.system.measureTimeMillis

class AStar(val maze: Maze, comparator: (a:Tile,b:Tile)->Int) {
    val closed: HashSet<Tile> = HashSet()
    private val open: PriorityQueue<Tile> = PriorityQueue(comparator)
    private val hso: HashSet<Tile> =
        HashSet() // hashset that mirrors open pq, used for o(1) checks for if tile is in open
    var tilesExpanded = 0
    private var cur: Tile = maze.maze[maze.start.second][maze.start.first]

    private fun h(t: Tile): Int {
        return abs(maze.goal.x - t.x) + abs(maze.goal.y - t.y)
    }

    fun run() {
        cur.f = Pair(h(cur), 1)
        open.add(cur)
        aStar()
    }

    private fun aStar() {
        while (cur != maze.goal && open.isNotEmpty()) {
            closed.add(cur)

            adjacent(cur).forEach { i ->
                i.f = Pair(h(i), cur.f!!.second + 1)
                if (!hso.contains(i)) {
                    open.add(i)
                    hso.add(i)
                    tilesExpanded++
                }
            }
            cur = open.remove()
            hso.remove(cur)
        }
    }

    private fun adjacent(t: Tile): List<Tile> {
        val l = mutableListOf<Tile>()
        if (t.x < 100 && !maze[t.y, t.x + 1].blocked && !closed.contains(maze[t.y, t.x + 1])) {
            l.add(maze[t.y, t.x + 1])
        }
        if (t.y < 100 && !maze[t.y + 1, t.x].blocked && !closed.contains(maze[t.y + 1, t.x])) {
            l.add(maze[t.y + 1, t.x])
        }
        if (t.x > 0 && !maze[t.y, t.x - 1].blocked && !closed.contains(maze[t.y, t.x - 1])) {
            l.add(maze[t.y, t.x - 1])
        }
        if (t.y > 0 && !maze[t.y - 1, t.x].blocked && !closed.contains(maze[t.y - 1, t.x])) {
            l.add(maze[t.y - 1, t.x])
        }
        return l
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (row in maze.maze) {
            for (i in row) {
                if (i.blocked) sb.append("#")
                else if (i == maze[maze.start.second, maze.start.first]) sb.append("O")
                else if (closed.contains(i)) sb.append("X")
                else if (i == maze.goal) sb.append("!")
                else sb.append("_")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

fun main() {

    val m = Maze()

    m.generateMaze()
    val a = AStar(m,Tile::compareLargeG)
    a.run()
    println("a took ${a.tilesExpanded}")
    val b = AStar(m,Tile::compareSmallG)
    b.run()
    println("b took ${b.tilesExpanded}")





}