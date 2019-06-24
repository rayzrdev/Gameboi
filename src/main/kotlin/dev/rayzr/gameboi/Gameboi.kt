package dev.rayzr.gameboi

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener

fun main() {
    Gameboi.load()

    val jda = JDABuilder("abc")
            .addEventListeners(Gameboi)
            .build()
}

object Gameboi : EventListener {
    lateinit var prefix: String

    fun load() {
        // TODO: Load from config
        prefix = "!"
    }

    private val commands: List<Command> = listOf(
            HelpCommand(),
            PingCommand()
    )

    override fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            if (!event.message.contentRaw.startsWith(prefix)) {
                return
            }

            val split = event.message.contentRaw.substring(prefix.length).split(" ")
            val commandLabel = split[0]
            val args = split.slice(1..split.size)

            commands.find { it.name == commandLabel }?.handle(event, args) ?: event.message.channel.sendMessage(":x: Invalid command `$commandLabel`!")
        }
    }
}
