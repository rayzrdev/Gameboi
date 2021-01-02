package dev.rayzr.gameboi.data.leaderboard

import dev.rayzr.gameboi.data.settings.yaml
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CompletableFuture

object LeaderboardManager {
    private val file = File("leaderboards.yml")

    private var leaderboards: MutableMap<String, Leaderboard> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    fun load() {
        if (!file.exists()) {
            // Nothing to load
            return
        }

        val raw = yaml.load(FileInputStream(file)) as Map<String, Any>

        leaderboards.clear()

        raw.mapKeys { it.key }
            .mapValues {
                try {
                    val section = it.value as Map<String, Map<String, Int>>

                    return@mapValues Leaderboard(it.key, section.mapValues { stat ->
                        stat.value.map { entry ->
                            LeaderboardEntry(entry.key, entry.value)
                        }.toMutableList()
                    }.toMutableMap())
                } catch (e: Exception) {
                    println("Failed to load leader-board for scope '${it.key}'")
                    e.printStackTrace()
                    return@mapValues null
                }
            }
            .filterValues { it != null }
            .mapValues { it.value!! }
            .forEach { leaderboards[it.key] = it.value }
    }

    private fun save() {
        file.delete()
        file.writeText(
            yaml.dumpAsMap(
                leaderboards.mapValues { it.value.toMap() }
            )
        )
    }

    operator fun get(scope: String) = getLeaderboardFor(scope)

    fun getLeaderboardFor(scope: String): CompletableFuture<Leaderboard> {
        val future = CompletableFuture<Leaderboard>()

        future.complete(leaderboards.computeIfAbsent(scope) { Leaderboard(scope) })

        return future
    }

    fun editLeaderboardFor(scope: String, action: Leaderboard.() -> Unit) {
        getLeaderboardFor(scope).thenAccept {
            action.invoke(it)
            save()
        }
    }

}