package me.modione.modioneplugin.utils

import me.modione.modioneplugin.ModionePlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class Command(
    command: String,
    private val usage: String,
    private val description: String,
    private val onRun: (Player) -> Boolean,
    private val subcommands: HashMap<String, (Player) -> Boolean>
) : TabExecutor {
    init {
        ModionePlugin.INSTANZ.getCommand(command)?.setExecutor(this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = sender as Player
        return if (args.isEmpty()) {
            onRun.invoke(player)
        } else {
            val subcommand = args[0]
            if (subcommands.containsKey(subcommand)) {
                subcommands[subcommand]?.invoke(player)!!
            } else {
                player.sendMessage("${ModionePlugin.INSTANZ}§cThis command does not exist!")
                false
            }
        }
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String>? {
        return if (subcommands.isNotEmpty()) {
            subcommands.keys.toMutableList()
        } else {
            null
        }
    }
}
