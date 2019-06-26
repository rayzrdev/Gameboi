package dev.rayzr.gameboi.game.hangman

import dev.rayzr.gameboi.game.MatchData
import java.awt.Image
import kotlin.math.min

class HangmanMatchData : MatchData {
    val attemptedLetters: MutableSet<Char> = mutableSetOf()
    val word = HANGMAN_WORDS.random()

    val incorrectLetters get() = attemptedLetters.filter { !word.contains(it) }
    val blankedWord
        get() = word.map {
            if (attemptedLetters.contains(it)) {
                it
            } else {
                " "
            }
        }.joinToString("")
    val currentStage get() = min(incorrectLetters.size - 1, 6)
    val isDead get() = currentStage == 6
    val hasWon get() = word.all { attemptedLetters.contains(it) }
}

class HangmanStage(val index: Int, val image: Image) {
    companion object {
        val stages = listOf(
                HangmanStage(0, Images.get("character/head.png")),
                HangmanStage(1, Images.get("character/body.png")),
                HangmanStage(2, Images.get("character/leg-left.png")),
                HangmanStage(3, Images.get("character/leg-right.png")),
                HangmanStage(4, Images.get("character/arm-left.png")),
                HangmanStage(5, Images.get("character/arm-right.png")),
                HangmanStage(6, Images.get("dead.png"))
        )
    }
}
