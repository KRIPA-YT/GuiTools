package de.kripa.guitools.guicreator;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.BackElement;
import de.kripa.guitools.std.element.EmptyElement;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArrowBackElement extends BackElement {
    private Player p;
    @Getter
    private String color = "GRAY";

    public ArrowBackElement(Player p) {
        this(p, "GRAY");
    }

    public ArrowBackElement(Player p, String color) {
        super(new ItemBuilder(Material.ARROW).setName("§aBack").toItemStack());
        this.p = p;
        this.setColor(color);
    }

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return GuiManager.historyManager.hasHistory(e.getPlayer()) && super.onClick(e);
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = super.getIcon();
        if (!GuiManager.historyManager.hasHistory(p)) {
            icon = new EmptyElement(this.color).getIcon();
        }
        return icon;
    }

    public void setColor(String color) {
        if (Material.getMaterial(color.toUpperCase() + "_STAINED_GLASS_PANE") == null) {
            throw new IllegalArgumentException("Argument color must be a valid color!");
        }
        this.color = color.toUpperCase();
    }
}
