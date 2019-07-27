package dev.rayzr.gameboi.data.leaderboard

class Leaderboard(val scope: String, val top: MutableMap<String, MutableList<LeaderboardEntry>> = mutableMapOf(), val limit: Int = 10) {
    companion object {
        const val GLOBAL_SCOPE = "ALL"
    }

    fun getTop(stat: String): MutableList<LeaderboardEntry> {
        return top.computeIfAbsent(stat) { mutableListOf() }
    }

    fun addStat(stat: String, name: String, value: Int) {
        val list = getTop(stat)

        if (list.size < 1) {
            list.add(LeaderboardEntry(name, value))
            return
        }

        for (i in list.indices) {
            if (value > list[i].value) {
                // Remove old scores from that user
                list.removeIf { it.name == name }

                list.add(i, LeaderboardEntry(name, value))

                while (top.size > limit) {
                    list.remove(list.last())
                }

                return
            }
        }
    }

    fun toMap(): Map<String, Map<String, Int>> = top.mapValues { it.value.associate { entry -> entry.name to entry.value } }
}

class LeaderboardEntry(val name: String, val value: Int)