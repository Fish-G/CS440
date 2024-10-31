import kotlin.math.abs
import kotlin.math.min

class A3 {
    data class Tile(val id: Int, var v: Double)
    data class State(val robot: Pair<Int, Int>, val bull: Pair<Int, Int>)

    fun manhattan(a: Pair<Int, Int>, b: Pair<Int, Int>) = abs(a.first - b.first) + abs(a.second - b.second)
    fun withinBounds(row: Int, col: Int) =
        (row in 0..12) && (col in 0..12) && !(row == 5 && col in 5..7) && !(row == 6 && col == 7) && !(row == 7 && col in 5..7)

    fun adjacentTiles(initial: Pair<Int, Int>, diagonal: Boolean = false): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        fun add(r: Int, c: Int) { if (withinBounds(r, c)) ret.add(Pair(r, c))}
        val (r, c) = initial
        add(r+1,c)
        add(r-1,c)
        add(r,c+1)
        add(r,c-1)
        if (diagonal) {
            add(r+1,c+1)
            add(r+1,c-1)
            add(r-1,c+1)
            add(r-1,c-1)
        }
        return ret
    }


    fun adjacentStates(state: State): List<State> {
        val adj = mutableListOf<State>()
        var (robot, bull) = state


        val adjRob = adjacentTiles(robot,true)

        for (r in adjRob) {
            if (manhattan(r, bull) <= 5) {
                val s = adjacentTiles(bull).sortedBy {manhattan(it,robot) }
                s.filter { it == s.first() }.forEach {adj.add(State(r,it))}
            } else {
                adjacentTiles(bull).forEach { adj.add(State(r,it)) }
            }
        }


        return adj.toList()
    }


}