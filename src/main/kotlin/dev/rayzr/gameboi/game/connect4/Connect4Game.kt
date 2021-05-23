package dev.rayzr.gameboi.game.connect4

import dev.rayzr.gameboi.data.shop.connect4.CONNECT_4_DESIGN_SLOT
import dev.rayzr.gameboi.data.shop.connect4.Connect4DesignItem
import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.math.max

object Connect4Game : Game(700, 600, "Connect 4", 2) {
    private val emojis =
        listOf("\u0031\u20e3", "\u0032\u20e3", "\u0033\u20e3", "\u0034\u20e3", "\u0035\u20e3", "\u0036\u20e3")

    private val boardImage = RenderUtils.loadImage("connect4/board.png")!!

    private fun draw(match: Match) {
        val data = getData(match)
        val winner = data.winner
        val board = data.board

        val emojisToRender = when {
            winner != null || board.none { it == Slot.EMPTY } -> emptyList()
            else -> emojis
        }

        val message = when {
            board.none { it == Slot.EMPTY } -> ":handshake: ${match.players.joinToString(" and ") { "**${it.user.name}**" }} have drawn!"
            winner == null -> ":thinking: **${match.players[data.currentPlayer].user.name}**'s turn!"
            else -> ":tada: **${winner.user.name}** has won and has earned **${data.coinsWon}** coins!"
        }

        match.players[0].getData().thenAccept { playerOneData ->
            match.players[1].getData().thenAccept { playerTwoData ->
                render(match, emojisToRender, message, match.players[data.currentPlayer].user.asMention) {
                    clear()
                    graphics.run {
                        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                        setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                        drawImage(boardImage, 0, 0, 700, 600, null)
                        scale(10.0, 10.0)

                        val offsetX = 12
                        val offsetY = 16
                        val widthX = 6
                        val widthY = 6
                        val gapX = 2
                        val gapY = 1

                        board.forEachIndexed { index, slot ->
                            val row = index / 6
                            val col = index % 6

                            val realX = offsetX + (widthX + gapX) * col
                            val realY = offsetY + (widthY + gapY) * row

                            drawImage(slot.image, realX, realY, widthX, widthY, null)

                            val playerData = when (slot) {
                                Connect4Game.Slot.ONE -> playerOneData
                                Connect4Game.Slot.TWO -> playerTwoData
                                else -> null
                            }

                            // Draw designs if applicable
//                            println("$playerData - ${playerData?.equipment?.get(CONNECT_4_DESIGN_SLOT.internalName)}")
                            if (playerData != null && playerData.equipment[CONNECT_4_DESIGN_SLOT.internalName] is Connect4DesignItem) {
                                val item =
                                    playerData.equipment[CONNECT_4_DESIGN_SLOT.internalName] as Connect4DesignItem
                                drawImage(item.image, realX, realY, null)
                            }
                        }
                    }

                    when {
                        winner != null -> renderCenteredText("${winner.user.name} wins!")
                        board.none { it == Slot.EMPTY } -> renderCenteredText("It's a draw!")
                    }
                }
            }
        }
    }

    private fun getData(match: Match) = match.data as Connect4MatchData

    override fun createData(match: Match): MatchData = Connect4MatchData()

    override fun begin(match: Match) {
        draw(match)
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        if (!emojis.contains(reaction.reactionEmote.name)) return

        val data = getData(match)
        val board = data.board

        reaction.removeReaction(player.user).queue()

        if (match.players.indexOf(player) != data.currentPlayer) {
            return
        }

        val col = emojis.indexOf(reaction.reactionEmote.name)

        val slotType = when (data.currentPlayer) {
            0 -> Slot.ONE
            else -> Slot.TWO
        }

        val user = player.user
        val scope = match.channel.guild

        player.editData { updateStatBy(user, scope, "connect4.played-${slotType.colorName}", 1) }

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

        val row = index / 6

        if (checkForWins(slotType, row, col, board)) {
            data.winner = player
            data.coinsWon = (15..25).random()
            player.editData {
                updateStatBy(user, scope, "connect4.wins", 1)
                coins += data.coinsWon
            }
            match.end()
        }

        draw(match)
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

    enum class Slot(val colorName: String, val image: BufferedImage) {
        ONE("red", RenderUtils.loadImage("connect4/player-one.png")!!),
        TWO("yellow", RenderUtils.loadImage("connect4/player-two.png")!!),
        EMPTY("n/a", BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
    }

    class Connect4MatchData : MatchData {
        val board = Array(36) { Slot.EMPTY }
        var currentPlayer = 0
        var winner: Player? = null
        var coinsWon: Int = 0
    }
}