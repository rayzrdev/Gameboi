package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.data.PlayerData
import dev.rayzr.gameboi.data.leaderboard.Leaderboard
import dev.rayzr.gameboi.data.leaderboard.LeaderboardManager
import dev.rayzr.gameboi.data.settings.GuildSettingsManager
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*
import kotlin.math.roundToInt

object HelpCommand : Command("help", "Shows you help for Gameboi", category = Categories.INFO) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        GuildSettingsManager.getGuildSettingsFor(event.guild).thenAccept { guildSettings ->
            val commands = Gameboi.commands.groupBy { it.category }
                    .toSortedMap(Comparator.comparingInt { it.priority })

            val embed = EmbedBuilder().run {
                setDescription(commands.map { category ->
                    val categoryCommands = category.value.joinToString("\n\n") { command ->
                        "`${guildSettings.realPrefix}${command.usage}` - ${command.description}"
                    }

                    "> __**${category.key.name}**__\n\n $categoryCommands"
                }.joinToString("\n\n"))
                setAuthor("Gameboi Help Commands", "https://github.com/RayzrDev/Gameboi", event.jda.selfUser.effectiveAvatarUrl)
                setColor(0x353940)
                build()
            }
            event.channel.sendMessage(embed).queue()
        }
    }
}

object InviteCommand : Command("invite", "Gives you an invite link for Gameboi", category = Categories.INFO) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val embed = EmbedBuilder().run {
            setTitle("Ready to level up your server with some fun?")
            setDescription(":tada: Click [here](${event.jda.getInviteUrl(Permission.MESSAGE_MANAGE)}) to add **Gameboi** to your server!")
//            setThumbnail(event.jda.selfUser.avatarUrl)
            setImage("https://raw.githubusercontent.com/RayzrDev/Gameboi/master/res/banner.png")

            setColor(0x353940)
            build()
        }
        event.channel.sendMessage(embed).queue()
    }
}

object AboutCommand : Command("about", "Shows you more information about Gameboi", category = Categories.INFO) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val embed = EmbedBuilder().run {
            setTitle("What is Gameboi?")

            setDescription("""
                **Gameboi** is a simple Discord bot made for Discord Hack Week 2019 with a plethora of small games you can play, a charming and nostalgic pixel art style, and a global currency and rewards system!
                
                With **2 multiplayer** games (Fight & Connect 4) and **2 singleplayer** games (Hangman & 2048), Gameboi is sure to liven up any server, and the charm of its retro pixel-art graphics feels right at home to any old-school gamer.
                
                :tada: Click [here](${event.jda.getInviteUrl(Permission.MESSAGE_MANAGE)}) to add **Gameboi** to your server!
                :link: Click [here](https://github.com/RayzrDev/Gameboi) to check out the source code for **Gameboi**!
                :heart: Click [here](https://patreon.com/Rayzr522) if you want to support **Gameboi** and its creators!
            """.trimIndent())

            addField("Servers", "%,d".format(event.jda.guilds.size), true)
            addField("Users", "%,d".format(event.jda.users.size), true)
            setFooter("Created by Rayzr522#9429 and zaeem#3333")

            setImage("https://raw.githubusercontent.com/RayzrDev/Gameboi/master/res/banner.png")

            setColor(0x353940)
            build()
        }
        event.channel.sendMessage(embed).queue()
    }
}

object PingCommand : Command("ping", "Shows you the bot's ping", category = Categories.INFO) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage(":stopwatch: Pong! `${event.jda.gatewayPing}ms`").queue()
    }

}

