package dev.rayzr.gameboi.data.shop.connect4

import dev.rayzr.gameboi.render.RenderUtils

object CircleConnect4DesignItem : Connect4DesignItem(
    "connect4-design-circle",
    "Circle Design",
    125,
    RenderUtils.loadImage("connect4/designs/circle.png")!!
)