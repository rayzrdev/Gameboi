package dev.rayzr.gameboi.data.shop


object ShopRegistry {
    private val registry: MutableMap<String, ShopItem> = mutableMapOf()
    val items get() = registry.values

    operator fun get(name: String): ShopItem? = registry[name]

    fun register(shopItem: ShopItem) = registry.put(shopItem.internalName, shopItem)

    fun find(predicate: (ShopItem) -> Boolean) = registry.values.find(predicate)
}