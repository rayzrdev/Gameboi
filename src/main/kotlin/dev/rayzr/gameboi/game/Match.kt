package dev.rayzr.gameboi.game

class Match(val players: MutableList<Player>, val game: Game) {
    val renderContext = game.createRenderContext()

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