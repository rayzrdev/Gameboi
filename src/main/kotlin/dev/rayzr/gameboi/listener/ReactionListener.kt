package dev.rayzr.gameboi.listener

import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.util.*

object ReactionListener : EventListener {

    fun onReact(event: GuildMessageReactionAddEvent) {
        if (event.user.isBot) return

        val msg = event.channel.history.getMessageById(event.messageId)
        if (msg!!.jda.selfUser != event.user) return

        // TODO: how should we track messages and matches that reaction controls apply to?
    }

}