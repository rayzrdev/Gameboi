package dev.rayzr.gameboi.game.hangman

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.RenderingHints
import java.awt.image.BufferedImage

object Images {
    fun get(path: String) = RenderUtils.loadImage("hangman/$path")!!

    val background = get("background.png")
    val post = get("post.png")
    val dead = get("dead.png")
    val blank = get("blank.png")
}

object HangmanGame : Game(700, 600, "Hangman", 1) {
    private fun draw(match: Match) {
        val data = getData(match)

        render(match, emptyList(), "Hangman") {
            clear()
            graphics.run {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                scale(10.0, 10.0)

                drawImage(Images.background, 0, 0, null)

                // Post
                drawImage(Images.post, 10, 9, null)

                // TODO: Draw failed letters
                // TODO: Draw blanks
                // TODO: Draw correctly guessed letters
            }
        }
    }

    override fun begin(match: Match) {
        draw(match)
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        // TODO: Get user input!
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        // Ignore
    }

    private fun getData(match: Match) = match.data as HangmanMatchData

    override fun createData(match: Match): MatchData = HangmanMatchData()

    class Letter(val char: Char, val image: BufferedImage) {
        companion object {
            private val letters: Map<Char, Letter> = "abcdefghijklmnopqrstuvwxyz".toCharArray()
                    .associateBy({ it }, { Letter(it, Images.get("letter-$it.png")) })

            operator fun get(char: Char): Letter? {
                return letters[char]
            }
        }
    }

    enum class Direction(val diff: Int) {
        LEFT(-1),
        UP(-4),
        DOWN(4),
        RIGHT(1)
    }
}