package dev.rayzr.gameboi.manager

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Invite
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User

object InviteManager {
    private val invites: MutableMap<User, Invite> = mutableMapOf()
    val currentInvites: Collection<Invite>
        get() = invites.values

    operator fun get(user: User) = invites[user]
    operator fun set(user: User, invite: Invite) = invites.put(user, invite)
    fun remove(user: User) {
        invites.remove(user)
    }

    fun invite(channel: MessageChannel, from: Player, to: Player, game: Game) {
        this[to.user] = Invite(channel, from, to, game, System.currentTimeMillis())
    }
}