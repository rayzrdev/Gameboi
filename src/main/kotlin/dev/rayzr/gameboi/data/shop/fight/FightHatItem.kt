package dev.rayzr.gameboi.data.shop.fight

import dev.rayzr.gameboi.data.shop.ItemSlot
import dev.rayzr.gameboi.data.shop.ShopItem
import java.awt.Image

val FIGHT_HAT_SLOT = ItemSlot("fight-hat", "Fight Hat")

open class FightHatItem(internalName: String, name: String, cost: Int, val image: Image)
    : ShopItem(internalName, name, cost, FIGHT_HAT_SLOT, 1)