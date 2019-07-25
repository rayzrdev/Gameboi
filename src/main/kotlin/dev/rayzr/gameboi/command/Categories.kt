package dev.rayzr.gameboi.command

private var nextPriority = 0;

class Category(val name: String, val priority: Int = nextPriority++)

object Categories {
    val GAMES = Category("Games")
    val MATCH = Category("Match Commands")
    val SHOP = Category("Shop Commands")
    val SETTINGS = Category("Settings")
    val INFO = Category("Info Commands")
}