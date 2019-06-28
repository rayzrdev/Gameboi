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
    val blank = get("blank.png")
}

object HangmanGame : Game(700, 600, "Hangman", 1) {
    private fun draw(match: Match, status: String) {
        val data = getData(match)

        render(match, emptyList(), status) {
            clear()
            graphics.run {
                setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

                scale(10.0, 10.0)

                drawImage(Images.background, 0, 0, null)

                // Post
                drawImage(Images.post, 10, 9, null)

                // Incorrect letters
                data.incorrectLetters.forEachIndexed { index, c ->
                    val col = index % 6
                    val row = index / 6

                    val letter = Letter[c] ?: return@forEachIndexed
                    drawImage(letter.image, 38 + col * 4, 8 + row * 6, null)
                }

                // Blanks & correct letters
                val blankOffsetX = Images.background.width / 2 - data.word.length * 2
                val blankOffsetY = 44

                val word = if (data.isDead) {
                    data.word
                } else {
                    data.blankedWord
                }

                word.forEachIndexed { index, c ->
                    drawImage(Images.blank, blankOffsetX + index * 4, blankOffsetY, null)

                    val letter = Letter[c] ?: return@forEachIndexed
                    drawImage(letter.image, blankOffsetX + index * 4, blankOffsetY, null)
                }

                // Player
                if (data.isDead) {
                    drawImage(HangmanStage.stages[6].image, 20, 25, null)
                } else if (data.currentStage >= 0) {
                    for (i in 0..data.currentStage) {
                        drawImage(HangmanStage.stages[i].image, 22, 14, null)
                    }
                }

                // Stop being gay, kotlin
                return@run
            }

            when {
                data.isDead -> renderCenteredText("You died!")
                data.hasWon -> renderCenteredText("You won!")
            }
        }
    }

    override fun begin(match: Match) {
        draw(match, ":thinking: Type a letter in chat to guess it!")
    }

    override fun handleMessage(player: Player, match: Match, message: Message) {
        if (!match.players.contains(player)) {
            // Not playing this game
            return
        }

        val input = message.contentRaw.toLowerCase()
        if (input.length > 1) {
            // More than one letter
            return
        }

        val letter = input[0]
        if (Letter[letter] == null) {
            // Ignore invalid letters
            return
        }

        message.delete().queue()

        val data = getData(match)

        if (data.attemptedLetters.contains(letter)) {
            draw(match, ":no_entry_sign: You have already tried that letter!")
        } else {
            val displayLetter = letter.toUpperCase()

            data.attemptedLetters.add(letter)

            if (data.incorrectLetters.contains(letter)) {
                player.editData { updateStatBy("hangman.total-guesses", 1) }

                if (data.isDead) {
                    draw(match, ":x: You tried **$displayLetter**, but it is not part of the word! The word was **${data.word}**. You have died!")
                    match.end()
                } else {
                    draw(match, ":x: You tried **$displayLetter**, but it is not part of the word!")
                }
            } else {
                player.editData {
                    updateStatBy("hangman.total-guesses", 1)
                    updateStatBy("hangman.correct-guesses", 1)
                }

                // All blanks filled
                if (!data.blankedWord.contains(" ")) {
                    val coinsEarned = (5..10).random() + (5 - data.currentStage) * 5

                    player.editData {
                        updateStatBy("hangman.wins", 1)
                        coins += coinsEarned
                    }

                    draw(match, ":tada: You tried **$displayLetter** and you guessed the word! The word was **${data.word}**. Congratulations, you have earned **$coinsEarned** coins!")
                    match.end()
                } else {
                    draw(match, ":white_check_mark: You tried **$displayLetter** and you guessed correctly!")
                }
            }
        }
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