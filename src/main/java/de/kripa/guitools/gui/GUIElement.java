package de.kripa.guitools.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public interface GUIElement {
    boolean onClick(Player player, boolean isLeftClick, InventoryAction action);
    ItemStack getIcon();
}
