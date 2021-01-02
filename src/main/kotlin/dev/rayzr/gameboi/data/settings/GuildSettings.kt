package dev.rayzr.gameboi.data.settings

import dev.rayzr.gameboi.Gameboi

class GuildSettings(var guild: Long, var prefix: String? = null) {
    val realPrefix get() = prefix ?: Gameboi.prefix

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "prefix" to prefix
        )
    }
}
