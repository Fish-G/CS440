import java.util.PriorityQueue
import kotlin.math.abs

class AStar(val maze: Maze, comparator: (a: Tile, b: Tile) -> Int, val start: Tile, val goal: Tile) {
    val closed: HashSet<Tile> = HashSet()
    private val open: PriorityQueue<Tile> = PriorityQueue(comparator)
    private val hso: HashSet<Tile> =
        HashSet() // hashset that mirrors open pq, used for o(1) checks for if tile is in open
    var tilesExpanded = 0
    private var cur: Tile = start

    private fun h(t: Tile): Int {
        return abs(goal.x - t.x) + abs(goal.y - t.y)
    }

    fun run() {
        cur.f = Pair(h(cur), 1)
        open.add(cur)
        aStar()
    }

    private fun aStar() {
        while (cur != goal && open.isNotEmpty()) {
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
                else if (i == start) sb.append("O")
                else if (closed.contains(i)) sb.append("X")
                else if (i == maze.goal) sb.append("!")
                else sb.append("_")
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}

class Testers {
    companion object {
        fun runAStar() {
            val maze = Maze()
            maze.generateMaze()
            println("goal: !, start: O, wall: #, empty: _")
            println(maze)
            val aStar = AStar(maze,Tile::compareSmallG,maze.start,maze.goal)
            aStar.run()
            println(aStar)

        }

        fun differenceInGCompare() {
            var delta = 0
            for (i in 0..<10000) {
                val m = Maze()

                m.generateMaze()
                val a = AStar(m, Tile::compareLargeG, m.start, m.goal)
                a.run()
                val b = AStar(m, Tile::compareSmallG, m.start, m.goal)
                b.run()
                delta += a.tilesExpanded - b.tilesExpanded
            }
            println(delta / 1000)
        }

        fun forwardVReverse() {
            var delta = 0
            for (i in 0..<10000) {
                val m = Maze()
                m.generateMaze()
                val forward = AStar(m, Tile::compareSmallG, m.start,m.goal)
                val backward = AStar(m, Tile::compareSmallG, m.goal,m.start)
                forward.run()
                backward.run()
//                println("${forward.tilesExpanded}, ${backward.tilesExpanded}")
                delta += forward.tilesExpanded-backward.tilesExpanded
            }
            println(delta / 10000)
        }

    }
}

fun main() {
    Testers.forwardVReverse()
}