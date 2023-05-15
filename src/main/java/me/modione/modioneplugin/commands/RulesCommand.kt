package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.listeners.ChatListener
import me.modione.modioneplugin.utils.GUI
import me.modione.modioneplugin.utils.ItemBuilder
import me.modione.modioneplugin.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RulesCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player = Utils.getPlayer(sender) ?: return true
        if (args.isEmpty()) {
            rulesInventory(player, player.world)
        } else {
            val world = Bukkit.getWorld(args[0])
            if (world == null) {
                player.sendMessage("${ModionePlugin.PREFIX}§cWorld not found")
                return true
            }
            rulesInventory(player, world)
        }
        return true
    }

    companion object {
        fun rulesInventory(player: Player, world: World) {
            val gui = GUI("§fGame rules", 54)
            world.gameRules.forEach { ruleName ->
                val rule = GameRule.getByName(ruleName)
                val value = world.getGameRuleValue(rule as GameRule<*>)
                var description = ""
                description = if (value is Boolean) {
                    "§7currently ${if (value == true) "§aenabled" else "§cdisabled"}"
                }else {
                    "§7Value: §l$value"
                }
                gui.addItem(
                    ItemBuilder(Material.COMMAND_BLOCK)
                            .displayname("§f$ruleName")
                            .lore(description)
                            .build()!!
                    ) {
                        if (value is Boolean) {
                            world.setGameRuleValue(ruleName, (!value).toString())
                            rulesInventory(player, world)
                        }
                        else {
                            player.sendMessage("${ModionePlugin.PREFIX}§7Enter new value for §f$ruleName")
                            player.closeInventory()
                            ChatListener.msg[player] = {
                                if (it.toIntOrNull() == null) player.sendMessage("${ModionePlugin.PREFIX}§cInvalid value")
                                else {
                                    world.setGameRuleValue(ruleName, it)
                                    player.sendMessage("${ModionePlugin.PREFIX}§7Set §f$ruleName§7 to §l$it")
                                }
                                rulesInventory(player, world)
                            }
                        }
                    }
            }
            gui.open(player, false)
        }
    }

}