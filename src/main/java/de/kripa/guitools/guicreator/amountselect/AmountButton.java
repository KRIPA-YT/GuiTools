package de.kripa.guitools.guicreator.amountselect;

import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.kripa.guitools.GuiTools.PREFIX;

@AllArgsConstructor
public class AmountButton implements GUIButton {
    private ItemStack icon;
    @Setter @Getter
    private int amount;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        Player p = e.getPlayer();
        Inventory playerInventory = p.getInventory();
        if (this.amount == -1) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.1F);
            return false;
        }

        if (playerInventory.firstEmpty() == -1) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.1F);
            p.sendMessage(PREFIX + "Your inventory is full!");
            return false;
        }
        this.playDing(p);
        ItemStack toAdd = this.icon.clone();
        toAdd.setAmount(this.amount);
        playerInventory.addItem(toAdd);
        return false;
    }

    @Override
    public ItemStack getIcon() {
        if (this.amount == -1) {
            return new ItemBuilder(Material.REDSTONE_BLOCK)
                    .setName("§4Invalid Amount!")
                    .addLoreLine("§c§o-1")
                    .addLoreLine("")
                    .addLoreLine("§eClick the sign")
                    .addLoreLine("§ebelow to fix!")
                    .toItemStack();
        };
        ItemStack toReturn = this.icon.clone();
        toReturn.setAmount(this.amount);
        return new ItemBuilder(this.icon.clone()).setAmount(this.amount).addLoreLine("").addLoreLine("§eClick to add").addLoreLine("§eitem to inventory!").toItemStack();
    }
}
