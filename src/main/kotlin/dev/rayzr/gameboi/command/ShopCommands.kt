package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.data.shop.ShopRegistry
import dev.rayzr.gameboi.game.Player
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object ShopCommand : Command("shop", "Lets you see what is available for purchase in the shop") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        val items = ShopRegistry.items.joinToString("\n\n") {
            "**${it.name}** (${it.cost} coins)"
        }

        event.channel.sendMessage(":information_source: **Available items:**\n\n$items").queue()
    }
}

object BuyCommand : Command("buy", "Lets you buy items from the shop", "buy <item>") {
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
            equipment.putIfAbsent(shopItem.slot, shopItem)

            event.channel.sendMessage(":white_check_mark: You have purchased **${shopItem.name}**x1.").queue()
        }
    }
}

object InventoryCommand : Command("inventory", "Shows you what items you currently have") {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        Player[event.author].getData().thenAccept {
            val items = when {
                it.inventory.isEmpty() -> "**You have no items.**"
                else -> "**Your items:**\n\n${
                it.inventory.map { item -> "**${item.key.name}**x${item.value}" }.joinToString("\n")
                }"
            }

            event.channel.sendMessage(":moneybag: **Coins:** ${String.format("%,d", it.coins)}\n\n$items").queue()
        }
    }
}