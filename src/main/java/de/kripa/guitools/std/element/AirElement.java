package de.kripa.guitools.std.element;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.gui.GUIElementClickEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AirElement implements GUIElement {
    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Material.AIR);
    }
}
