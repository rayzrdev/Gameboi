package dev.rayzr.gameboi.game.fight

import dev.rayzr.gameboi.game.Game
import dev.rayzr.gameboi.game.Match
import dev.rayzr.gameboi.game.MatchData
import dev.rayzr.gameboi.game.Player
import dev.rayzr.gameboi.render.RenderUtils
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageReaction
import java.awt.Graphics2D
import kotlin.math.ceil

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
                    name = "punch",
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
                    name = "kick",
                    emoji = "\ud83d\udc5e", // shoe
                    damage = 6..10,
                    attackChance = 0.60,
                    messages = listOf(
                            "was kicked in the gut by",
                            "was drop-kicked by",
                            "was kicked in the butt by"
                    )
            ),
            Attack(
                    name = "slam",
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
        draw(match)
    }

    private fun draw(match: Match) {
        val data = getData(match)

        val emojisToRender = if (data.winner == null) {
            attacks.map { it.emoji }
        } else {
            emptyList()
        }

        val messsage = if (data.winner == null) {
            var output = ":thinking: **${data.currentPlayer.player.user.name}**'s turn!"

            when (data.lastHitResult) {
                HitResult.HIT -> output = ":boom: **${data.currentPlayer.player.user.name}** ${data.lastAttack?.attack?.messages?.random()} **${data.otherPlayer.player.user.name}**! **${data.currentPlayer.player.user.name}** is now down to **${data.currentPlayer.health}/100** HP.\n\n$output"
                HitResult.MISS -> output = ":x: **${data.otherPlayer.player.user.name}** tried to ${data.lastAttack?.attack?.name} **${data.currentPlayer.player.user.name}**, but missed!\n\n$output"
                HitResult.NONE -> Unit
            }

            output
        } else {
            ":boom: **${data.currentPlayer.player.user.name}** ${data.lastAttack?.attack?.messages?.random()} **${data.otherPlayer.player.user.name}**!\n\n:tada: **${data.winner!!.player.user.name}** has won!"
        }

        render(match, emojisToRender, messsage) {
            graphics.run {
                scale(10.0, 10.0)

                // Background
                drawImage(Images.background, 0, 0, null)

                // Players
                drawPlayer(this, data.playerOne)
                drawPlayer(this, data.playerTwo)

                if (data.winner != null) {
                    this@render.renderText("${data.winner?.player?.user?.name} wins!", 20, 50, 35)
                } else if (data.lastHitResult != HitResult.NONE) {
                    val textImage = when (data.lastHitResult) {
                        HitResult.HIT -> Images.textHit
                        else -> Images.textMiss
                    }

                    drawImage(textImage, data.otherPlayer.offset.x, data.otherPlayer.offset.y - 13, null)
                }
            }
        }
    }

    private fun drawPlayer(graphics: Graphics2D, player: FightPlayer) {
        graphics.drawImage(player.skin, player.offset.x, player.offset.y, null)

        // HP Bar
        graphics.drawImage(Images.hpBorder, player.offset.x, player.offset.y - 6, null)
        graphics.drawImage(Images.hpBackground, player.offset.x, player.offset.y - 6, null)

        val hpWidth = ceil((player.health / 100.0) * Images.hpFiller.width).toInt()

        graphics.setClip(player.offset.x, 0, hpWidth, 800)
        graphics.drawImage(Images.hpFiller, player.offset.x, player.offset.y - 6, null)
        graphics.clip = null
    }

    private fun getData(match: Match): FightMatchData = match.data as FightMatchData

    override fun handleMessage(player: Player, match: Match, message: Message) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleReaction(player: Player, match: Match, reaction: MessageReaction) {
        val data = getData(match)

        if (player != data.currentPlayer.player) {
            reaction.removeReaction(player.user).queue()
            return
        }

        val attack = attacks.find { it.emoji == reaction.reactionEmote.name } ?: return

        data.lastAttack = Hit(data.currentPlayer, attack)

        if (Math.random() < attack.attackChance) {
            // Hit!
            data.lastHitResult = HitResult.HIT

            val damage = attack.damage.random()
            data.otherPlayer.health -= damage

            if (data.otherPlayer.health < 0) {
                data.otherPlayer.health = 0
                data.winner = data.currentPlayer
                match.end()
            }
        } else {
            // Miss!

            data.lastHitResult = HitResult.MISS
        }

        // Cycle
        data.currentPlayerIndex = (data.currentPlayerIndex + 1) % 2
        draw(match)
    }

    override fun createData(match: Match): MatchData = FightMatchData(match.players[0], match.players[1])

}