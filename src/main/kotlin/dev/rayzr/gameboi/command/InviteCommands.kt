package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.connect4.Connect4Game
import dev.rayzr.gameboi.game.fight.FightGame
import dev.rayzr.gameboi.game.hangman.HangmanGame
import dev.rayzr.gameboi.game.twenty48.Twenty48Game
import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*
import kotlin.concurrent.schedule

object Connect4Invite : Command("connect4", "Invites a player to play Connect4 with you!", "connect4 <other>") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (event.message.mentionedMembers.size < 1) {
            event.channel.sendMessage(":x: Please mention the user you would like to play with!").queue {
                Timer().schedule(Gameboi.errorLife) {
                    it.textChannel.deleteMessages(listOf(it, event.message)).queue()
                }
            }
            return
        }

        val otherUser = event.message.mentionedMembers[0]

        InviteManager.invite(event.message, Player[event.author], Player[otherUser.user], Connect4Game)
    }
}

object FightInvite : Command("fight", "Invites a player to play Fight with you!", "fight <other>") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (event.message.mentionedMembers.size < 1) {
            event.channel.sendMessage(":x: Please mention the user you would like to play with!").queue {
                Timer().schedule(Gameboi.errorLife) {
                    it.textChannel.deleteMessages(listOf(it, event.message)).queue()
                }
            }
            return
        }

        val otherUser = event.message.mentionedMembers[0]

        InviteManager.invite(event.message, Player[event.author], Player[otherUser.user], FightGame)
    }
}

object Twenty48Invite: Command("2048", "Starts a 2048 game.", "2048") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        InviteManager.singlePlayer(event.message, Player[event.author], Twenty48Game)
    }
}

object HangmanInvite: Command("hangman", "Starts a hangman game.", "hangman") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        InviteManager.singlePlayer(event.message, Player[event.author], HangmanGame)
    }
}