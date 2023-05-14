package me.modione.modioneplugin.utils

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.commands.AdminCommand
import me.modione.modioneplugin.listeners.ChatListener
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class AdminList {
    private val admins = ArrayList<Player>()

    fun add(player: Player) {
        if (isAdmin(player)) {
            player.sendMessage("${ModionePlugin.PREFIX}§cYou are already logged in")
            return
        }
        player.sendMessage("${ModionePlugin.PREFIX}§7Please enter your password")
        player.closeInventory()
        ChatListener.msg[player] = {
            if (ModionePlugin.config.getString("admin.password").equals(it)) {
                player.sendMessage("${ModionePlugin.PREFIX}§aYou have logged in")
                admins.add(player)
                player.addAttachment(ModionePlugin.INSTANZ, "modione.ec", true)
                AdminCommand.openAdminInv(player)
            } else {
                player.sendMessage("${ModionePlugin.PREFIX}§cWrong password")
            }
        }
    }

    fun remove(player: Player) {
        player.addAttachment(ModionePlugin.INSTANZ, "modione.ec", false)
        if (isAdmin(player)) {
            player.sendMessage("${ModionePlugin.PREFIX}§aYou have logged out")
            if (AdminCommand.opened.contains(player)) player.closeInventory()
            admins.remove(player)
        }else {
            player.sendMessage("${ModionePlugin.PREFIX}§cYou are not logged in")
        }
    }

    fun isAdmin(player: Player): Boolean {
        return admins.contains(player)
    }
    fun allAdmins(): String {
        return admins.joinToString("\n") { "§7-" + Bukkit.getPlayer(it.uniqueId)!!.name }
    }


}