package me.patrykanuszczyk.spigot.fastcrafting

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.tags.ItemTagType

class FastCraftingListener(val plugin: FastCraftingPlugin) : Listener {
    private fun modifyWorkbenches(inventory: Iterable<ItemStack>) {
        inventory.filter { isValidWorkbench(it) }.forEach { modifyWorkbench(it) }
    }

    private fun modifyWorkbench(stack: ItemStack) {
        val meta = stack.itemMeta ?: return
        meta.customTagContainer.setCustomTag(plugin.workbenchTagKey, ItemTagType.INTEGER, 1)
        val lore = plugin.getMessage("workbench_lore")
        meta.lore = if(lore.isEmpty()) emptyList() else listOf(plugin.getMessage("workbench_lore"))
        stack.itemMeta = meta
    }

    private fun isValidWorkbench(stack: ItemStack?): Boolean {
        if(stack == null) return false
        if(stack.type != Material.CRAFTING_TABLE) return false
        val meta = stack.itemMeta
        if(meta?.hasLore() != true) return true
        val tagValue = meta.customTagContainer.getOrDefault(plugin.workbenchTagKey, ItemTagType.INTEGER, 0)
        return tagValue != 0
    }

    @EventHandler
    fun onOpenInventory(event: InventoryOpenEvent) {
        if(!plugin.config.getBoolean("listeners.inventory_opened", false)) return

        if (event.isCancelled) return

        modifyWorkbenches(setOf(event.view.topInventory, event.view.bottomInventory).flatten())
    }

    @EventHandler
    fun onCraftingItem(event: PrepareItemCraftEvent) {
        if(!plugin.config.getBoolean("listeners.item_crafted", true)) return

        if(isValidWorkbench(event.recipe?.result))
            event.inventory.result = event.recipe?.result.apply { modifyWorkbench(this!!) }
    }

    @EventHandler
    fun onPLayerJoin(event: PlayerJoinEvent) {
        if(!plugin.config.getBoolean("listeners.player_joined", false)) return

        modifyWorkbenches(event.player.inventory)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.isCancelled) return

        val modificationOn = plugin.config.getBoolean("listeners.item_clicked", true)
        if(modificationOn && isValidWorkbench(event.cursor)) modifyWorkbench(event.cursor!!)

        if(isValidWorkbench(event.currentItem)) {
            if(modificationOn) modifyWorkbench(event.currentItem!!)

            if (!event.isRightClick) return
            if (event.cursor?.amount ?: 0 > 0) return
            if(!setOf(
                            InventoryType.SlotType.CONTAINER,
                            InventoryType.SlotType.QUICKBAR
                    ).contains(event.slotType))
                return
            if(event.view.topInventory.type == InventoryType.WORKBENCH) return

            event.isCancelled = true

            Bukkit.getScheduler().runTask(plugin) { ->
                event.whoClicked.openWorkbench(null, true)
            }
        }
    }

    @EventHandler
    fun onItemPickup(event: EntityPickupItemEvent) {
        if(!plugin.config.getBoolean("listeners.item_pickup", true)) return

        if(event.entityType != EntityType.PLAYER) return

        if(isValidWorkbench(event.item.itemStack)) modifyWorkbench(event.item.itemStack)
    }
}