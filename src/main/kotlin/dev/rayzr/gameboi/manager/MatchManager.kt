package dev.rayzr.gameboi.manager

import dev.rayzr.gameboi.game.Match
import net.dv8tion.jda.api.entities.User

object MatchManager {
    private val matches: Map<User, Match> = emptyMap()

    operator fun get(user: User) = matches[user]
}