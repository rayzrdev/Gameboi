package dev.rayzr.gameboi.data.shop

import dev.rayzr.gameboi.data.shop.connect4.CircleConnect4DesignItem
import dev.rayzr.gameboi.data.shop.connect4.StarConnect4DesignItem
import dev.rayzr.gameboi.data.shop.fight.RibbitHatItem
import dev.rayzr.gameboi.data.shop.fight.TophatHatItem

fun initShopItems() {
    listOf(
            // Hats
            RibbitHatItem,
            TophatHatItem,
            // Connect 4 designs
            CircleConnect4DesignItem,
            StarConnect4DesignItem
    ).forEach {
        ShopRegistry.register(it)
    }
}