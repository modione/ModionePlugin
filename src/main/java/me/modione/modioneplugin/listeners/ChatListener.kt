package me.modione.modioneplugin.listeners

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChatEvent

class ChatListener : Listener {
    companion object {
        @JvmStatic
        val msg: MutableMap<Player, (String) -> Unit> = HashMap()
    }

    @EventHandler
    fun onPlayerChat(e: PlayerChatEvent) {
        val player = e.player
        if (!msg.containsKey(player)) {
            return
        }
        val message = e.message
        if (msg[player] != null) {
            msg[player]?.invoke(message)
        }
        msg.remove(player)
        e.isCancelled = true
    }
}