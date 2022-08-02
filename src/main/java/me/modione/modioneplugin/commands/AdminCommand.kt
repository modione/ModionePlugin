package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.FileConfig
import me.modione.modioneplugin.utils.GUI
import me.modione.modioneplugin.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import java.util.*

class AdminCommand(private val config: FileConfig) : TabExecutor, Listener {
    private val admins: ArrayList<UUID> = ArrayList()
    private val commands: MutableMap<String, (Player) -> Unit> = HashMap()

    init {
        Bukkit.getPluginManager().registerEvents(this, ModionePlugin.INSTANZ)
        commands["login"] = {login(it)}
        commands["logout"] = {logout(it)}
        commands["commandstalk"] = {stalkCommands(it)}
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = Utils.getPlayer(sender) ?: return true
        if (args.isEmpty())
            openAdminInv(player)
        else {
            val cmd = args[0]
            if (commands.containsKey(cmd))
                commands[cmd]?.invoke(player)
            else
                player.sendMessage("${ModionePlugin.PREFIX}§cUsage: §7/§6admin §7<§6command§7>")
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String>? {
        return commands.keys.toMutableList()
    }

    private fun openAdminInv(player: Player) {
        val gui = GUI("§a§lAdmin Panel", 18)
        if (!isAdmin(player)) {
            gui.setItem(4, Material.BEDROCK, "Login", "§7Login to admin panel") { login(player) }
        } else {
            gui.setItem(0, Material.PAPER, "§fLogged in Admins", admins.joinToString("\n") { "§7-" + Bukkit.getPlayer(it)!!.name }) {}
            gui.setItem(4, Material.COMMAND_BLOCK, "§fCommandStalk", "§7Stalks commands executed by all players") { stalkCommands(player) }
            gui.setItem(5, Material.TNT, "§fAdvanced Explosions",
                description = "§7Explosions make blocks fly currently ${ if (config.getBoolean("advanced-explosions")) "§aenabled" else "§cdisabled"}"
            ) {
                if (config.getBoolean("advanced-explosions")) {
                    config.set("advanced-explosions", false)
                    player.sendMessage("${ModionePlugin.PREFIX}§cAdvanced Explosions disabled")
                } else {
                    config.set("advanced-explosions", true)
                    player.sendMessage("${ModionePlugin.PREFIX}§aAdvanced Explosions enabled")
                }
                config.saveconfig()
                opened.forEach { openAdminInv(it) }
            }
            gui.setItem(17, Material.BEDROCK, "§fLogout", "§7Logout from admin panel") { logout(player) }
        }
        gui.open(player)
        opened.add(player)
    }

    private fun stalkCommands(player: Player) {
        // Check if player is admin
        if (!isAdmin(player)) {
            player.sendMessage("${ModionePlugin.PREFIX}§cYou are not Admin")
            return
        }
        if (commandStalks.contains(player)) {
            player.sendMessage("${ModionePlugin.PREFIX}§cYou are no longer stalking commands")
            commandStalks.remove(player)
        } else {
            player.sendMessage("${ModionePlugin.PREFIX}§aYou are now stalking commands")
            commandStalks.add(player)
        }
    }

    private fun login(player: Player) {
        player.sendMessage("${ModionePlugin.PREFIX}§7Please enter your password")
        player.closeInventory()
        msg[player] = {
            if (it == config.getString("admin.password")) {
                player.sendMessage("${ModionePlugin.PREFIX}§aYou have logged in")
                admins.add(player.uniqueId)
                openAdminInv(player)
            } else {
                player.sendMessage("${ModionePlugin.PREFIX}§cWrong password")
                openAdminInv(player)
            }
        }
    }

    private fun logout(player: Player) {
        admins.remove(player.uniqueId)
        commandStalks.remove(player)
        player.sendMessage("${ModionePlugin.PREFIX}§aYou have logged out")
        if (opened.contains(player)) openAdminInv(player)
        opened.forEach { openAdminInv(it) }
    }

    private fun isAdmin(player: Player): Boolean {
        return admins.contains(player.uniqueId)
    }

    private val msg: MutableMap<Player, (String) -> Unit> = HashMap()
    @EventHandler
    fun onPlayerChat(e: PlayerChatEvent) {
        val player = e.player
        if (!msg.containsKey(player)) {
            return
        }
        val msg = e.message
        this.msg[player]?.invoke(msg)
        this.msg.remove(player)
        e.isCancelled = true
    }
    private val commandStalks: ArrayList<Player> = ArrayList()
    @EventHandler
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        for (player in commandStalks)
            player.sendMessage("${ModionePlugin.PREFIX}§6${e.player.name}§a is executing §c${e.message}")
    }
    private val opened: ArrayList<Player> = ArrayList()
    @EventHandler
    fun onInvClose(InventoryCloseEvent: org.bukkit.event.inventory.InventoryCloseEvent) {
        val player = InventoryCloseEvent.player as Player
        if (opened.contains(player))
            opened.remove(player)
    }
}
