package dev.rayzr.gameboi.data

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.data.shop.ShopRegistry
import dev.rayzr.gameboi.game.Player
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CompletableFuture

val yaml = Yaml()

object DataManager {
    private val file = File("player-data.yml")
    private val playerDataCache: MutableMap<Long, PlayerData> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    fun load() {
        if (!file.exists()) {
            // Nothing to load
            return
        }

        val raw = Gameboi.yaml.load(FileInputStream(file)) as Map<Long, Any>

        playerDataCache.clear()

        raw.mapKeys { it.key }
            .mapValues {
                try {
                    val section = it.value as Map<String, Any>

                    return@mapValues PlayerData(
                        // Coins
                        section.getOrDefault("coins", 0) as Int,

                        // Stats
                        (section.getOrDefault("stats", emptyMap<String, Int>()) as Map<String, Int>).toMutableMap(),

                        // Inventory
                        (section.getOrDefault("inventory", emptyMap<String, Int>()) as Map<String, Int>)
                            .mapKeys { item -> ShopRegistry[item.key] }
                            .filterKeys { item -> item != null }
                            .mapKeys { item -> item.key!! }
                            .toMutableMap(),

                        // Equipped items
                        (section.getOrDefault("equipment", emptyMap<String, String>()) as Map<String, String>)
                            .mapValues { item -> ShopRegistry[item.value] }
                            .filterValues { item -> item != null }
                            .mapValues { item -> item.value!! }
                            .toMutableMap()
                    )
                } catch (e: Exception) {
                    println("Failed to load player data for player with ID '${it.key}'")
                    e.printStackTrace()
                    return@mapValues null
                }
            }
            .filterValues { it != null }
            .mapValues { it.value!! }
            .forEach { (id, data) -> playerDataCache[id] = data }
    }

    private fun save() {
        file.delete()
        file.writeText(
            yaml.dumpAsMap(
                playerDataCache.mapValues { it.value.toMap() }
            )
        )
    }

    fun getPlayerData(player: Player): CompletableFuture<PlayerData> {
        val future = CompletableFuture<PlayerData>()

        future.complete(playerDataCache.computeIfAbsent(player.user.idLong) { PlayerData() })

        return future
    }

    fun editPlayerData(player: Player, action: PlayerData.() -> Unit) {
        getPlayerData(player).thenAccept {
            action.invoke(it)
            save()
        }
    }
}