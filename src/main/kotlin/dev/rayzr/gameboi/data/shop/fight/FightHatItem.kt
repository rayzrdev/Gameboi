package dev.rayzr.gameboi.data.shop.fight

import dev.rayzr.gameboi.data.shop.ShopItem
import java.awt.Image

open class FightHatItem(internalName: String, name: String, cost: Int, val image: Image)
    : ShopItem(internalName, name, cost, "fight-hat", 1)