object StatsCommand : Command("stats", "Shows your game stats", "stats [game]", Categories.INFO) {

    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        Player[event.author].getData().thenAccept { data ->
            GuildSettingsManager[event.guild].thenAccept { guildSettings ->
                val embed = EmbedBuilder().run {

                    // TODO: Set up stat registry to automate this PLEASE
                    when (args.joinToString(" ").toLowerCase()) {
                        "" -> {
                            setDescription("To see stats for a specific game, type `${guildSettings.realPrefix}stats <game>`")

                            addStat(this, data, "Total Games Played", "games-played.total")
                            addStat(this, data, "Connect 4 Games Played", "games-played.Connect 4")
                            addStat(this, data, "Fight Games Played", "games-played.Fight")
                            addStat(this, data, "Hangman Games Played", "games-played.Hangman")
                            addStat(this, data, "2048 Games Played", "games-played.2048")
                        }
                        "connect 4" -> {
                            addStat(this, data, "Red Tiles Played", "connect4.played-red")
                            addStat(this, data, "Yellow Tiles Played", "connect4.played-yellow")
                            addStat(this, data, "Wins", "connect4.wins")
                        }
                        "fight" -> {
                            addStat(this, data, "Damage Dealt", "fight.damage-dealt")
                            addStat(this, data, "Damage Taken", "fight.damage-taken")
                            addStat(this, data, "Successful Attacks", "fight.successful-attacks")
                            addStat(this, data, "Missed Attacks", "fight.missed-attacks")
                            addStat(this, data, "Punches", "fight.attack.punch")
                            addStat(this, data, "Kicks", "fight.attack.kick")
                            addStat(this, data, "Slams", "fight.attack.slam")
                            addStat(this, data, "Wins", "fight.wins")
                        }
                        "hangman" -> {
                            addStat(this, data, "Total Guesses", "hangman.total-guesses")
                            addStat(this, data, "Correct Guesses", "hangman.correct-guesses")
                            // TODO: Method for computed stats?
                            addField("Accuracy", "${((data.getStat("hangman.correct-guesses") * 100.0) / data.getStat("hangman.total-guesses")).roundToInt()}%", true)
                            addStat(this, data, "Wins", "hangman.wins")
                        }
                        "2048" -> {
                            addStat(this, data, "Total Moves", "2048.total-moves")
                            addStat(this, data, "Highest Score", "2048.highest-score")
                            addStat(this, data, "Wins", "2048.wins")
                        }
                        else -> {
                            return@thenAccept fail(event, "That is not a valid game!")
                        }
                    }

                    setFooter("${event.author.name}'s stats")
                    setThumbnail(event.author.avatarUrl)
                    setColor(0x353940)


                    build()
                }

                event.channel.sendMessage(embed).queue()
            }
        }
    }

    private fun addStat(embed: EmbedBuilder, data: PlayerData, name: String, stat: String) {
        embed.addField(name, String.format("%,d", data.getStat(stat)), true)
    }

}

//object LeaderboardCommand : Command("leaderboard", "Shows the leaderboards for all players", "leaderboard [guild|all]", Categories.INFO) {
//
//    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
//        val scope = when (args[0]) {
//            "guild" -> event.channel.guild.id
//            "all" -> Leaderboard.GLOBAL_SCOPE
//            else -> {
//                return fail(event, "That is not a valid context, please specify either `guild` or `all`!")
//            }
//        }
//
//        LeaderboardManager[scope].thenAccept {
//            val embed = EmbedBuilder().run {
//
//                // TODO: Set up stat registry to automate this PLEASE
//                when (args.joinToString(" ").toLowerCase()) {
//                    "" -> {
//                        addStat(this, data, "Total Games Played", "games-played.total")
//                        addStat(this, data, "Connect 4 Games Played", "games-played.Connect 4")
//                        addStat(this, data, "Fight Games Played", "games-played.Fight")
//                        addStat(this, data, "Hangman Games Played", "games-played.Hangman")
//                        addStat(this, data, "2048 Games Played", "games-played.2048")
//                    }
//                    "connect 4" -> {
//                        addStat(this, data, "Red Tiles Played", "connect4.played-red")
//                        addStat(this, data, "Yellow Tiles Played", "connect4.played-yellow")
//                        addStat(this, data, "Wins", "connect4.wins")
//                    }
//                    "fight" -> {
//                        addStat(this, data, "Damage Dealt", "fight.damage-dealt")
//                        addStat(this, data, "Damage Taken", "fight.damage-taken")
//                        addStat(this, data, "Successful Attacks", "fight.successful-attacks")
//                        addStat(this, data, "Missed Attacks", "fight.missed-attacks")
//                        addStat(this, data, "Punches", "fight.attack.punch")
//                        addStat(this, data, "Kicks", "fight.attack.kick")
//                        addStat(this, data, "Slams", "fight.attack.slam")
//                        addStat(this, data, "Wins", "fight.wins")
//                    }
//                    "hangman" -> {
//                        addStat(this, data, "Total Guesses", "hangman.total-guesses")
//                        addStat(this, data, "Correct Guesses", "hangman.correct-guesses")
//                        // TODO: Method for computed stats?
//                        addField("Accuracy", "${((data.getStat("hangman.correct-guesses") * 100.0) / data.getStat("hangman.total-guesses")).roundToInt()}%", true)
//                        addStat(this, data, "Wins", "hangman.wins")
//                    }
//                    "2048" -> {
//                        addStat(this, data, "Total Moves", "2048.total-moves")
//                        addStat(this, data, "Highest Score", "2048.highest-score")
//                        addStat(this, data, "Wins", "2048.wins")
//                    }
//                    else -> {
//                        return@thenAccept fail(event, "That is not a valid game!")
//                    }
//                }
//
//                setFooter("${event.author.name}'s stats")
//                setThumbnail(event.author.avatarUrl)
//                setColor(0x353940)
//
//
//                build()
//            }
//
//            event.channel.sendMessage(embed).queue()
//        }
//    }
//
//    private fun addStat(embed: EmbedBuilder, data: PlayerData, name: String, stat: String) {
//        embed.addField(name, String.format("%,d", data), true)
//    }
//
//}
