package dev.rayzr.gameboi.render

import dev.rayzr.gameboi.Gameboi
import dev.rayzr.gameboi.game.Match
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

data class RenderOptions(
    val embed: MessageEmbed,
    val messageContents: String?,
    val reactions: List<String>
)

class RenderContext(val match: Match, private val width: Int, height: Int) {
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics2D
        get() = image.createGraphics()

    var lastRenderOptions: RenderOptions? = null
    var lastMessage: Message? = null
    var lastRender: ByteArray? = null

    fun clear() {
        graphics.clearRect(0, 0, image.width, image.height)
    }

    /*fun renderText(text: String, x: Int, y: Int, size: Int = 35) {
        graphics.run {
            font = RenderUtils.font.deriveFont(size.toFloat())
            drawString(text, x, y)
        }
    }*/

    fun renderCenteredText(text: String, x: Int = width / 2, y: Int = 65, size: Int = 40, outlineWidth: Int = 2) {
        graphics.run {
            font = RenderUtils.font.deriveFont(size.toFloat())

            val bounds = font.getStringBounds(text, fontRenderContext)

            val realX = x - (bounds.width / 2).toInt()
            val realY = y - (bounds.height / 2).toInt()

            // Outline effect
            if (outlineWidth > 0) {
                color = Color.black
                drawString(text, realX - outlineWidth, realY - outlineWidth)
                drawString(text, realX - outlineWidth, realY + outlineWidth)
                drawString(text, realX + outlineWidth, realY + outlineWidth)
                drawString(text, realX + outlineWidth, realY - outlineWidth)
            }

            // Solid middle
            color = Color.white
            drawString(text, realX, realY)
        }
    }

    fun draw(embedDescription: String? = null, messageContents: String? = null, reactions: List<String> = emptyList()) {
        // this does nothing other than just defeat caching
        val state = UUID.randomUUID()
        val imageUrl = "${Gameboi.host}/matches/${match.id}/render?state=${state}"
        val embed = EmbedBuilder()
            .setImage(imageUrl)
            .setFooter("${match.game.name} || Players: ${match.players.joinToString(", ") { it.user.name }}")
            .setColor(0x353940)
            .apply { if (embedDescription != null) setDescription(embedDescription) }
            .build()

        val renderOptions = RenderOptions(embed, messageContents, reactions)
        lastRender = toBytes()
        render(renderOptions)
    }

    fun render(renderOptions: RenderOptions) {
        lastRenderOptions = renderOptions
        val future = lastMessage?.editMessage(renderOptions.embed)?.content(renderOptions.messageContents)?.submit()
            ?: match.channel.sendMessage(renderOptions.embed).content(renderOptions.messageContents).submit()

        future.thenAccept { newMessage ->
            lastMessage = newMessage
            renderOptions.reactions.forEach { reaction ->
                newMessage.addReaction(reaction).queue()
            }
        }

        match.bumpTimeout()
    }

    fun bump() {
        val renderOptions = lastRenderOptions
            ?: throw IllegalStateException("You cannot call bump before having rendered at least once.")

        lastMessage?.delete()?.queue()
        lastMessage = null
        render(renderOptions)
    }

    private fun toBytes(): ByteArray {
        val outputStream = ByteArrayOutputStream()

        ImageIO.write(image, "png", outputStream)

        return outputStream.toByteArray()
    }
}