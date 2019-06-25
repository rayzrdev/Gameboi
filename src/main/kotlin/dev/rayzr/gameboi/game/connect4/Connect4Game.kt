package dev.rayzr.gameboi.game.connect4

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.Color

object Connect4Game : Game(500, 400, "Connect 4", 2) {
    val board: Array<Slot> = Array(36) { Connect4Game.Slot.EMPTY }

    override fun begin(match: Match) {
        render(match, listOf("\u0031\u20e3", "\u0032\u20e3", "\u0033\u20e3", "\u0034\u20e3", "\u0035\u20e3", "\u0036\u20e3")) {
            clear()
            graphics.run {
                color = Color.BLUE
                fillRect(45, 60, 410, 340)

                val offsetX = 60
                val offsetY = 70
                val widthX = 65
                val widthY = 55

                board.forEachIndexed { index, slot ->
                    val row = index % 6
                    val col = index / 6

                    color = slot.color
                    fillOval(offsetX + widthX * col, offsetY + widthY * row, 50, 50)
                }
            }
        }
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        // TODO: Ignore?
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        // TODO: Check which column, update board, and re-send
    }

    enum class Slot(val color: Color) {
        RED(Color.RED),
        YELLOW(Color.YELLOW),
        EMPTY(Color.BLACK);
    }
}