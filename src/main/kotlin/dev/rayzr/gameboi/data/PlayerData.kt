package dev.rayzr.gameboi.data

data class PlayerData(var coins: Int, val stats: MutableMap<String, Int>) {
    fun toMap(): Map<String, Any> {
        return mapOf(
                "coins" to coins,
                "stats" to stats
        )
    }

    fun getStat(name: String, default: Int = 0) = stats.computeIfAbsent(name) { default }

    fun updateStatBy(name: String, amount: Int) = stats.put(name, stats.getOrDefault(name, 0) + amount)
}
