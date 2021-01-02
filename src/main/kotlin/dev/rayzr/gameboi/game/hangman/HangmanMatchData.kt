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

class HangmanStage(val image: Image) {
    companion object {
        val stages = listOf(
            HangmanStage(Images.get("character/head.png")),
            HangmanStage(Images.get("character/body.png")),
            HangmanStage(Images.get("character/leg-left.png")),
            HangmanStage(Images.get("character/leg-right.png")),
            HangmanStage(Images.get("character/arm-left.png")),
            HangmanStage(Images.get("character/arm-right.png")),
            HangmanStage(Images.get("dead.png"))
        )
    }
}
