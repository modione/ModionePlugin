package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TimeCommand(private val time:Long) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = Utils.getPlayer(sender)?:return false
        player.world.time = time
        player.sendMessage("${ModionePlugin.PREFIX}§aTime set to §c$time")
        return true
    }
}
