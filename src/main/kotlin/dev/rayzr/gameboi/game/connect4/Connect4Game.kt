package dev.rayzr.gameboi.game.connect4

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.Color
import java.awt.RenderingHints
import kotlin.math.max

object Connect4Game : Game(500, 400, "Connect 4", 2) {
    val emojis = listOf("\u0031\u20e3", "\u0032\u20e3", "\u0033\u20e3", "\u0034\u20e3", "\u0035\u20e3", "\u0036\u20e3")

    private fun draw(match: Match, board: Array<Slot>) {
        val winner = getData(match).winner
        val emojisToRender = if (winner == null) {
            emojis
        } else {
            emptyList()
        }

        render(match, emojisToRender) {
            clear()
            graphics.run {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                color = Color.BLUE
                fillRect(45, 60, 410, 340)

                val offsetX = 60
                val offsetY = 70
                val widthX = 65
                val widthY = 55

                board.forEachIndexed { index, slot ->
                    val row = index / 6
                    val col = index % 6

                    color = slot.color
                    fillOval(offsetX + widthX * col, offsetY + widthY * row, 50, 50)
                }
            }

            if (winner != null) {
                renderText("Winner:\n${winner.user.name}", 10, 30)
            }
        }
    }

    private fun getData(match: Match) = match.data as Connect4MatchData

    override fun createData(match: Match): MatchData = Connect4MatchData()

    override fun begin(match: Match) {
        draw(match, getData(match).board)
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        // TODO: Ignore?
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        // TODO: Check which column, update board, and re-send
        if (!emojis.contains(reaction.reactionEmote.name)) return


        val data = getData(match)
        val board = data.board

        if (match.players.indexOf(player) != data.currentPlayer) {
            reaction.removeReaction(player.user).queue()
            return
        }

        val col = emojis.indexOf(reaction.reactionEmote.name)

        val slotType = when (data.currentPlayer) {
            0 -> Slot.ONE
            else -> Slot.TWO
        }

        var index = -1
        for (i in 5.downTo(0)) {
            val temp = i * 6 + col
            if (board[temp] == Connect4Game.Slot.EMPTY) {
                index = temp
                break
            }
        }

        if (index == -1) {
            reaction.removeReaction(player.user).queue()
            return
        }

        board[index] = slotType
        data.currentPlayer = (data.currentPlayer + 1) % 2

        val row = index % 6

        if (checkForWins(slotType, row, col, board)) {
            data.winner = player
            match.end()
        }

        draw(match, board)
    }

    private fun checkForWins(type: Slot, row: Int, col: Int, board: Array<Slot>): Boolean {
        var consecutiveHorizontal = 0
        var currentHorizontal = 0

        for (i in 0..5) {
            val next = board[row * 6 + i]
            if (next != type) {
                consecutiveHorizontal = max(consecutiveHorizontal, currentHorizontal)
                currentHorizontal = 0
            } else {
                currentHorizontal++
            }
        }
        consecutiveHorizontal = max(consecutiveHorizontal, currentHorizontal)

        if (consecutiveHorizontal >= 4) {
            return true
        }

        var consecutiveVertical = 0
        var currentVertical = 0

        for (i in 0..5) {
            val next = board[i * 6 + col]
            if (next != type) {
                consecutiveVertical = max(consecutiveVertical, currentVertical)
                currentVertical = 0
            } else {
                currentVertical++
            }
        }
        consecutiveVertical = max(consecutiveVertical, currentVertical)

        if (consecutiveVertical >= 4) {
            return true
        }

        for (x in 0..2) {
            for (y in 0..2) {
                if ((0..3).all { board[(x + it) + (y + it) * 6] == type }) {
                    return true
                } else if ((0..3).all { board[(x + it) + (5 - (y + it)) * 6] == type }) {
                    return true
                }
            }
        }

        return false
    }

    enum class Slot(val color: Color) {
        ONE(Color.RED),
        TWO(Color.YELLOW),
        EMPTY(Color.BLACK);
    }

    class Connect4MatchData : MatchData {
        val board = Array(36) { Slot.EMPTY }
        var currentPlayer = 0
        var winner: Player? = null
    }
}