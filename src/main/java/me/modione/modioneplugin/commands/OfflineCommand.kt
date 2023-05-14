package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.GUI
import me.modione.modioneplugin.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.event.server.PluginEnableEvent
import java.util.*

class OfflineCommand: CommandExecutor, Listener {
    companion object {
        val offline = HashMap<UUID, Long>()
    }
    override fun onCommand(sender: CommandSender, command: org.bukkit.command.Command, label: String, args: Array<out String>): Boolean {
        val player = Utils.getPlayer(sender) ?: return true
        if (args.isEmpty()) {
            player.sendMessage()
            player.sendMessage(ModionePlugin.PREFIX+" Showing offline players...")
            val gui = GUI("Offline Players", 54)
            offline.forEach {
                val milliseconds = System.currentTimeMillis() - it.value
                val player1 = Bukkit.getOfflinePlayer(it.key)
                val item = Utils.getHead(player1)
                val meta = item.itemMeta
                meta!!.setDisplayName("§a${player1.name}")
                meta.lore = listOf("§7is offline for §a${LongToTime(milliseconds)}")
                item.setItemMeta(meta)
                gui.addItem(item) {}
            }
            gui.open(player, false)
        }else {
            val target = Bukkit.getOfflinePlayer(args[0])
            if (offline.containsKey(target.uniqueId)) {
                val milliseconds = System.currentTimeMillis() - offline[target.uniqueId]!!
                player.sendMessage("§a${target.name} §7is offline for §a${LongToTime(milliseconds)}")
            }
        }
        return true
    }
    fun LongToTime(milliseconds: Long): String {
        val minutes = (milliseconds / (1000 * 60) % 60).toInt()
        val hours = (milliseconds / (1000 * 60 * 60) % 24).toInt()
        val days = (milliseconds / (1000 * 60 * 60 * 24) % 24).toInt()
        return "$days days, $hours hours and $minutes minutes"
    }
    @EventHandler
    fun onLeave(e: PlayerQuitEvent) {
        val player = e.player
        offline[player.uniqueId] = System.currentTimeMillis()
    }
    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val player = e.player
        offline.remove(player.uniqueId)
    }
    @EventHandler
    fun onDisable(e: PluginDisableEvent) {
        offline.forEach {
            ModionePlugin.config.set("offline.${it.key}", it.value)
        }
        ModionePlugin.config.saveconfig()
    }
    @EventHandler
    fun onEnable(e: PluginEnableEvent) {
        if (ModionePlugin.config.contains("offline")) {
            ModionePlugin.config.getConfigurationSection("offline")?.getKeys(false)?.forEach {
                offline[UUID.fromString(it)] = ModionePlugin.config.getLong("offline.$it")
            }
        }
    }
}