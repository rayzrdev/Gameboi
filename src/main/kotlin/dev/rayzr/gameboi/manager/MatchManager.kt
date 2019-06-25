package dev.rayzr.gameboi.manager

import dev.rayzr.gameboi.game.Match
import net.dv8tion.jda.api.entities.User

object MatchManager {
    private val matches: MutableMap<User, Match> = mutableMapOf()
    val currentMatches: Collection<Match>
        get() = matches.values

    operator fun get(user: User) = matches[user]
    operator fun set(user: User, match: Match) = matches.put(user, match)
    fun remove(user: User) {
        matches.remove(user)
    }
}