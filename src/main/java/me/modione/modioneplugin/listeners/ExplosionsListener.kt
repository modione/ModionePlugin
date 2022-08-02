package me.modione.modioneplugin.listeners

import me.modione.modioneplugin.ModionePlugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.FallingBlock
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.util.Vector
import kotlin.random.Random


class ExplosionsListener : Listener {
    @EventHandler
    fun onExplosion(event: EntityExplodeEvent) {
        // Loop through all the blocks in the explosion and set their velocity to the explosion's velocity
        if (ModionePlugin.INSTANZ.config.getBoolean("advanced-explosions")) {
            for (block in event.blockList()) {
                bounceBlock(block, event.entity.location)
            }
        }
    }
    private fun bounceBlock(b: Block?, source: Location) {
        if (b == null) return
        val fb: FallingBlock = b.world.spawnFallingBlock(
            b.location,
            b.type, b.data
        )
        fb.dropItem = false
        b.type = Material.AIR
        val x = source.x - b.location.x + Random.nextDouble(0.0, 1.0)
        val y = source.y - b.location.y + Random.nextDouble(0.0, 1.0)
        val z = source.z - b.location.z + Random.nextDouble(0.0, 1.0)
        fb.velocity = Vector(x, y, z)
    }
}