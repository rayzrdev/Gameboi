package dev.rayzr.gameboi.data.shop


object ShopRegistry {
    private val registry: MutableMap<String, ShopItem> = mutableMapOf()
    private val slotRegistry: MutableMap<String, ItemSlot> = mutableMapOf()

    val items get() = registry.values
    val slots get() = slotRegistry.values

    operator fun get(name: String): ShopItem? = registry[name]

    fun getSlot(name: String): ItemSlot? = slotRegistry[name]

    fun register(shopItem: ShopItem) {
        registry[shopItem.internalName] = shopItem
        slotRegistry.putIfAbsent(shopItem.slot.internalName, shopItem.slot)
    }

    fun find(predicate: (ShopItem) -> Boolean) = registry.values.find(predicate)
}