package de.kripa.guitools.std.element;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.element.button.GUIButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackElement implements GUIButton {
    @Getter private ItemStack icon;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        this.playDing(e.getPlayer());
        GuiManager.historyManager.removePlayerHistoryEntry(e.getPlayer(), -1);
        return false;
    }
}
