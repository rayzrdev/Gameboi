package dev.rayzr.gameboi.data

import dev.rayzr.gameboi.data.leaderboard.Leaderboard
import dev.rayzr.gameboi.data.leaderboard.LeaderboardManager
import dev.rayzr.gameboi.data.shop.ShopItem
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User

data class PlayerData(
        var coins: Int = 0,
        val stats: MutableMap<String, Int> = mutableMapOf(),
        val inventory: MutableMap<ShopItem, Int> = mutableMapOf(),
        val equipment: MutableMap<String, ShopItem> = mutableMapOf()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
                "coins" to coins,
                "stats" to stats,
                "inventory" to inventory.mapKeys { it.key.internalName },
                "equipment" to equipment.mapValues { it.value.internalName }
        )
    }

    fun getStat(name: String, default: Int = 0) = stats.computeIfAbsent(name) { default }
    fun setStat(user: User, guild: Guild, name: String, value: Int) {
        stats[name] = value

        LeaderboardManager.editLeaderboardFor(guild.id) {
            addStat(user.name, value)
        }

        LeaderboardManager.editLeaderboardFor(Leaderboard.GLOBAL_SCOPE) {
            addStat(user.name, value)
        }
    }

    fun updateStatBy(user: User, guild: Guild, name: String, amount: Int) = setStat(user, guild, name, stats.getOrDefault(name, 0) + amount)
}
