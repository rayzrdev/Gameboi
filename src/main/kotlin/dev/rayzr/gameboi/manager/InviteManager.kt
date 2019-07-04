package dev.rayzr.gameboi.manager

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Invite
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User
import java.util.*
import kotlin.concurrent.schedule

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
                Timer().schedule(Gameboi.errorLife) {
                    it.textChannel.deleteMessages(listOf(it, message)).queue()
                }
            }
            return
        }

        if (invites.containsKey(to.user.idLong)) {
            message.channel.sendMessage(":x: That player has already been invited to a game!").queue {
                Timer().schedule(Gameboi.errorLife) {
                    it.textChannel.deleteMessages(listOf(it, message)).queue()
                }
            }
            return
        }

        this[to.user] = Invite(message.channel, from, to, game)
    }

    fun singlePlayer(message: Message, player: Player, game: Game) {
        if (player.currentMatch != null) {
            message.channel.sendMessage(":x: You can only play one game at a time!").queue {
                Timer().schedule(Gameboi.errorLife) {
                    it.textChannel.deleteMessages(listOf(it, message)).queue()
                }
            }
            return
        }

        val match = Match(game, message.channel)
        match.addPlayer(player)
    }
}