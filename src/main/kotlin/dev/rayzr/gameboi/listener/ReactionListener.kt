package dev.rayzr.gameboi.listener

import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.manager.InviteManager
import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.EventListener

object ReactionListener : EventListener {
    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReactionAddEvent) {
            if (event.user.isBot) return

            val history = try {
                event.channel.getHistoryAround(event.messageId, 1).complete() ?: return
            } catch (ex: Exception) {
                // Ignore if we can't get the message history
                return
            }

            val msg = history.getMessageById(event.messageId) ?: return
            if (msg.author != msg.jda.selfUser) return

            val player = Player[event.user]
            val match = MatchManager.currentMatches.find { it.renderContext.lastMessage?.id == msg.id }

            if (match != null) {
                match.game.handleReaction(player, match, event.reaction)
                match.bumpTimeout()
                return
            }

            InviteManager.currentInvites.find { it.message?.id == msg.id }?.handleReaction(event.reaction, msg, player)
        }
    }
}