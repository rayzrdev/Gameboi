package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.connect4.Connect4Game
import dev.rayzr.gameboi.game.fight.FightGame
import dev.rayzr.gameboi.game.hangman.HangmanGame
import dev.rayzr.gameboi.game.twenty48.Twenty48Game
import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.concurrent.TimeUnit

fun checkPermissions(event: GuildMessageReceivedEvent): Boolean {
    if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_WRITE)) {
        // Can't do anything about this, oh well
        return false
    }

    if (!event.guild.selfMember.hasPermission(event.channel, Permission.MESSAGE_MANAGE, Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS)) {
        event.channel.sendMessage(":x: This bot is missing the permissions required to manage/delete messages, attach images, and embed links!").queue()
        return false
    }

    return true
}

fun checkValidPlayer(player: User) = !player.isBot

object Connect4Invite : Command("connect4", "Invites a player to play Connect4 with you!", "connect4 <other>", Categories.GAMES) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (!checkPermissions(event)) {
            return
        }

        if (event.message.mentionedMembers.size < 1) {
            event.channel.sendMessage(":x: Please mention the user you would like to play with!").queue {
                it.textChannel.deleteMessages(listOf(it, event.message)).queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
            return
        }

        val otherUser = event.message.mentionedMembers[0]

        if (!checkValidPlayer(otherUser.user)) {
            event.channel.sendMessage(":x: You cannot challenge bots, please challenge a valid user!")
            return
        }

        InviteManager.invite(event.message, Player[event.author], Player[otherUser.user], Connect4Game)
    }
}

object FightInvite : Command("fight", "Invites a player to play Fight with you!", "fight <other>", Categories.GAMES) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (!checkPermissions(event)) {
            return
        }

        if (event.message.mentionedMembers.size < 1) {
            event.channel.sendMessage(":x: Please mention the user you would like to play with!").queue {
                it.textChannel.deleteMessages(listOf(it, event.message)).queueAfter(Gameboi.errorLife, TimeUnit.MILLISECONDS)
            }
            return
        }

        val otherUser = event.message.mentionedMembers[0]

        if (!checkValidPlayer(otherUser.user)) {
            event.channel.sendMessage(":x: You cannot challenge bots, please challenge a valid user!")
            return
        }

        InviteManager.invite(event.message, Player[event.author], Player[otherUser.user], FightGame)
    }
}

object Twenty48Invite: Command("2048", "Starts a 2048 game.", "2048", Categories.GAMES) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (!checkPermissions(event)) {
            return
        }

        InviteManager.singlePlayer(event.message, Player[event.author], Twenty48Game)
    }
}

object HangmanInvite: Command("hangman", "Starts a hangman game.", "hangman", Categories.GAMES) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (!checkPermissions(event)) {
            return
        }

        InviteManager.singlePlayer(event.message, Player[event.author], HangmanGame)
    }
}