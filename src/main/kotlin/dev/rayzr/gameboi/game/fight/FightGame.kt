package dev.rayzr.gameboi.game.fight

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.Graphics2D

object Images {
    val background = RenderUtils.loadImage("fight/bg.png")!!
    val hpBackground = RenderUtils.loadImage("fight/hp-bg.png")!!
    val hpFiller = RenderUtils.loadImage("fight/hp-filler.png")!!
    val hpBorder = RenderUtils.loadImage("fight/hp-border.png")!!
    val textHit = RenderUtils.loadImage("fight/text-hit.png")!!
    val textMiss = RenderUtils.loadImage("fight/text-miss.png")!!
}

object FightGame : Game(800, 600, "Fight", 2) {
    val attacks = listOf(
            Attack(
                    name = "Punch",
                    emoji = "\ud83e\udd1c", // right fist
                    damage = 5..7,
                    attackChance = 0.75,
                    messages = listOf(
                            "was punched in the gut by",
                            "was punched in the face by",
                            "was punched in the stomach by"
                    )
            ),
            Attack(
                    name = "Kick",
                    emoji = "\ud83d\udc5e", // shoe
                    damage = 6..10,
                    attackChance = 0.60,
                    messages = listOf(
                            "was kicked in the gut by",
                            ":was drop-kicked by",
                            "was kicked in the butt by"
                    )
            ),
            Attack(
                    name = "Slam",
                    emoji = "\u270a", // open fist
                    damage = 9..20,
                    attackChance = 0.35,
                    messages = listOf(
                            "was slammed on the head with a ladder by",
                            "was slammed in the face with a hammer by",
                            "was slammed on the head with a metal door by",
                            "was slammed into the wall by"
                    )
            )
    )

    override fun begin(match: Match) {
        draw(match, getData(match))
        // Temporary for testing purposes
        match.end()
    }

    private fun draw(match: Match, data: FightMatchData) {
        render(match, attacks.map { it.emoji }) {
            graphics.run {
                scale(10.0, 10.0)

                // Background
                drawImage(Images.background, 0, 0, null)

                // Players
                drawPlayer(this, data.playerOne)
                drawPlayer(this, data.playerTwo)

                // TODO: Determine last hit & whether it was successful or not
                drawImage(Images.textHit, 12, 14, null)
            }
        }
    }

    private fun drawPlayer(graphics: Graphics2D, player: FightPlayer) {
        graphics.drawImage(player.skin, player.offset.x, player.offset.y, null)

        // HP Bar
        graphics.drawImage(Images.hpBorder, player.offset.x, player.offset.y - 6, null)
        graphics.drawImage(Images.hpBackground, player.offset.x, player.offset.y - 6, null)

        val hpWidth = ((player.health / 100.0) * Images.hpFiller.width).toInt()

        graphics.setClip(player.offset.x, 0, hpWidth, 800)
        graphics.drawImage(Images.hpFiller, player.offset.x, player.offset.y - 6, null)
        graphics.clip = null
    }

    private fun getData(match: Match): FightMatchData = match.data as FightMatchData

    override fun handleMessage(player: Player, match: Match, message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createData(match: Match): MatchData = FightMatchData(match.players[0], match.players[1])

}