package de.kripa.guitools.std.element.button;

import de.kripa.guitools.GuiTools;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@AllArgsConstructor
public class CloseGUIButton implements GUIButton {
    @Getter private ItemStack icon = new ItemBuilder(Material.BARRIER).setName("§cClose").toItemStack();

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        this.playDing(e.getPlayer());
        Bukkit.getScheduler().runTask(GuiTools.plugin, () -> e.getPlayer().closeInventory());
        return false;
    }
}
