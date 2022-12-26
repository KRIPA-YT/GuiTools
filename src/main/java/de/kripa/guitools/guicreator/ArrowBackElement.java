package de.kripa.guitools.guicreator;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.BackElement;
import de.kripa.guitools.std.element.EmptyElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArrowBackElement extends BackElement {
    private Player p;
    public ArrowBackElement(Player p) {
        super(new ItemBuilder(Material.ARROW).setName("§aBack").toItemStack());
        this.p = p;
    }

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return GuiManager.historyManager.hasHistory(e.getPlayer()) && super.onClick(e);
    }

    @Override
    public ItemStack getIcon() {
        ItemStack icon = super.getIcon();
        if (!GuiManager.historyManager.hasHistory(p)) {
            icon = new EmptyElement().getIcon();
        }
        return icon;
    }
}
