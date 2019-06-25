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
    val playerOne = FightPlayer(playerOne, defaultLeftSkin, leftOffset, 80)
    val playerTwo = FightPlayer(playerTwo, defaultRightSkin, rightOffset, 35)
    var currentPlayerIndex = 0
    var winner: Player? = null

    val currentPlayer: FightPlayer
        get() = when (currentPlayerIndex) {
            0 -> playerOne
            else -> playerTwo
        }

}

class FightPlayer(val player: Player, val skin: BufferedImage, val offset: Point, var health: Int = 100)