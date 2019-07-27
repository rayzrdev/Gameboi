package dev.rayzr.gameboi.manager

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Invite
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.TimeUnit

object InviteManager {
    private val invites: MutableMap<Long, Invite> = mutableMapOf()
    val currentInvites: Collection<Invite>
        get() = invites.values

    operator fun get(user: User) = invites[user.idLong]
    operator fun set(user: User, invite: Invite) = invites.put(user.idLong, invite)
    fun remove(user: User) {
        invites.remove(user.idLong)
    }

    fun invite(message: Message, from: Player, to: Player, game: Game) {
        if (from.currentMatch != null || to.currentMatch != null) {
            message.channel.sendMessage(":x: Players can only play one game at a time!").queue {
                it.textChannel.deleteMessages(listOf(it, message)).queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
            return
        }

        if (invites.containsKey(to.user.idLong)) {
            message.channel.sendMessage(":x: That player has already been invited to a game!").queue {
                it.textChannel.deleteMessages(listOf(it, message)).queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
            return
        }

        if (message.channel !is TextChannel) {
            throw IllegalArgumentException("message.channel must be a guild TextChannel!")
        }

        this[to.user] = Invite(message.channel as TextChannel, from, to, game)
    }

    fun singlePlayer(message: Message, player: Player, game: Game) {
        if (player.currentMatch != null) {
            message.channel.sendMessage(":x: You can only play one game at a time!").queue {
                it.textChannel.deleteMessages(listOf(it, message)).queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
            return
        }

        if (message.channel !is TextChannel) {
            throw IllegalArgumentException("message.channel must be a guild TextChannel!")
        }

        val match = Match(game, message.channel as TextChannel)
        match.addPlayer(player)
    }
}