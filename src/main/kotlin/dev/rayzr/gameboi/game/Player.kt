package dev.rayzr.gameboi.game

import net.dv8tion.jda.api.entities.User

class Player(val user: User, var currentMatch: Match? = null) {
    companion object {
        // Cache
        // TODO: Clear cache at regular intervals for AFK players to avoid memory usage?
        private val players: Map<User, Player> = emptyMap()

        operator fun get(user: User) = players.getOrElse(user, { Player(user) })
    }

    override fun hashCode(): Int {
        return user.idLong.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (user.id != other.user.id) return false

        return true
    }
}