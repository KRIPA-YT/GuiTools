package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.guicreator.ArrowBackElement;
import de.kripa.guitools.guicreator.amountselect.AmountSelectGUI;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIOpenButton;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class ItemEditGUI extends EmptyGUI {
    @Setter @Getter
    private ItemStack toEdit;

    public ItemEditGUI(ItemStack toEdit) {
        super("Edit item", 3);
        this.toEdit = toEdit;
    }

    @Override
    public Inventory render(Player p) {
        this.setGUIElement(new GUIOpenButton(new AmountSelectGUI(this.toEdit), this.toEdit), 4, 0);

        this.setGUIElement(new GUIOpenButton(new EnchantmentSelectGUI(this.toEdit.clone()), new ItemBuilder(Material.ENCHANTED_BOOK)
                .setName("§aEdit Enchantments").addLoreLine("").addLoreLine("§eClick to edit").toItemStack()), 1, 1);
        this.setGUIElement(new NameEditButton(this.toEdit.clone()), 3, 1);
        this.setGUIElement(new GUIOpenButton(new LoreEditGUI(this.toEdit.clone()), new ItemBuilder(Material.OAK_SIGN)
                .setName("§aEdit Lore").addLoreLine("").addLoreLine("§eClick to edit").toItemStack()), 5, 1);
        this.setGUIElement(new GUIOpenButton(new DurabilityEditGUI(this.toEdit.clone()), new ItemBuilder(Material.DIAMOND_SWORD)
                .setName("§aEdit Durability").addLoreLine("").addLoreLine("§eClick to edit").setDurability((short) 1)
                .setItemFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack()), 7, 1);

        this.setGUIElement(new ArrowBackElement(p), 4, 2);
        return super.render(p);
    }
}
