package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.Utils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class EnderChestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = Utils.getPlayer(sender) ?: return false

        if (args.isEmpty()) {
            player.openInventory(player.enderChest)
            player.sendMessage("${ModionePlugin.PREFIX} §aEnderchest opened!")
            return true
        } else if (player.hasPermission("modione.ec")) {
            val targetPlayer = Utils.getPlayer(args[0], player) ?: return false
            player.openInventory(targetPlayer.enderChest)
            player.sendMessage("${ModionePlugin.PREFIX} §aOpened §6${targetPlayer.name}§a's enderchest!")
        }
        return true
    }
}
