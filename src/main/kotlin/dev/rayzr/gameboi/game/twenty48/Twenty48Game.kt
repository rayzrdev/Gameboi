package dev.rayzr.gameboi.game.twenty48

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.Color
import java.awt.RenderingHints
import kotlin.random.Random

object Twenty48Game : Game(800, 700, "2048", 1) {
    val emojis = listOf("\u2b05", "\u2b06", "\u2b07", "\u27a1")

    private fun draw(match: Match) {
        val data = getData(match)
        val board = data.board

        val emojisToRender = if (board.any { it != Tile.TWOZEROFOUREIGHT }) {
            emojis
        } else {
            emptyList()
        }

        render(match, emojisToRender, "2048") {
            clear()
            graphics.run {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                color = Color.decode("#f48cb6")
                fillRect(100, 50, 600, 600)

                board.forEachIndexed { index, tile ->
                    val row = index / 4
                    val col = index % 4

                    color = tile.color
                    fillRoundRect(125 + col * 140, 75 + row * 140, 128, 128, 5, 5)
                    if (tile.value > 0) renderText(tile.value.toString(), 189 + col * 140, 139 + row * 140)
                }
            }
        }
    }

    private fun addTile(match: Match) {
        val data = getData(match)
        val board = data.board

        var tilePos = Random.nextInt(16)
        while (board[tilePos] != Tile.EMPTY) tilePos = Random.nextInt(16)
        val tile = if (Random.nextInt(4) == 3) Tile.FOUR else Tile.TWO
        getData(match).board[tilePos] = tile
    }

    private fun moveTiles(match: Match, direction: Direction) {
        val data = getData(match)
        val board = data.board

        when (direction) {
            Direction.LEFT, Direction.UP -> {
                board.forEachIndexed { index, tile ->
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.LEFT) {
                            val row = (index / 4) * 4
                            val col = index % 4
                            var newIndex = index

                            for (i in 0..col) {
                                if (newIndex - 1 >= row && board[newIndex - 1] == Tile.EMPTY) newIndex = row + col - i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            if (newIndex - 1 >= row && board[newIndex - 1] == tile) {
                                val newTile = Tile.values().find { it.value == tile.value * 2 }!!
                                board[newIndex - 1] = newTile
                                board[newIndex] = Tile.EMPTY
                            }
                        } else {
                            val row = index / 4
                            val col = index % 4
                            var newIndex = index
                        }
                    }
                }
            }
            Direction.DOWN, Direction.RIGHT -> {
                for (index in board.size - 1 downTo 0) {
                    val tile = board[index]
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.RIGHT) {
                            val row = (index / 4) * 4
                            val col = index % 4
                            var newIndex = index

                            for (i in 0..col) {
                                if (newIndex + 1 <= (row + 3) && board[newIndex + 1] == Tile.EMPTY) newIndex = row + col + i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            if (newIndex + 1 <= (row + 3) && board[newIndex + 1] == tile) {
                                val newTile = Tile.values().find { it.value == tile.value * 2 }!!
                                board[newIndex + 1] = newTile
                                board[newIndex] = Tile.EMPTY
                            }
                        } else {
                            val row = index / 4
                            val col = index % 4
                            var newIndex = index
                        }
                    }
                }
            }
        }

        addTile(match)
        draw(match)
    }

    override fun begin(match: Match) {
        for (i in 0..1) addTile(match)
        draw(match)
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        if (!emojis.contains(reaction.reactionEmote.name)) return

        val direction: Direction = when (reaction.reactionEmote.name) {
            "\u2b05" -> Direction.LEFT
            "\u2b06" -> Direction.UP
            "\u2b07" -> Direction.DOWN
            else -> Direction.RIGHT
        }

        moveTiles(match, direction)
    }

    private fun getData(match: Match) = match.data as Twenty48MatchData

    override fun createData(match: Match): MatchData = Twenty48MatchData()

    enum class Tile(val value: Int, val color: Color) {
        EMPTY(0, Color.WHITE),
        TWO(2, Color(0x9b9c82)),
        FOUR(4, Color(0xf7b69e)),
        EIGHT(8, Color(0xf7e476)),
        ONESIX(16, Color(0x6df7c1)),
        THREETWO(32, Color(0xa1e55a)),
        SIXFOUR(64, Color(0x11adc1)),
        ONETWOEIGHT(128, Color(0x5bb361)),
        TWOFIVESIX(256, Color(0x1e8875)),
        FIVEONETWO(512, Color(0x606c81)),
        ONEZEROTWOFOUR(1024, Color(0x6a3771)),
        TWOZEROFOUREIGHT(2048, Color(0x393457))
    }

    enum class Direction(val diff: Int) {
        LEFT(-1),
        UP(-4),
        DOWN(4),
        RIGHT(1)
    }

    class Twenty48MatchData : MatchData {
        val board = Array(16) { Tile.EMPTY }
    }
}