package dev.rayzr.gameboi.data

import dev.rayzr.gameboi.game.Player
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.util.concurrent.CompletableFuture

val yaml = Yaml()

object DataManager {
    private val file = File("player-data.yml")
    private val playerDataCache: MutableMap<Long, PlayerData> = mutableMapOf()

    fun load() {
        // TODO: Temporary, only while we're using flat-files

        if (!file.exists()) {
            // Nothing to load
            return
        }

    }

    fun save() {
        file.delete()
        file.writeText(
                yaml.dumpAsMap(
                        playerDataCache.mapValues { it.value.toMap() }
                )
        )
    }

    fun getPlayerData(player: Player): CompletableFuture<PlayerData> {
        val future = CompletableFuture<PlayerData>()

        future.complete(playerDataCache.computeIfAbsent(player.user.idLong) { PlayerData(0, mutableMapOf()) })

        return future
    }

    fun editPlayerData(player: Player, action: PlayerData.() -> Unit) {
        getPlayerData(player).thenAccept {
            action.invoke(it)
            save()
        }
    }
}