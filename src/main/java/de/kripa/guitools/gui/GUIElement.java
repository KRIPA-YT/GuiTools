package de.kripa.guitools.gui;

import org.bukkit.inventory.ItemStack;

public interface GUIElement {
    boolean onClick(GUIElementClickEvent e);
    ItemStack getIcon();
}
