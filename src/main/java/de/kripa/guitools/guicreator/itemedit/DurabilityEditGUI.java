package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class DurabilityEditGUI extends EmptyGUI {
    @Setter @Getter
    private ItemStack toEdit;

    public DurabilityEditGUI(ItemStack toEdit) {
        super("Edit lore", 3);
        this.toEdit = toEdit;
    }
}
