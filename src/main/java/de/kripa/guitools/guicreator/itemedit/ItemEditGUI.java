package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemEditGUI extends EmptyGUI {
    @Setter @Getter
    private ItemStack toEdit;

    public ItemEditGUI(ItemStack toEdit) {
        super("Edit item", 3);
    }

    @Override
    public Inventory render(Player p) {

        return super.render(p);
    }
}
