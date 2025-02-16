package A1A3

import kotlin.math.abs

class A3 {
    data class State(
        val robot: Pair<Int, Int>,
        val bull: Pair<Int, Int>,
        var value: Double? = null,
        var id: Int
    ) // (x,y)

    val allStates = Array(13) { Array(13) { Array(13) { Array(13) { State(Pair(-1, -1), Pair(-1, -1), id = -1) } } } }

    init {
        var i = 0
        (0..12).forEach { rx ->
            (0..12).forEach { ry ->
                (0..12).forEach { bx ->
                    (0..12).forEach { by ->
                        if (bx == 6 && by == 6) allStates[rx][ry][bx][by] =
                            State(Pair(rx, ry), Pair(bx, by), value = 0.0, i)
                        else allStates[rx][ry][bx][by] = State(Pair(rx, ry), Pair(bx, by), id = i)
                        i++
                    }
                }
            }
        }
    }

    private fun manhattan(a: Pair<Int, Int>, b: Pair<Int, Int>) = abs(a.first - b.first) + abs(a.second - b.second)
    private fun withinBounds(x: Int, y: Int) =
        (x in 0..12) && (y in 0..12) && !(x == 5 && y in 5..7) && !(x == 6 && y == 7) && !(x == 7 && y in 5..7)

    private fun adjacentTiles(
        initial: Pair<Int, Int>,
        other: Pair<Int, Int>,
        diagonal: Boolean = false
    ): List<Pair<Int, Int>> {
        val ret = mutableListOf<Pair<Int, Int>>()
        fun add(r: Int, c: Int) {
            if (withinBounds(r, c) && !(initial.first == other.first && initial.second == other.second)) ret.add(
                Pair(
                    r,
                    c
                )
            )
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

    // all actions the bull can take if the robot is in a tile and bull's current position
    fun bullActions(robot: Pair<Int, Int>, bull: Pair<Int, Int>): List<State> {
        val ret = mutableListOf<State>()
        if (manhattan(robot, bull) <= 5) {
            adjacentTiles(bull, robot).filter { manhattan(it, robot) <= manhattan(bull, robot) }
                .map { allStates[robot.first][robot.second][it.first][it.second] }.forEach { ret.add(it) }
        } else adjacentTiles(bull, robot).map { allStates[robot.first][robot.second][it.first][it.second] }
            .forEach { ret.add(it) }

        return ret.toList()
    }

    private fun isGoalState(s: State): Boolean = (s.bull.first == 6) && (s.bull.second == 6)

    fun calculateBellman(robotStart: Pair<Int, Int>, bullStart: Pair<Int, Int>): Double {
        var tval: Double? = null
        for (i in 0..<10000) {
            for (rx in 0..12) {
                for (ry in 0..12) {
                    for (bx in 0..12) {
                        for (by in 0..12) {
                            val cur = allStates[rx][ry][bx][by]
                            if (!isGoalState(cur) && withinBounds(rx, ry) && withinBounds(
                                    bx,
                                    by
                                ) && !(rx == bx && ry == by)
                            ) {
                                val actions = adjacentTiles(
                                    cur.robot,
                                    cur.bull,
                                    true
                                )
                                cur.value = actions.mapNotNull { action ->
                                    if (bullActions(action, cur.bull).any { it.value != null }) {
                                        val t = 1.0 / (bullActions(action, cur.bull).filter { it.value != null }.size)
                                        bullActions(action, cur.bull).filter { it.value != null }
                                            .sumOf { sPrime -> t * (1 + sPrime.value!!) }
                                    } else null
                                }.minOrNull()
                            }
                        }
                    }
                }
            }
            println(allStates[robotStart.first][robotStart.second][bullStart.first][bullStart.second].value)
            if (tval != null && abs(tval - allStates[robotStart.first][robotStart.second][bullStart.first][bullStart.second].value!!) < 0.0001) return allStates[robotStart.first][robotStart.second][bullStart.first][bullStart.second].value!!
            else tval = allStates[robotStart.first][robotStart.second][bullStart.first][bullStart.second].value

        }
        return allStates[robotStart.first][robotStart.second][bullStart.first][bullStart.second].value!!
    }
}

fun main() {
    println(A3().calculateBellman(Pair(12, 12), Pair(0, 0)))
}