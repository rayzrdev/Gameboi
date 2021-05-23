package dev.rayzr.gameboi.game.twenty48

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import kotlin.random.Random

object Images {
    val background = RenderUtils.loadImage("2048/background.png")!!
    val tile2 = RenderUtils.loadImage("2048/2.png")!!
    val tile4 = RenderUtils.loadImage("2048/4.png")!!
    val tile8 = RenderUtils.loadImage("2048/8.png")!!
    val tile16 = RenderUtils.loadImage("2048/16.png")!!
    val tile32 = RenderUtils.loadImage("2048/32.png")!!
    val tile64 = RenderUtils.loadImage("2048/64.png")!!
    val tile128 = RenderUtils.loadImage("2048/128.png")!!
    val tile256 = RenderUtils.loadImage("2048/256.png")!!
    val tile512 = RenderUtils.loadImage("2048/512.png")!!
    val tile1024 = RenderUtils.loadImage("2048/1024.png")!!
    val tile2048 = RenderUtils.loadImage("2048/2048.png")!!
}

object Twenty48Game : Game(600, 600, "2048", 1) {
    private val emojis = listOf("\u2b05", "\u2b06", "\u2b07", "\u27a1")

    private fun draw(match: Match) {
        val data = getData(match)
        val board = data.board

        val coinsWon = (25..35).random()

        val emojisToRender = when {
            board.any { it == Tile.TWOZEROFOUREIGHT } || !canPlay(match) -> emptyList()
            else -> emojis
        }

        val score = board.sumBy { it.value }

        val message = when {
            board.any { it == Tile.TWOZEROFOUREIGHT } -> ":tada: **${match.players[0].user.name}** has won and has earned **$coinsWon** coins! Your final score was **$score**."
            !canPlay(match) -> ":thumbsdown: **${match.players[0].user.name}** has lost! Your final score was **$score**."
            else -> ":thinking: **${match.players[0].user.name}** is playing 2048! Your current score is **$score**."
        }

        render(match, emojisToRender, message) {
            clear()
            graphics.run {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                scale(10.0, 10.0)
                drawImage(Images.background, 0, 0, null)

                board.forEachIndexed { index, tile ->
                    val row = index / 4
                    val col = index % 4

                    drawImage(tile.image, 7 + col * 12, 7 + row * 12, null)
                }
            }

            when {
                board.any { it == Tile.TWOZEROFOUREIGHT } -> {
                    renderCenteredText("You won!")
                    match.players[0].editData {
                        updateStatBy(match.players[0].user, match.channel.guild, "2048.wins", 1)
                        coins += coinsWon
                    }
                    match.end()
                }
                !canPlay(match) -> {
                    renderCenteredText("You lost!")
                    match.end()
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

    private fun moveTiles(match: Match, direction: Direction): Boolean {
        val data = getData(match)
        val board = data.board

        val moved = canMove(match, direction)

        if (!moved) return moved

        when (direction) {
            Direction.LEFT, Direction.UP -> {
                board.forEachIndexed { index, tile ->
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.LEFT) {
                            val row = (index / 4) * 4
                            val col = index % 4
                            var newIndex = index

                            for (i in 0..col) {
                                if (index - i >= row && board[index - i] == Tile.EMPTY) newIndex = index - i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            for (i in 0..col) {
                                newIndex = index - i
                                if (newIndex - 1 >= row && board[newIndex] == board[newIndex - 1]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex - 1] = newTile
                                }
                            }
                        } else {
                            var newIndex = index

                            for (i in 0..12 step 4) {
                                if (index - i >= 0 && board[index - i] == Tile.EMPTY) newIndex = index - i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            for (i in 0..12 step 4) {
                                newIndex = index - i
                                if (newIndex - 4 >= 0 && board[newIndex] == board[newIndex - 4]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex - 4] = newTile
                                }
                            }
                        }
                    }
                }
            }
            Direction.DOWN, Direction.RIGHT -> {
                for (index in board.size - 1 downTo 0) {
                    val tile = board[index]
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.RIGHT) {
                            val row = (index / 4) * 4 + 3
                            var newIndex = index

                            for (i in 0..3) {
                                if (index + i <= row && board[index + i] == Tile.EMPTY) newIndex = index + i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            for (i in 0..3) {
                                newIndex = index + i
                                if (newIndex + 1 <= row && board[newIndex] == board[newIndex + 1]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex + 1] = newTile
                                }
                            }
                        } else {
                            var newIndex = index

                            for (i in 0..12 step 4) {
                                if (index + i <= 15 && board[index + i] == Tile.EMPTY) newIndex = index + i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile

                            for (i in 0..12 step 4) {
                                newIndex = index + i
                                if (newIndex + 4 <= 15 && board[newIndex] == board[newIndex + 4]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex + 4] = newTile
                                }
                            }
                        }
                    }
                }
            }
        }

        if (moved) {
            val score = board.sumBy { it.value }

            match.players[0].editData {
                updateStatBy(match.players[0].user, match.channel.guild, "2048.total-moves", 1)
                if (getStat("2048.highest-score") < score) {
                    setStat(match.players[0].user, match.channel.guild, "2048.highest-score", score)
                }
            }

            addTile(match)
            draw(match)
        }

        return moved
    }

