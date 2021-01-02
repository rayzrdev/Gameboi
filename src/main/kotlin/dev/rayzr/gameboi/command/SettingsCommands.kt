package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.data.settings.GuildSettingsManager
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

object SetPrefixCommand :
    Command("setprefix", "Allows you to change Gameboi's prefix", "setprefix [new prefix]", Categories.SETTINGS) {
    override fun handle(event: GuildMessageReceivedEvent, args: List<String>) {
        if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
            return fail(event, "Only admins (those with the **Manage Server** permission) can edit this!")
        }

        GuildSettingsManager.editGuildSettings(event.guild) {
            if (args.isEmpty() || args[0].toLowerCase() == "none") {
                prefix = null
                event.channel.sendMessage(":white_check_mark: Removed custom prefix, you are now using the default `$realPrefix` prefix.")
                    .queue()
            } else {
                prefix = args[0]
                event.channel.sendMessage(":white_check_mark: Updated prefix to `$prefix`!").queue()
            }
        }
    }
}