package me.patrykanuszczyk.spigot.fastcrafting

import org.bukkit.NamespacedKey
import org.bukkit.inventory.meta.tags.CustomItemTagContainer
import org.bukkit.inventory.meta.tags.ItemTagType

/**
 * Returns the custom tag's value that is stored on the item.
 * @param key the key to look up in the custom tag map
 * @param type the type the value must have and will be casted into
 * @param default the default value that the function will return if the key is not found
 * @param R the generic type of the eventually created complex object
 * @throws IllegalArgumentException if the value exists under the given key, but cannot be accessed using the given type
 * @throws IllegalArgumentException if no suitable adapter will be found for the [ItemTagType.getPrimitiveType]
 */
fun <R> CustomItemTagContainer.getOrDefault(key: NamespacedKey, type: ItemTagType<*, R>, default: R): R {
    return this.getCustomTag(key, type) ?: default
}