    private fun canMove(match: Match, direction: Direction): Boolean {
        val board = getData(match).board.clone()
        var moved = false

        when (direction) {
            Direction.LEFT, Direction.UP -> {
                board.forEachIndexed { index, tile ->
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.LEFT) {
                            val row = (index / 4) * 4
                            val col = index % 4
                            var newIndex = index

                            for (i in 0..col) {
                                if (index - i >= row && board[index - i] == Tile.EMPTY) newIndex = index - i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile
                            if (index != newIndex) moved = true

                            for (i in 0..col) {
                                newIndex = index - i
                                if (newIndex - 1 >= row && board[newIndex] == board[newIndex - 1]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex - 1] = newTile
                                    moved = true
                                }
                            }
                        } else {
                            var newIndex = index

                            for (i in 0..12 step 4) {
                                if (index - i >= 0 && board[index - i] == Tile.EMPTY) newIndex = index - i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile
                            if (index != newIndex) moved = true

                            for (i in 0..12 step 4) {
                                newIndex = index - i
                                if (newIndex - 4 >= 0 && board[newIndex] == board[newIndex - 4]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex - 4] = newTile
                                    moved = true
                                }
                            }
                        }
                    }
                }
            }
            Direction.DOWN, Direction.RIGHT -> {
                for (index in board.size - 1 downTo 0) {
                    val tile = board[index]
                    if (tile != Tile.EMPTY) {
                        if (direction == Direction.RIGHT) {
                            val row = (index / 4) * 4 + 3
                            var newIndex = index

                            for (i in 0..3) {
                                if (index + i <= row && board[index + i] == Tile.EMPTY) newIndex = index + i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile
                            if (index != newIndex) moved = true

                            for (i in 0..3) {
                                newIndex = index + i
                                if (newIndex + 1 <= row && board[newIndex] == board[newIndex + 1]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex + 1] = newTile
                                    moved = true
                                }
                            }
                        } else {
                            var newIndex = index

                            for (i in 0..12 step 4) {
                                if (index + i <= 15 && board[index + i] == Tile.EMPTY) newIndex = index + i
                            }
                            board[index] = Tile.EMPTY
                            board[newIndex] = tile
                            if (index != newIndex) moved = true

                            for (i in 0..12 step 4) {
                                newIndex = index + i
                                if (newIndex + 4 <= 15 && board[newIndex] == board[newIndex + 4]) {
                                    val newTile = Tile.values().find { it.value == board[newIndex].value * 2 }!!
                                    board[newIndex] = Tile.EMPTY
                                    board[newIndex + 4] = newTile
                                    moved = true
                                }
                            }
                        }
                    }
                }
            }
        }
        return moved
    }

    private fun canPlay(match: Match): Boolean {
        return Direction.values().any { canMove(match, it) }
    }

    override fun begin(match: Match) {
        for (i in 0..1) addTile(match)
        draw(match)
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {}

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        if (!emojis.contains(reaction.reactionEmote.name)) return

        reaction.removeReaction(player.user).queue()

        if (!match.players.contains(player)) {
            return
        }

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

    enum class Tile(val value: Int, val image: BufferedImage) {
        EMPTY(0, BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)),
        TWO(2, Images.tile2),
        FOUR(4, Images.tile4),
        EIGHT(8, Images.tile8),
        ONESIX(16, Images.tile16),
        THREETWO(32, Images.tile32),
        SIXFOUR(64, Images.tile64),
        ONETWOEIGHT(128, Images.tile128),
        TWOFIVESIX(256, Images.tile256),
        FIVEONETWO(512, Images.tile512),
        ONEZEROTWOFOUR(1024, Images.tile1024),
        TWOZEROFOUREIGHT(2048, Images.tile2048)
    }

    enum class Direction {
        LEFT,
        UP,
        DOWN,
        RIGHT
    }

    class Twenty48MatchData : MatchData {
        val board = Array(16) { Tile.EMPTY }
    }
}