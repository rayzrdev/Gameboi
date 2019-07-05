package dev.rayzr.gameboi.data.settings

import net.dv8tion.jda.api.entities.Guild
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.CompletableFuture

val yaml = Yaml()

object GuildSettingsManager {
    private val file = File("guild-settings.yml")

    private var guildSettings: MutableMap<Long, GuildSettings> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    fun load() {
        if (!file.exists()) {
            // Nothing to load
            return
        }

        val raw = yaml.load(FileInputStream(file)) as Map<Long, Any>

        guildSettings.clear()

        raw.mapKeys { it.key }
                .mapValues {
                    try {
                        val section = it.value as Map<String, Any>

                        return@mapValues GuildSettings(it.key, section.get("prefix")?.toString())
                    } catch (e: Exception) {
                        println("Failed to load guild settings for player with ID '${it.key}'")
                        e.printStackTrace()
                        return@mapValues null
                    }
                }
                .filterValues { it != null }
                .mapValues { it.value!! }
                .forEach { guildSettings[it.key] = it.value }
    }

    private fun save() {
        file.delete()
        file.writeText(
                yaml.dumpAsMap(
                        guildSettings.mapValues { it.value.toMap() }
                )
        )
    }

    operator fun get(guild: Guild) = getGuildSettingsFor(guild)

    fun getGuildSettingsFor(guild: Guild): CompletableFuture<GuildSettings> {
        val future = CompletableFuture<GuildSettings>()

        future.complete(guildSettings.computeIfAbsent(guild.idLong) { GuildSettings(guild.idLong) })

        return future
    }

    fun editGuildSettings(guild: Guild, action: GuildSettings.() -> Unit) {
        getGuildSettingsFor(guild).thenAccept {
            action.invoke(it)
            save()
        }
    }
}