package dev.rayzr.gameboi

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class Command(val name: String, val description: String, val usage: String) {
    abstract fun handle(event: MessageReceivedEvent)
}