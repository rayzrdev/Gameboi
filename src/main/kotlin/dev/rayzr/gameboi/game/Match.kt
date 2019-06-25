package dev.rayzr.gameboi.game

import net.dv8tion.jda.api.entities.MessageChannel

class Match(val players: MutableList<Player>, val game: Game, val channel: MessageChannel) {
    val renderContext = game.createRenderContext(this)

    fun canJoin(player: Player) = !players.contains(player) && players.size < game.maxPlayers

    fun addPlayer(player: Player) {
        if (!canJoin(player)) {
            return
        }

        players.add(player)

        if (players.size >= game.maxPlayers) {
            game.begin(this)
        }
    }
}