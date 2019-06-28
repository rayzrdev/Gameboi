package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.data.PlayerData
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object HelpCommand : Command("help", "Shows you help for Gameboi") {
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

object InviteCommand : Command("invite", "Gives you an invite link for Gameboi") {
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

object PingCommand : Command("ping", "Shows you the bot's ping") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        event.channel.sendMessage(":stopwatch: Pong! `${event.jda.gatewayPing}ms`").queue()
    }
}

object StatsCommand : Command("stats", "Shows your game stats", "stats [game]") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        Player[event.author].getData().thenAccept { data ->
            val embed = EmbedBuilder().run {

                // TODO: Set up stat registry to automate this PLEASE
                when (args.joinToString(" ").toLowerCase()) {
                    "" -> {
                        setDescription("To see stats for a specific game, type `${Gameboi.prefix}stats <game>`")

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

    private fun addStat(embed: EmbedBuilder, data: PlayerData, name: String, stat: String) {
        embed.addField(name, String.format("%,d", data.getStat(stat)), true)
    }
}