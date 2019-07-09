package dev.rayzr.gameboi

import dev.rayzr.gameboi.command.*
import dev.rayzr.gameboi.data.DataManager
import dev.rayzr.gameboi.data.settings.GuildSettingsManager
import dev.rayzr.gameboi.data.shop.initShopItems
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.listener.MessageListener
import dev.rayzr.gameboi.listener.ReactionListener
import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.system.exitProcess

fun main() {
    initShopItems()

    Gameboi.load()

    // TODO: Temporary, only while we're using flat files
    DataManager.load()
    GuildSettingsManager.load()

    // Init JDA
    val jda = JDABuilder(Gameboi.token)
            .addEventListeners(Gameboi, ReactionListener, MessageListener)
            .build()
            .awaitReady()

    // Generate invite
    println(jda.getInviteUrl(
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ATTACH_FILES
    ))

    if (Gameboi.updateStatus) {
        Timer().scheduleAtFixedRate(0L, 30000L) { Gameboi.updatePresence(jda) }
    }
}

object Gameboi : EventListener {
    val yaml = Yaml()
    lateinit var prefix: String
    lateinit var token: String
    var updateStatus: Boolean = true
    var errorLife: Long = 0

    fun load() {
        val configFile = File("config.yml")
        if (!configFile.exists()) {
            Files.copy(javaClass.getResourceAsStream("/config.yml"), configFile.toPath())

            println("No config.yml found! The default one has been copied to the current directory. Please set it up before running Gameboi again.")
            exitProcess(1)
        }

        val output = yaml.load(FileInputStream(configFile)) as Map<String, Any>
        prefix = output["prefix"].toString()
        token = output["token"].toString()
        updateStatus = output["update-status"] as Boolean? ?: true
        errorLife = output["error-life"]?.toString()?.toLong() ?: 15000
    }

    val commands: List<Command> = listOf(
            // Info
            HelpCommand,
            InviteCommand,
            AboutCommand,
            PingCommand,
            StatsCommand,
            // Invites
            // Multiplayer
            Connect4Invite,
            FightInvite,
            // Singleplayer
            Twenty48Invite,
            HangmanInvite,
            // Match commands
            QuitCommand,
            // Shop commands
            ShopCommand,
            BuyCommand,
            InventoryCommand,
            EquipCommand,
            // Settings commands
            SetPrefixCommand
    )

    override fun onEvent(event: GenericEvent) {
        if (event is GuildMessageReceivedEvent) {
            GuildSettingsManager.getGuildSettingsFor(event.guild).thenAccept { guildSettings ->
                val raw = event.message.contentRaw

                val remainder = when {
                    // Handle @mention commands
                    raw.startsWith(event.jda.selfUser.asMention) -> raw.substring(event.jda.selfUser.asMention.length).trim()
                    // Handle custom prefixes
                    raw.startsWith(guildSettings.realPrefix) -> raw.substring(guildSettings.realPrefix.length)
                    // Allow help no matter what custom prefix there is
                    raw.startsWith("${prefix}help") -> "help"
                    else -> return@thenAccept
                }

                val split = remainder.split(" ")
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

    fun updatePresence(jda: JDA) {
        jda.presence.setPresence(OnlineStatus.ONLINE, Activity.watching(
                "over ${jda.guilds.size} guilds | ${prefix}help"
        ))
    }
}
