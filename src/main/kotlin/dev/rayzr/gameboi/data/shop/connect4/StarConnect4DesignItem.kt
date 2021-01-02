package dev.rayzr.gameboi.data.shop.connect4

import dev.rayzr.gameboi.render.RenderUtils

object StarConnect4DesignItem : Connect4DesignItem(
    "connect4-design-star",
    "Star Design",
    150,
    RenderUtils.loadImage("connect4/designs/star.png")!!
)