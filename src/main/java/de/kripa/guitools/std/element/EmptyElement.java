package de.kripa.guitools.std.element;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class EmptyElement implements GUIElement {
    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(ChatColor.GRAY + "").toItemStack();
    }
}
