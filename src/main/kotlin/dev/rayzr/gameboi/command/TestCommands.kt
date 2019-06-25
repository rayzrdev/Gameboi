package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.fight.FightGame
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object FightGameTest : Command("fight", "Invites a player to play Fight with you!", "fight <other>") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (event.message.mentionedMembers.size < 1) {
            event.channel.sendMessage(":x: Please mention the user you would like to play with!")
            return
        }

        val otherUser = event.message.mentionedMembers[0]

        val match = Match(FightGame, event.channel)

        match.addPlayer(Player[event.author])
        match.addPlayer(Player[otherUser.user])
    }
}