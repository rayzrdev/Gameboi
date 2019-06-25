package dev.rayzr.gameboi.listener

import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.connect4.Connect4Game
import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.EventListener

object ReactionListener : EventListener {


    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReactionAddEvent) {
            if (event.user.isBot) return

            val msg = event.channel.getHistoryAround(event.messageId, 1).complete().getMessageById(event.messageId)!!
            if (msg.author != msg.jda.selfUser) return

            val player = Player[event.user]
            val match = MatchManager.currentMatches.find { it.renderContext.lastMessage?.id == msg.id } ?: return

            Connect4Game.handleReaction(player, match, event.reaction)
        }
    }

}