package me.patrykanuszczyk.spigot.fastcrafting

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class FastCraftingCommand(val plugin: FastCraftingPlugin) : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        fun noPermission(): Boolean {
            sender.sendMessage(plugin.getMessage("no_permission"))
            return true
        }

        if(args.isNotEmpty() && args[0].equals("reload", true)) {
            if(!sender.hasPermission("fastcrafting.commands.reload"))
                return noPermission()
            plugin.reloadConfig()
            sender.sendMessage(plugin.getMessage("config_reloaded"))
        } else {
            if(!sender.hasPermission("fastcrafting.commands.info"))
                return noPermission()
            sender.sendMessage(plugin.getMessage("info",
                    "version" to plugin.description.version
            ))
        }

        return true
    }

    override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<out String>
    ): List<String> {
        return if(args.size == 1) {
            val list = mutableListOf<String>()
            if(sender.hasPermission("fastcrafting.commands.info") && "info".startsWith(args[0], true))
                list.add("info")
            if(sender.hasPermission("fastcrafting.commands.reload") && "reload".startsWith(args[0], true))
                list.add("reload")
            list
        } else emptyList()
    }
}