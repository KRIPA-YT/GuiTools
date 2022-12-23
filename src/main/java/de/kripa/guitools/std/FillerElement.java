package de.kripa.guitools.std;

import de.kripa.guitools.gui.GUIElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class FillerElement implements GUIElement {
    @Override
    public boolean onClick(Player player, boolean isLeftClick, InventoryAction action) {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setName(ChatColor.GRAY + "").toItemStack();
    }
}
