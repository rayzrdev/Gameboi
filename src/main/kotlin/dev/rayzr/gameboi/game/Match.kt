package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.entities.TextChannel
import java.util.*
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

val MATCH_TIMEOUT = TimeUnit.MINUTES.toMillis(15)

class Match(val game: Game, val channel: TextChannel) {
    private var scheduledFuture: ScheduledFuture<*>? = null

    val id = UUID.randomUUID()

    val players = mutableListOf<Player>()
    val renderContext = game.createRenderContext(this)
    var data: MatchData? = null
    private var isEnded = false

    private fun canJoin(player: Player) =
        player.currentMatch == null && !players.contains(player) && players.size < game.maxPlayers

    fun addPlayer(player: Player) {
        if (!canJoin(player)) {
            return
        }

        players.add(player)
        MatchManager[player.user] = this
        player.currentMatch = this

        if (players.size >= game.maxPlayers) {
            begin()
        }
    }

    private fun begin() {
        data = game.createData(this)
        game.begin(this)
        players.forEach { player ->
            player.editData {
                updateStatBy(player.user, channel.guild, "games-played.${game.name}", 1)
                updateStatBy(player.user, channel.guild, "games-played.total", 1)
            }
        }

        // Start timer
        bumpTimeout()
    }

    fun end() {
        isEnded = true

        players.forEach {
            MatchManager.remove(it.user)
            it.currentMatch = null
        }

        scheduledFuture?.cancel(true)
    }

    fun bumpTimeout() {
        scheduledFuture?.cancel(true)

        if (isEnded) {
            return
        }

        scheduledFuture =
            channel.sendMessage(":x: Your **${game.name}** match has timed out, ${players.joinToString(" ") { it.user.asMention }}!")
                .queueAfter(MATCH_TIMEOUT, TimeUnit.MILLISECONDS) {
                    end()
                }
    }
}