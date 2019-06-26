package dev.rayzr.gameboi.game.fight

import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import java.awt.Point
import java.awt.image.BufferedImage

val defaultLeftSkin = RenderUtils.loadImage("fight/player-left.png")!!
val defaultRightSkin = RenderUtils.loadImage("fight/player-right.png")!!

val leftOffset = Point(12, 27)
val rightOffset = Point(48, 27)

class FightMatchData(playerOne: Player, playerTwo: Player) : MatchData {
    val playerOne = FightPlayer(playerOne, defaultLeftSkin, leftOffset)
    val playerTwo = FightPlayer(playerTwo, defaultRightSkin, rightOffset)
    var currentPlayerIndex = 0
    var winner: FightPlayer? = null
    var lastAttack: Hit? = null
    var lastHitResult: HitResult = HitResult.NONE

    val currentPlayer: FightPlayer
        get() = when (currentPlayerIndex) {
            0 -> playerOne
            else -> playerTwo
        }

    val otherPlayer: FightPlayer
        get() = when (currentPlayerIndex) {
            0 -> playerTwo
            else -> playerOne
        }
}

class FightPlayer(val player: Player, val skin: BufferedImage, val offset: Point, var health: Int = 100)

class Hit(val player: FightPlayer, val attack: Attack)

enum class HitResult {
    NONE,
    HIT,
    MISS
}