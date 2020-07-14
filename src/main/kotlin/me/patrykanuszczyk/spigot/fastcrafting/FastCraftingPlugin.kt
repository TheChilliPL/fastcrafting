package me.patrykanuszczyk.spigot.fastcrafting

import org.bstats.bukkit.Metrics
import org.bukkit.ChatColor
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

class FastCraftingPlugin : JavaPlugin() {
    override fun onEnable() {
        try {
            val metrics = initializeMetrics()
            if(metrics.isEnabled) logger.info("Metrics initialized an enabled.")
        } catch(t: Exception) {
            logger.warning("Couldn't initialize bStats.")
            logger.warning(t.toString())
        }

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

    fun initializeMetrics(): Metrics {
        return Metrics(this, metricsPluginId).apply {
            addCustomChart(Metrics.SimplePie("listener_inventory_opened") {
                if(config.getBoolean("listener.inventory_opened", false)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("listener_item_crafted") {
                if(config.getBoolean("listener.item_crafted", true)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("listener_player_joined") {
                if(config.getBoolean("listener.player_joined", false)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("listener_item_clicked") {
                if(config.getBoolean("listener.item_clicked", true)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("listener_item_pickup") {
                if(config.getBoolean("listener.item_pickup", true)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("use_nbt") {
                if(config.getBoolean("use_nbt", true)) "yes" else "no"
            })
            addCustomChart(Metrics.SimplePie("workbench_lore") {
                if((config.getString("messages.workbench_lore")?:"").isNotBlank()) "set" else "none"
            })
        }
    }

    companion object {
        const val metricsPluginId = 8192
    }
}