package dev.rayzr.gameboi.data.shop.connect4

import dev.rayzr.gameboi.data.shop.ItemSlot
import dev.rayzr.gameboi.data.shop.ShopItem
import java.awt.Image

val CONNECT_4_DESIGN_SLOT = ItemSlot("connect4-design", "Connect 4 Design")

open class Connect4DesignItem(internalName: String, name: String, cost: Int, val image: Image) :
    ShopItem(internalName, name, cost, CONNECT_4_DESIGN_SLOT, 1)