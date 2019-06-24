package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.render.RenderContext

class Game(private val width: Int, private val height: Int, val name: String) {
    fun createRenderContext(): RenderContext = RenderContext(width, height)
}
