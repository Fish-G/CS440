import java.util.*
import kotlin.collections.HashSet
import kotlin.math.abs

class A3 {
    data class Tile(val id: Int, var v: Double)
    data class State(val robot: Pair<Int, Int>, val bull: Pair<Int, Int>, var value: Double? = null) // (x,y)

    val allStates = Array(12) { Array(12) { Array(12) { Array(12) { State(Pair(-1, -1), Pair(-1, -1)) } } } }

    init {
        (0..12).forEach { rx ->
            (0..12).forEach { ry ->
                (0..12).forEach { bx ->
                    (0..12).forEach { by ->
                        allStates[rx][ry][bx][by] = State(Pair(rx, ry), Pair(bx, by))
                    }
                }
            }
        }
    }

    class StateSet() {
        val rx = HashSet<Int>()
        val ry = HashSet<Int>()
        val bx = HashSet<Int>()
        val by = HashSet<Int>()
        fun add(s: State) {
            rx.add(s.robot.first)
            ry.add(s.robot.second)
            bx.add(s.bull.first)
            by.add(s.bull.second)
        }

        fun contains(s: State) =
            rx.contains(s.robot.first) && ry.contains(s.robot.second) && bx.contains(s.bull.first) && by.contains(s.bull.second)
    }


    fun manhattan(a: Pair<Int, Int>, b: Pair<Int, Int>) = abs(a.first - b.first) + abs(a.second - b.second)
    fun withinBounds(x: Int, y: Int) =
        (x in 0..12) && (y in 0..12) && !(x == 5 && y in 5..7) && !(x == 6 && y == 7) && !(x == 7 && y in 5..7)

    fun adjacentTiles(initial: Pair<Int, Int>, diagonal: Boolean = false): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        fun add(r: Int, c: Int) {
            if (withinBounds(r, c)) ret.add(Pair(r, c))
        }
        val (r, c) = initial
        add(r + 1, c)
        add(r - 1, c)
        add(r, c + 1)
        add(r, c - 1)
        if (diagonal) {
            add(r + 1, c + 1)
            add(r + 1, c - 1)
            add(r - 1, c + 1)
            add(r - 1, c - 1)
        }
        return ret
    }

    fun adjacentStates(state: State): List<State> {
        val adj = mutableListOf<State>()
        val (robot, bull) = state

        val adjRob = adjacentTiles(robot, true)

        for (r in adjRob) {
            if (manhattan(r, bull) <= 5) {
                adjacentTiles(bull).filter { manhattan(it, r) <= manhattan(r, bull) }.forEach { adj.add(State(r, it)) }
            } else {
                adjacentTiles(bull).forEach { adj.add(State(r, it)) }
            }
        }
        return adj.toList()
    }

    fun robotMoves(state:State) : Int = adjacentTiles(state.robot,true).size
    fun bullMoves(state:State) : Int {
        if (manhattan(state.robot,state.bull) <= 5) return adjacentTiles(state.bull).filter { manhattan(it,state.robot) <= manhattan(state.robot,state.bull) }.size
        return adjacentTiles(state.bull).size
    }

    fun isGoalState(s:State) :Boolean = (s.bull.first == 6) && (s.bull.first == 6)


    fun calculateBellman(start: State) {
        for (i in 0..<100) {
            val visited = StateSet().also { it.add(start) }
            val queue: Queue<State> = LinkedList()
            while (queue.isNotEmpty()) {
                val cur = queue.remove()
                adjacentStates(cur).filter { !visited.contains(it) }.forEach {
                    visited.add(it)
                    queue.add(it)
                }

                val (rx,ry) = cur.robot
                val (bx,by) = cur.bull
                val bm = bellman(cur) //.also { it.add(cur.value) }
                allStates[rx][ry][bx][by].value = if (bm.filterNotNull().isEmpty()) null else bm.filterNotNull().min()
            }
        }
    }

    private fun t(s:State, sp:State) : Double{
        return (1.0/robotMoves(s)) * (1.0/bullMoves(sp)) //might not be sp
    }

    private fun r(s:State) = if (s.bull.first == 6 && s.bull.second == 6) 0 else 1

    private fun bellman(s:State) :MutableList<Double?>{
        val ret = mutableListOf<Double?>()
        adjacentStates(s).forEach { sp ->
            if (sp.value == null) ret.add(null)
            else ret.add(t(s,sp) * (r(sp) + sp.value!!))
        }
        return ret
    }

}

fun main() {
    A3().adjacentStates(A3.State(Pair(3, 3), Pair(1, 1)))
}