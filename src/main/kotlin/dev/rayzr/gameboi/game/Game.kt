package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.render.RenderContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction

abstract class Game(private val width: Int, private val height: Int, val name: String, val maxPlayers: Int) {
    fun createRenderContext(match: Match): RenderContext = RenderContext(match.channel, width, height)

    abstract fun begin(match: Match)
    abstract fun handleMessage(player: Player, match: Match, message: Message)
    abstract fun handleReaction(player: Player, match: Match, reaction: MessageReaction)
}
