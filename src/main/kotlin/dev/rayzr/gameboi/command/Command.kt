package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*
import kotlin.concurrent.schedule

abstract class Command(val name: String, val description: String, val usage: String = name) {
    abstract fun handle(event: GuildMessageReceivedEvent, args: List<String>)

    fun fail(event: GuildMessageReceivedEvent, message: String) {
        event.channel.sendMessage(":x: $message").queue {
            Timer().schedule(Gameboi.errorLife) {
                it.textChannel.deleteMessages(listOf(it, event.message)).queue()
            }
        }
    }
}