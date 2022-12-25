package de.kripa.guitools.guicreator.itemselect;

import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.element.GUIButton;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class ItemButton implements GUIButton {
    @Getter private ItemStack icon;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return false;
    }
}
