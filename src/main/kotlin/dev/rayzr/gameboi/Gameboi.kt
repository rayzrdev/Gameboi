package dev.rayzr.gameboi

import dev.rayzr.gameboi.command.*
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.listener.ReactionListener
import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.omg.CORBA.Object
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import kotlin.system.exitProcess

fun main() {
    Gameboi.load()

    val jda = JDABuilder(Gameboi.token)
            .addEventListeners(Gameboi, ReactionListener)
            .build()
}

object Gameboi : EventListener {
    val yaml = Yaml()
    lateinit var prefix: String
    lateinit var token: String

    fun load() {
        val configFile = File("config.yml")
        if (!configFile.exists()) {
            Files.copy(javaClass.getResourceAsStream("/config.yml"), configFile.toPath())

            println("No config.yml found! The default one has been copied to the current directory. Please set it up before running Gameboi again.")
            exitProcess(1)
        }

        val output = yaml.load(FileInputStream(configFile)) as Map<String, Object>
        prefix = output["prefix"].toString()
        token = output["token"].toString()
    }

    private val commands: List<Command> = listOf(
            // Info
            HelpCommand,
            PingCommand,
            // Test
            RenderTestCommand,
            MatchTestCommand
    )

    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReceivedEvent) {
            if (!event.message.contentRaw.startsWith(prefix)) {
                return
            }

            val split = event.message.contentRaw.substring(prefix.length).split(" ")
            val commandLabel = split[0]
            val args = split.slice(1 until split.size)
            val command = commands.find { it.name == commandLabel }

            if (command != null) {
                command.handle(event, args)
            } else {
                MatchManager[event.author]?.run { game.handleMessage(Player[event.author], this, event.message) }
            }
        }
    }
}
