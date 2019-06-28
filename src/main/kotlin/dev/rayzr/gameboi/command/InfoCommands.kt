package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object HelpCommand : Command("help", "Shows you help for Gameboi", "help [command]") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val embed = EmbedBuilder().run {
            setDescription(Gameboi.commands.joinToString("\n\n") { "**${it.usage}** - ${it.description}" })
            setAuthor("Gameboi Help Commands", "https://github.com/RayzrDev/Gameboi", event.jda.selfUser.effectiveAvatarUrl)
            setColor(0x353940)
            build()
        }
        event.channel.sendMessage(embed).queue()
    }
}

object PingCommand : Command("ping", "Shows you the bot's ping") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage(":stopwatch: Pong! `${event.jda.gatewayPing}ms`").queue()
    }
}