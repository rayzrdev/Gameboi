package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.render.RenderContext

class Match(val players: List<Player>, val game: Game) {
    val renderContext = game.createRenderContext()
}