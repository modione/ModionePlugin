package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.listeners.ChatListener
import me.modione.modioneplugin.utils.AdminList
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.Inventory

class AdminCommand : TabExecutor, Listener {
    private val commands: MutableMap<String, (Player) -> Unit> = HashMap()

    init {
        Bukkit.getPluginManager().registerEvents(this, ModionePlugin.INSTANZ)
        commands["login"] = { admins.add(it) }
        commands["logout"] = { admins.remove(it) }
        commands["commandstalk"] = { stalkCommands(it) }
    }

    companion object {
        @JvmStatic
        val opened: MutableSet<Player> = HashSet()
        val admins: AdminList = AdminList()
        val commandStalks: ArrayList<Player> = ArrayList()
        fun openAdminInv(player: Player) {
            val gui = GUI("§a§lAdmin Panel", 18)
            if (!admins.isAdmin(player)) {
                gui.setItem(4, Material.BEDROCK, "§fLogin", "§7Login to admin panel") { admins.add(player) }
            } else {

                gui.setItem(
                    0,
                    Material.PAPER,
                    "§fLogged in Admins",
                    admins.allAdmins()) {}

                gui.setItem(3, Material.CHEST, "§fInvsee", "§7See any Players inventory") {
                    player.closeInventory()
                    player.sendMessage("${ModionePlugin.PREFIX}§7Please specify the Player you want to invsee.")
                    ChatListener.msg[player] = {
                        val target = Bukkit.getPlayer(it)
                        if (target == null) {
                            player.sendMessage("§f${it} §cis not a valid Player.")
                        } else {
                            player.sendMessage("§aOpening §f${target.name}§a's inventory!")
                            player.openInventory(target.inventory)
                        }
                    }
                }

                gui.setItem(
                    4, Material.COMMAND_BLOCK, "§fCommandStalk",
                    "§7Stalks commands executed by all players \n§7currently ${if (commandStalks.contains(player)) "§aenabled" else "§cdisabled"}"
                )
                { stalkCommands(player); opened.forEach { openAdminInv(it); } }

                gui.setItem(
                    5, Material.TNT, "§fAdvanced Explosions",
                    description = "§7Explosions make blocks fly \n§7currently ${if (ModionePlugin.config.getBoolean("advanced-explosions")) "§aenabled" else "§cdisabled"}"
                ) {
                    if (ModionePlugin.config.getBoolean("advanced-explosions")) {
                        ModionePlugin.config.set("advanced-explosions", false)
                        player.sendMessage("${ModionePlugin.PREFIX}§cAdvanced Explosions disabled")
                    } else {
                        ModionePlugin.config.set("advanced-explosions", true)
                        player.sendMessage("${ModionePlugin.PREFIX}§aAdvanced Explosions enabled")
                    }
                    ModionePlugin.config.saveconfig()
                    opened.forEach { openAdminInv(it) }
                }

                gui.setItem(12, Material.BARRIER, "§fGamerules", "§7Change gamerules") {
                    player.closeInventory()
                    RulesCommand.rulesInventory(player, player.world)
                }

                gui.setItem(17, Material.BEDROCK, "§fLogout", "§7Logout from admin panel") { admins.remove(player) }

            }
            gui.open(player, true)
            opened.add(player)
        }
        private fun stalkCommands(player: Player) {
            // Check if player is admin
            if (!admins.isAdmin(player)) {
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

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return commands.keys.toMutableList()
    }

    @EventHandler
    fun onCommand(e: PlayerCommandPreprocessEvent) {
        for (player in commandStalks)
            player.sendMessage("${ModionePlugin.PREFIX}§6${e.player.name}§a is executing §c${e.message}")
    }

    @EventHandler
    fun onInvClose(InventoryCloseEvent: org.bukkit.event.inventory.InventoryCloseEvent) {
        val player = InventoryCloseEvent.player as Player
        if (opened.contains(player))
            opened.remove(player)
    }
}
