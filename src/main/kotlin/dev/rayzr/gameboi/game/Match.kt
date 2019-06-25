package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.manager.MatchManager
import net.dv8tion.jda.api.entities.MessageChannel

class Match(val game: Game, val channel: MessageChannel) {
    val players = mutableListOf<Player>()
    val renderContext = game.createRenderContext(this)
    val data = game.createData(this)

    fun canJoin(player: Player) = player.currentMatch == null && !players.contains(player) && players.size < game.maxPlayers

    fun addPlayer(player: Player) {
        if (!canJoin(player)) {
            return
        }

        players.add(player)
        MatchManager[player.user] = this
//        player.currentMatch = this

        if (players.size >= game.maxPlayers) {
            game.begin(this)
        }
    }

    fun end() {
        players.forEach {
            MatchManager.remove(it.user)
            it.currentMatch = null
        }
    }
}