package me.modione.modioneplugin.commands

import me.modione.modioneplugin.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class InvSeeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = Utils.getPlayer(sender) ?: return false
        if (args.isEmpty()) {
            player.sendMessage("§cYou need to specify a Player.")
            return true
        } else {
            val target = Bukkit.getPlayer(args[0])
            if (target == null) {
                player.sendMessage("§f${this} §cis not a valid Player.")
            } else {
                player.sendMessage("§aOpening ${target.name}'s inventory!")
                player.openInventory(target.inventory)
            }
            return true
        }
    }
}