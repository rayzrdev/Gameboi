package dev.rayzr.gameboi

import dev.rayzr.gameboi.command.*
import dev.rayzr.gameboi.data.DataManager
import dev.rayzr.gameboi.data.leaderboard.LeaderboardManager
import dev.rayzr.gameboi.data.settings.GuildSettingsManager
import dev.rayzr.gameboi.data.shop.initShopItems
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.listener.MessageListener
import dev.rayzr.gameboi.listener.ReactionListener
import dev.rayzr.gameboi.manager.MatchManager
import dev.rayzr.gameboi.manager.RenderManager
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.ApiBuilder.path
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
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
    LeaderboardManager.load()

    // Init JDA
    Gameboi.shardManager = DefaultShardManagerBuilder.create(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_REACTIONS
    )
        .setShardsTotal(Gameboi.shardCount)
        .setToken(Gameboi.token)
        .addEventListeners(Gameboi, ReactionListener, MessageListener)
        .build()

    // Generate invite
    println(
        Gameboi.shardManager.shards.first().getInviteUrl(
            Permission.MESSAGE_WRITE,
            Permission.MESSAGE_MANAGE,
            Permission.MESSAGE_EMBED_LINKS,
            Permission.MESSAGE_ATTACH_FILES
        )
    )

    if (Gameboi.updateStatus) {
        Timer().scheduleAtFixedRate(0L, 30000L) { Gameboi.updatePresence() }
    }

    Javalin.create().routes {
        path("matches") {
            path(":match-id") {
                // TODO: get state via API route
                get("render/:state-id", RenderManager::handleRenderReq)
            }
        }
    }.start(Gameboi.port)
}

object Gameboi : EventListener {
    lateinit var shardManager: ShardManager

    val yaml = Yaml()
    lateinit var prefix: String
    lateinit var token: String
    var shardCount: Int = 1
    var updateStatus: Boolean = true
    var errorLife: Long = 0
    lateinit var host: String
    var port: Int = 1


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
        shardCount = (output["shards"] ?: 1) as Int
        updateStatus = output["update-status"] as Boolean? ?: true
        errorLife = output["error-life"]?.toString()?.toLong() ?: 15000
        host = output["host"].toString()
        port = (output["port"] ?: 7000) as Int
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
                if (event.author.isBot) return@thenAccept

                val raw = event.message.contentRaw

                val taggedBotRole = event.message.mentionedRoles.find {
                    raw.startsWith(it.asMention)
                            && event.guild.selfMember.roles.contains(it)
                            && it.name == event.jda.selfUser.name
                }

                val prefixes = listOfNotNull(
                    taggedBotRole?.asMention,
                    "<@${event.jda.selfUser.id}>",
                    "<@!${event.jda.selfUser.id}>",
                    guildSettings.realPrefix
                )

                val remainder = prefixes.find {
                    raw.startsWith(it)
                }?.let {
                    raw.substring(it.length).trim()
                } ?: return@thenAccept

                val split = remainder.split(" ")
                val commandLabel = split[0]
                val args = split.slice(1 until split.size)
                val command = commands.find { it.name == commandLabel }

                if (command != null) {
                    command.handle(event, args)
                } else {
                    MatchManager[event.author]?.run {
                        game.handleMessage(Player[event.author], this, event.message)
                    }
                }
            }
        }
    }

    fun updatePresence() {
        shardManager.setPresence(
            OnlineStatus.ONLINE,
            Activity.watching(
                "over ${shardManager.guilds.size} guilds | ${prefix}help"
            )
        )
    }
}
