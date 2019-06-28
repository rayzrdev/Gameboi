package dev.rayzr.gameboi.game

import dev.rayzr.gameboi.data.DataManager
import dev.rayzr.gameboi.data.PlayerData
import dev.rayzr.gameboi.manager.InviteManager
import net.dv8tion.jda.api.entities.User
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.scheduleAtFixedRate

private val CACHE_CLEAR_DELAY = TimeUnit.MINUTES.toMillis(30)

class Player private constructor(val user: User, var currentMatch: Match? = null) {
    companion object {
        init {
            Timer().scheduleAtFixedRate(CACHE_CLEAR_DELAY, CACHE_CLEAR_DELAY) {
                players.filter {
                    it.value.currentMatch == null && InviteManager.currentInvites.none { invite ->
                        invite.to == it.value || invite.from == it.value
                    }
                }
            }
        }

        // Cache
        private val players: MutableMap<Long, Player> = mutableMapOf()

        operator fun get(user: User) = players.computeIfAbsent(user.idLong) { Player(user) }
    }

    fun getData() = DataManager.getPlayerData(this)
    fun editData(action: PlayerData.() -> Unit) = DataManager.editPlayerData(this, action)

    override fun hashCode(): Int {
        return user.idLong.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (user.id != other.user.id) return false

        return true
    }
}