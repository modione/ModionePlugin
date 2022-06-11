package me.modione.modioneplugin.utils

import me.modione.modioneplugin.ModionePlugin
import org.bukkit.ChatColor
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.IOException

class FileConfig(folder: String, filename: String) : YamlConfiguration() {
    private val path: String

    init {
        this.path = "plugins/$folder/$filename"
        try {
            load(this.path)
        } catch (ex: InvalidConfigurationException) {
            ex.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    constructor(filename: String) : this("ModionePlugin3.0", filename) {}

    fun saveconfig() {
        try {
            save(this.path)
        } catch (ex: IOException) {
            ModionePlugin.log("${ChatColor.RED}Error saving config file: $path")
        }
    }
}
