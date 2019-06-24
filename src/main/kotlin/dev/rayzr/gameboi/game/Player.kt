package dev.rayzr.gameboi.game

import net.dv8tion.jda.api.entities.User

class Player(val user: User, var currentMatch: Match? = null)