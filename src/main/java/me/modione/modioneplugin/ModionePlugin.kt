package me.modione.modioneplugin

import me.modione.modioneplugin.commands.*
import me.modione.modioneplugin.listeners.ExplosionsListener
import me.modione.modioneplugin.utils.FileConfig
import me.modione.modioneplugin.utils.Lag
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.plugin.java.JavaPlugin

class ModionePlugin : JavaPlugin() {

    init {
        INSTANZ = this
    }

    lateinit var config: FileConfig

    override fun onEnable() {
        // Register Commands
        saveDefaultConfig()
        config = FileConfig("config.yml")
        getCommand("gms")?.setExecutor(GameModesCommand(GameMode.SURVIVAL))
        getCommand("gmc")?.setExecutor(GameModesCommand(GameMode.CREATIVE))
        getCommand("gma")?.setExecutor(GameModesCommand(GameMode.ADVENTURE))
        getCommand("gmsp")?.setExecutor(GameModesCommand(GameMode.SPECTATOR))
        getCommand("enderchest")?.setExecutor(EnderChestCommand())
        getCommand("where")?.setExecutor(WhereCommand())
        getCommand("day")?.setExecutor(TimeCommand(1000))
        getCommand("admin")?.setExecutor(AdminCommand(config))
        getCommand("invsee")?.setExecutor(InvSeeCommand())
        Bukkit.getPluginManager().registerEvents(ExplosionsListener(), this)
        Bukkit.getServer().scheduler.scheduleSyncRepeatingTask(this, Lag(), 100L, 1L)
        // Show tps in tab
        server.scheduler.scheduleSyncRepeatingTask(this, {
            server.onlinePlayers.forEach {
                it.playerListHeader = "§7TPS: §e${Lag.tps}"
            }
        }, 0, 20)
    }

    override fun onDisable() {
        // Plugin shutdown logic
        config.saveconfig()
    }

    companion object {
        lateinit var INSTANZ: ModionePlugin
        val PREFIX =
            ChatColor.WHITE.toString() + "[" + ChatColor.GOLD + "Modione" + ChatColor.YELLOW + "Plugin" + ChatColor.WHITE + "]" + ChatColor.RESET + " "

        fun log(text: String) {
            INSTANZ.logger.info(PREFIX + text)
        }
    }
}
