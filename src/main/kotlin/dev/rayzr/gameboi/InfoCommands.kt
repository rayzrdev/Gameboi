package dev.rayzr.gameboi

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class HelpCommand : Command("help", "Shows you help for Gameboi", "help [command]") {
    override fun handle(event: MessageReceivedEvent) {
        // TODO: Show help message
        event.channel.sendMessage("Help :)").queue()
    }
}

class PingCommand : Command("ping", "Shows you the bot's ping", "ping") {
    override fun handle(event: MessageReceivedEvent) {
        event.channel.sendMessage(":stopwatch: Pong! `${event.jda.gatewayPing}ms`")
    }
}