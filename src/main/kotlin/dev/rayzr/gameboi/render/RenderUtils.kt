package dev.rayzr.gameboi.render

import java.awt.Font
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object RenderUtils {
    val font: Font = Font.createFont(Font.TRUETYPE_FONT, javaClass.getResourceAsStream("/font/kongtext.ttf"))

    fun loadImage(path: String): BufferedImage? = ImageIO.read(javaClass.getResourceAsStream("/image/$path"))
}