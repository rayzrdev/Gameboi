package dev.rayzr.gameboi.render

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

class RenderContext(width: Int, height: Int) {
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics2D
        get() {
            return image.createGraphics()
        }

    fun clear() {
        graphics.clearRect(0, 0, image.width, image.height)
    }

    fun toJpeg(): ByteArray {
        val outputStream = ByteOutputStream()
        ImageIO.write(image, "jpg", outputStream)
        return outputStream.bytes!! // TODO: No '!!'?
    }
}