package me.modione.modioneplugin.utils

import me.modione.modioneplugin.ModionePlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class Utils {
    companion object {
        fun getPlayer(sender: CommandSender): Player? {
            return if (sender is Player) {
                sender
            } else {
                sender.sendMessage("${ModionePlugin.PREFIX}§cYou must be a player to use this command!")
                null
            }
        }

        fun getPlayer(sender: CommandSender, permission: String): Player? {
            val player = getPlayer(sender)
            return if (player != null) {
                if (player.hasPermission(permission)) {
                    player
                } else {
                    sender.sendMessage("${ModionePlugin.PREFIX}§cYou don't have permission to use this command!")
                    null
                }
            } else null
        }

        fun getPlayer(target: String, player: Player): Player? {
            val re: Player? = Bukkit.getPlayer(target)
            return if (re == null) {
                player.sendMessage("${ModionePlugin.PREFIX}§cPlayer not found!")
                null
            } else re
        }

        fun fillInv(inv: Inventory) {
            // Loop through all slots in the inventory and if it is air set it to a glass pane
            for (i in 0 until inv.size) {
                if (inv.getItem(i) == null || inv.getItem(i)!!.type == Material.AIR) {
                    inv.setItem(i, ItemStack(Material.BLACK_STAINED_GLASS, 1, 15))
                }
            }
        }
    }
}
