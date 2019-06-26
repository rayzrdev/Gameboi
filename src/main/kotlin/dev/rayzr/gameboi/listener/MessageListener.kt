package dev.rayzr.gameboi.listener

import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener

object MessageListener : EventListener {

    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReceivedEvent) {
            if (event.author.isBot) {
                return
            }

            val player = Player[event.author]
            val match = player.currentMatch

            if (match != null) {
                match.game.handleMessage(player, match, event.message)
                return
            }
        }
    }
}