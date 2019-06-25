package dev.rayzr.gameboi.command

import dev.rayzr.gameboi.render.RenderContext
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.awt.Color

object RenderTestCommand : Command("rendertest", "Tests the RenderContext system", "rendertest [message]") {
    override fun handle(event: MessageReceivedEvent, args: List<String>) {
        val message = args.joinToString(" ").ifEmpty { "Test" }

        val context = RenderContext(event.channel, 500, 300)

        context.graphics.run {
            color = Color.RED
            fillRect(10, 10, 480, 30)
            color = Color.BLUE
            fillOval(50, 50, 100, 100)
            color = Color.WHITE
        }

        context.renderText(message, 20, 30)

        context.draw {
            context.clear()
            context.renderText(message, 50, 50, 50)
            context.draw()
        }
    }
}