package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.Utils
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GameModesCommand(private val mode: GameMode) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = Utils.getPlayer(sender) ?: return false
        val message = "${ModionePlugin.PREFIX}${ChatColor.AQUA}You are now in ${ChatColor.RED}${mode.name.toLowerCase()}${ChatColor.AQUA} mode!"

        if (args.isEmpty()) {
            if (player.gameMode==mode) player.sendMessage("${ModionePlugin.PREFIX}${ChatColor.RED}You are already in ${mode.name.toLowerCase()} mode!")
            else {
                player.gameMode = mode
                player.sendMessage(message)
            }
        }else {
            val target: Player = Utils.getPlayer(args[0], player)?: return false
            if (target.gameMode==mode) player.sendMessage("${ModionePlugin.PREFIX}${ChatColor.RED}${target.name} is already in ${mode.name.toLowerCase()} mode!")
            else {
                target.gameMode = mode
                target.sendMessage(message)
                player.sendMessage("${ModionePlugin.PREFIX}${ChatColor.AQUA}You set ${ChatColor.GOLD}${target.name}${ChatColor.AQUA} to ${ChatColor.RED}${mode.name.toLowerCase()}${ChatColor.AQUA} mode!")
            }
        }
        return true
    }
}
