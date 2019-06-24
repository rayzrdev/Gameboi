package dev.rayzr.gameboi.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class Command(val name: String, val description: String, val usage: String = name) {
    abstract fun handle(event: MessageReceivedEvent, args: List<String>)
}