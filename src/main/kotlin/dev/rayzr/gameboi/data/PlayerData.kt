package dev.rayzr.gameboi.data

import dev.rayzr.gameboi.data.shop.ShopItem

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
    fun setStat(name: String, value: Int) = stats.put(name, value)

    fun updateStatBy(name: String, amount: Int) = stats.put(name, stats.getOrDefault(name, 0) + amount)
}
