package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageReaction

class Invite(val channel: MessageChannel, val from: Player, val to: Player, val game: Game, val time: Long) {
    lateinit var message: Message

    init {
        channel.sendMessage(
                EmbedBuilder()
                        .setThumbnail(from.user.avatarUrl)
                        .setTitle("${from.user.name} has invited you to play ${game.name}!")
                        .setDescription("Press the check mark below to accept!")
                        .build()
        ).queue {
            it.addReaction("\u2705").queue() // check mark
            it.addReaction("\u274c").queue() // x mark

            message = it
        }
    }

    fun handleReaction(reaction: MessageReaction, message: Message, player: Player) {
        if (player != to) {
            reaction.removeReaction(player.user).queue()
            return
        }

        when (reaction.reactionEmote.name) {
            "\u2705" -> {
                // TODO: More than 2-player game support?
                val match = Match(game, channel)

                match.addPlayer(from)
                match.addPlayer(to)
            }
            "\u274c" -> {
                InviteManager.remove(to.user)
            }
        }

        message.delete().queue()
    }
}