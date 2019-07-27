package dev.rayzr.gameboi.data.leaderboard

class Leaderboard(val scope: String, val top: MutableList<LeaderboardEntry> = mutableListOf(), val limit: Int = 10) {
    companion object {
        const val GLOBAL_SCOPE = "ALL"
    }

    fun addStat(name: String, value: Int) {
        if (top.size < 1) {
            top.add(LeaderboardEntry(name, value))
            return
        }

        for (i in top.indices) {
            if (value > top[i].value) {
                top.add(i, LeaderboardEntry(name, value))
                while (top.size > limit) {
                    top.remove(top.last())
                }
                return
            }
        }
    }

    fun toMap(): Map<String, Int> = top.associate { it.name to it.value }
}

class LeaderboardEntry(val name: String, val value: Int)