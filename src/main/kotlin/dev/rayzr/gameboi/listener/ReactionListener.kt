package dev.rayzr.gameboi.listener

import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.connect4.Connect4Game
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.hooks.EventListener

object ReactionListener : EventListener {


    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReactionAddEvent) {
            if (event.user.isBot) return

            val msg = event.channel.history.retrievePast(1).complete()[0]
            if (msg.author != msg.jda.selfUser) return

            // TODO: how should we track messages and matches that reaction controls apply to?

            /*************
             * Temporary
             *************/
            if (Connect4Game.match == null) return
            val player = Player.get(event.user)
            Connect4Game.handleReaction(player, Connect4Game.match!!, event.reaction)
        }
    }

}