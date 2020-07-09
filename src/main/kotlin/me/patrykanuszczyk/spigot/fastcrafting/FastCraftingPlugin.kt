package me.patrykanuszczyk.spigot.fastcrafting

import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class FastCraftingPlugin : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(FastCraftingListener(this), this)
        FastCraftingCommand(this).also {
            getCommand("fastcrafting")?.apply {
                setExecutor(it)
                tabCompleter = it
            } ?: throw NullPointerException("Didn't find the /fastcrafting command.")
        }
        saveDefaultConfig()
    }

    override fun onDisable() {}

    val workbenchTagKey = NamespacedKey(this, "workbench-lore")

    fun getMessage(path: String, vararg placeholders: Pair<String, String>): String
            = getMessage(path, placeholders.toMap())

    fun getMessage(path: String, map: Map<String, String>? = null): String {
        var message = config.getString("messages.$path") ?: return ""
        message = ChatColor.translateAlternateColorCodes('&', message)
        map?.forEach { (key, value) ->
            message = message.replace("{$key}", value, true)
        }
        return message
    }
}