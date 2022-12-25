package de.kripa.guitools.std.element;

import de.kripa.guitools.GuiTools;
import de.kripa.guitools.gui.GUIElementClickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class BackElement implements GUIButton {
    @Getter private ItemStack icon;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        GuiTools.historyManager.removePlayerHistoryEntry(e.getPlayer(), -1);
        return false;
    }
}
