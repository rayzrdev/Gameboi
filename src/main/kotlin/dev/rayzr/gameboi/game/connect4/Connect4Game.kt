package dev.rayzr.gameboi.game.connect4

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction

object Connect4Game : Game(500, 400, "Connect 4", 2) {
    val board: Array<Array<Slot>> = Array(6) {
        Array(6) {
            Connect4Game.Slot.EMPTY
        }
    }

    override fun begin(match: Match) {
        // TODO: Send initial frame
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        // TODO: Ignore?
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        // TODO: Check which column, updated board, and re-send
    }

    enum class Slot {
        RED,
        BLUE,
        EMPTY
    }
}