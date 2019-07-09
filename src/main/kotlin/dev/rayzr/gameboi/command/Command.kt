package dev.rayzr.gameboi.command

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

abstract class Command(val name: String, val description: String, val usage: String = name) {
    abstract fun handle(event: GuildMessageReceivedEvent, args: List<String>)

    fun fail(event: GuildMessageReceivedEvent, message: String) {
        event.channel.sendMessage(":x: $message").queue()
    }
}