package dev.rayzr.gameboi.render

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.OutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.stream.ImageOutputStream
import javax.imageio.stream.MemoryCacheImageOutputStream

class RenderContext(width: Int, height: Int) {
    val image: BufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val graphics: Graphics2D
        get() = image.createGraphics()

    fun clear() {
        graphics.clearRect(0, 0, image.width, image.height)
    }

    fun renderText(text: String, x: Int, y: Int, size: Int = 20) {
        graphics.run {
            font = RenderUtils.font.deriveFont(size.toFloat())
            drawString(text, x, y)
        }
    }

    fun toJpeg(): ByteArray {
        val outputStream = ByteOutputStream()

        val writer = ImageIO.getImageWritersByFormatName("jpg").next()
        val params = writer.defaultWriteParam
        params.compressionMode = ImageWriteParam.MODE_EXPLICIT
        params.compressionQuality = 0.9f

        writer.output = MemoryCacheImageOutputStream(outputStream)
        writer.write(null, IIOImage(image, null, null), params)

        writer.dispose()
        outputStream.close()

        return outputStream.bytes!! // TODO: No '!!'?
    }
}