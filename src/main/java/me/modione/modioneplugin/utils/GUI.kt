package me.modione.modioneplugin.utils

import me.modione.modioneplugin.ModionePlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class GUI(name: String, size: Int) : Listener {
    private val inv = Bukkit.createInventory(null, size, name)
    private val items = mutableMapOf<Int, (InventoryClickEvent) -> Unit>()

    fun setItem(slot: Int, type: Material, name: String, description: String, onClick: (InventoryClickEvent) -> Unit) {
        val item = ItemBuilder(type)
            .displayname(name)
            .lore(description.split("\n").toMutableList())
            .build()
        inv.setItem(slot, item)
        items[slot] = onClick
    }

    fun setItem(slot: Int, item: ItemStack, onClick: Unit) {
        inv.setItem(slot, item)
    }

    fun open(player: org.bukkit.entity.Player) {
        Utils.fillInv(inv)
        player.openInventory(inv)
    }

    init {
        if (size > 54) {
            throw IllegalArgumentException("Size must be less than 54")
        } else if (size < 9) {
            throw IllegalArgumentException("Size must be greater than 9")
        } else if (size % 9 != 0) {
            throw IllegalArgumentException("Size must be divisible by 9")
        }
        Bukkit.getPluginManager().registerEvents(this, ModionePlugin.INSTANZ)
    }

    @EventHandler
    fun onClick(e: InventoryClickEvent) {
        if (e.inventory == inv) {
            e.isCancelled = true
            if (e.currentItem != null)
                items[e.slot]?.invoke(e)
        }
    }
}
