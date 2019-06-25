package dev.rayzr.gameboi.command

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

object HelpCommand : Command("help", "Shows you help for Gameboi", "help [command]") {
    override fun handle(event: MessageReceivedEvent, args: List<String>) {
        // TODO: Show help message
        event.channel.sendMessage("Help :)").queue()
    }
}

object PingCommand : Command("ping", "Shows you the bot's ping") {
    override fun handle(event: MessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage(":stopwatch: Pong! `${event.jda.gatewayPing}ms`").queue()
    }
}