package dev.rayzr.gameboi

import dev.rayzr.gameboi.command.Command
import dev.rayzr.gameboi.command.HelpCommand
import dev.rayzr.gameboi.command.PingCommand
import dev.rayzr.gameboi.command.RenderTestCommand
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
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
            .addEventListeners(Gameboi)
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
            RenderTestCommand
    )

    override fun onEvent(event: GenericEvent) {
        if (event is MessageReceivedEvent) {
            if (!event.message.contentRaw.startsWith(prefix)) {
                return
            }

            val split = event.message.contentRaw.substring(prefix.length).split(" ")
            val commandLabel = split[0]
            val args = split.slice(1 until split.size)

            commands.find { it.name == commandLabel }?.handle(event, args)
                    ?: event.message.channel.sendMessage(":x: Invalid command `$commandLabel`!")
        }
    }
}
