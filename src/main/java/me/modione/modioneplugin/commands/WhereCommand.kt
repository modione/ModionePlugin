package me.modione.modioneplugin.commands

import me.modione.modioneplugin.ModionePlugin
import me.modione.modioneplugin.utils.Utils
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class WhereCommand: TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val player: Player = Utils.getPlayer(sender)?: return false
        val component = TextComponent("§c${player.location.x.toInt()} ${player.location.y.toInt()} ${player.location.z.toInt()}")
        component.isUnderlined = true
        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, arrayOf(TextComponent("§aClick to copy Coordinates")))
        component.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "${player.location.x.toInt()} ${player.location.y.toInt()} ${player.location.z.toInt()}")
        Bukkit.getServer().spigot().broadcast(TextComponent("${ModionePlugin.PREFIX}§6${player.name}§a is at "), component)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        return mutableListOf("")
    }
}
