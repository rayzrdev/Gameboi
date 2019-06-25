package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.game.connect4.Connect4Game
import dev.rayzr.gameboi.manager.MatchManager
import dev.rayzr.gameboi.render.RenderContext
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.awt.Color

object RenderTestCommand : Command("rendertest", "Tests the RenderContext system", "rendertest [message]") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val message = args.joinToString(" ").ifEmpty { "Test" }

        val context = RenderContext(event.channel, 500, 300)

        context.graphics.run {
            color = Color.RED
            fillRect(10, 10, 480, 30)
            color = Color.BLUE
            fillOval(50, 50, 100, 100)
            color = Color.WHITE
        }

        context.renderText(message, 20, 30)

        context.draw {
            context.clear()
            context.renderText(message, 50, 50, 50)
            context.draw()
        }
    }
}

object MatchTestCommand : Command("matchtest", "Creates a test match", "matchtest <other>") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val otherUser = event.message.mentionedMembers[0]

        val match = Match(Connect4Game, event.channel)

        match.addPlayer(Player(event.author))
        match.addPlayer(Player(otherUser.user))
    }
}