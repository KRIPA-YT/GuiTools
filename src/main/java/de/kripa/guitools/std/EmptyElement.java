package de.kripa.guitools.std;

import de.kripa.guitools.gui.GUIElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class EmptyElement implements GUIElement {
    @Override
    public boolean onClick(Player player, boolean isLeftClick, InventoryAction action) {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.AIR);
    }
}
