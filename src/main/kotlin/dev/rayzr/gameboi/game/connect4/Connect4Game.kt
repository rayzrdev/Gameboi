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
        render(match, listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣")) {
            clear()
            graphics.run {
                color = Color.BLUE
                fillRect(50, 80, 400, 320)

                val offsetX = 50
                val offsetY = 80

                board.forEachIndexed { index, slot ->
                    val row = index % 6
                    val col = index / 6

                    color = slot.color
                    fillOval(offsetX + 50 * col, offsetY + 50 * row, 50, 50)
                }
            }
        }
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        // TODO: Ignore?
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        // TODO: Check which column, updated board, and re-send
    }

    enum class Slot(val color: Color) {
        RED(Color.RED),
        BLUE(Color.BLUE),
        EMPTY(Color.BLACK);
    }
}