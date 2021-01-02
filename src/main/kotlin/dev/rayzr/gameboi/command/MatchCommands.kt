package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.TimeUnit

object QuitCommand : Command("quit", "Quits you from your current match", category = Categories.MATCH) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val player = Player[event.author]

        if (player.currentMatch == null) {
            event.channel.sendMessage(":x: You are not in a match currently!").queue {
                it.textChannel.deleteMessages(listOf(it, event.message))
                    .queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
        } else {
            player.currentMatch?.end()
            event.channel.sendMessage(":wave: You have quit your current match.").queue()
        }
    }
}