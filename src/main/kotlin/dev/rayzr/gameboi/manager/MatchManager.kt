package dev.rayzr.gameboi.manager

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match
import net.dv8tion.jda.api.entities.User

object MatchManager {
    private val matches: Map<User, Match> = emptyMap()

    operator fun get(user: User) = matches[user]
}