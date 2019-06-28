package dev.rayzr.gameboi.data.shop

import dev.rayzr.gameboi.data.shop.fight.RibbitHatItem
import dev.rayzr.gameboi.data.shop.fight.TophatHatItem

fun initShopItems() {
    listOf(
            TophatHatItem,
            RibbitHatItem
    ).forEach {
        ShopRegistry.register(it)
    }
}