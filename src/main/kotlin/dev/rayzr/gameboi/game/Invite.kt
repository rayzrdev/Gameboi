package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageReaction
import java.util.*
import kotlin.concurrent.schedule

class Invite(val channel: MessageChannel, val from: Player, val to: Player, val game: Game, val life: Long = 120000) {
    lateinit var message: Message

    init {
        channel.sendMessage(
                EmbedBuilder()
                        .setThumbnail(from.user.avatarUrl)
                        .setTitle("${from.user.name} has invited you to play ${game.name}!")
                        .setDescription("Press the check mark below to accept!")
                        .setFooter("This invite will expire in ${life / 60000} minutes")
                        .build()
        ).queue {
            it.addReaction("\u2705").queue() // check mark
            it.addReaction("\u274c").queue() // x mark

            message = it

            Timer().schedule(life) {
                InviteManager.remove(to.user)
                message.delete().queue()
            }
        }
    }

    fun handleReaction(reaction: MessageReaction, message: Message, player: Player) {
        if (player != to) {
            reaction.removeReaction(player.user).queue()
            return
        }

        when (reaction.reactionEmote.name) {
            "\u2705" -> {
                // Check *again*
                if (from.currentMatch != null || to.currentMatch != null) {
                    message.channel.sendMessage(":x: One of you has already joined another match!").queue {
                        Timer().schedule(Gameboi.errorLife) {
                            it.textChannel.deleteMessages(listOf(it, message)).queue()
                        }
                    }
                    return
                }

                // TODO: More than 2-player game support?
                val match = Match(game, channel)

                match.addPlayer(from)
                match.addPlayer(to)
            }
            "\u274c" -> {
                // Do nothing special
            }
            else -> return
        }

        // Bye bye invite
        InviteManager.remove(to.user)
        message.delete().queue()
    }
}