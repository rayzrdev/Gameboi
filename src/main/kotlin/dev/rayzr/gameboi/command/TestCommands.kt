package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.hangman.HangmanGame
import dev.rayzr.gameboi.game.twenty48.Twenty48Game
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object Twenty48TestCommand : Command("2048test", "2048 test command") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val match = Match(Twenty48Game, event.channel)
        match.addPlayer(Player[event.author])
    }
}

object HangmanTestCommand : Command("hangman", "Starts a game of hangman!") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val match = Match(HangmanGame, event.channel)
        match.addPlayer(Player[event.author])
    }
}