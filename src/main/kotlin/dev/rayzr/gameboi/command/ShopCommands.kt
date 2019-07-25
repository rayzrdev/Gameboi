package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.data.shop.ShopRegistry
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object ShopCommand : Command("shop", "Lets you see what is available for purchase in the shop", category = Categories.SHOP) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val items = ShopRegistry.items.joinToString("\n\n") {
            "[__${it.slot.name}__] **${it.name}** (${it.cost} coins)"
        }

        event.channel.sendMessage(":information_source: **Available items:**\n\n$items").queue()
    }
}

object BuyCommand : Command("buy", "Lets you buy items from the shop", "buy <item>", Categories.SHOP) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            fail(event, "Please specify the item you would like to purchase!")
            return
        }

        val input = args.joinToString(" ").toLowerCase()

        val shopItem = ShopRegistry.find { it.name.toLowerCase() == input }

        if (shopItem == null) {
            fail(event, "That is not a valid item!")
            return
        }

        Player[event.author].editData {
            if (shopItem.maxQuantity > 0 && inventory.getOrDefault(shopItem, 0) >= shopItem.maxQuantity) {
                fail(event, "You cannot purchase any more of this item.")
                return@editData
            }

            if (coins < shopItem.cost) {
                fail(event, "You need **${shopItem.cost - coins}** more coins to purchase that.")
                return@editData
            }

            coins -= shopItem.cost

            inventory.compute(shopItem) { _, current -> (current ?: 0) + 1 }

            // Equip if nothing is purchased yet
            equipment.putIfAbsent(shopItem.slot.internalName, shopItem)

            event.channel.sendMessage(":white_check_mark: You have purchased **${shopItem.name}**x1.").queue()
        }
    }
}

object InventoryCommand : Command("inventory", "Shows you what items you currently have", category = Categories.SHOP) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        Player[event.author].getData().thenAccept {
            val items = when {
                it.inventory.isEmpty() -> "**You have no items.**"
                else -> "**Your items:**\n${
                it.inventory.map { item -> "- **${item.key.name}**x${item.value}" }.joinToString("\n")
                }"
            }

            event.channel.sendMessage(":moneybag: **Coins:** ${String.format("%,d", it.coins)}\n\n$items").queue()
        }
    }
}

object EquipCommand : Command("equip", "Lets you equip different items", "equip <slot> none|<item>", Categories.SHOP) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (args.isEmpty()) {
            val slots = ShopRegistry.slots.joinToString("\n") { "- ${it.internalName}" }
            event.channel.sendMessage("**Available slots:**\n$slots").queue()
            return
        }

        val slot = ShopRegistry.getSlot(args[0].toLowerCase()) ?: return fail(event, "That is not a valid slot!")

        Player[event.author].editData {
            if (args.size < 2) {
                val availableItems = inventory.keys.filter { it.slot == slot }

                if (availableItems.isEmpty()) {
                    return@editData fail(event, "You don't have any items available for this slot.")
                } else {
                    event.channel.sendMessage("**Available items:**\n${availableItems.joinToString("\n") { "- ${it.name}" }}").queue()
                    return@editData
                }
            }

            val itemName = args.subList(1, args.size).joinToString(" ").toLowerCase()
            if (itemName == "none") {
                equipment.remove(slot.internalName)
                event.channel.sendMessage(":white_check_mark: Removed all items from your **${slot.name}** slot.").queue()
                return@editData
            }

            val item = inventory.keys.find { it.name.toLowerCase() == itemName }
                    ?: return@editData fail(event, "That is not a valid item!")

            equipment[slot.internalName] = item
            event.channel.sendMessage(":white_check_mark: Equipped **${item.name}** to your **${slot.name}** slot.").queue()
        }
    }
}