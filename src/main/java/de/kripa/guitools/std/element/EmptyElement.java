package de.kripa.guitools.std.element;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class EmptyElement implements GUIElement {
    @Getter
    private String color = "GRAY";

    public EmptyElement(String color) {
        this.setColor(color);
    }

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return false;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.getMaterial(color.toUpperCase() + "_STAINED_GLASS_PANE")).setName(ChatColor.GRAY + "").toItemStack();
    }

    public void setColor(String color) {
        if (Material.getMaterial(color.toUpperCase() + "_STAINED_GLASS_PANE") == null) {
            throw new IllegalArgumentException("Argument color must be a valid color!");
        }
        this.color = color.toUpperCase();
    }
}
