package de.kripa.guitools.guicreator.amountselect;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.guicreator.CloseBackElement;
import de.kripa.guitools.guicreator.itemedit.ItemEditGUI;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.EmptyElement;
import de.kripa.guitools.std.element.button.GUIOpenButton;
import de.kripa.guitools.std.element.button.SignInputButton;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AmountSelectGUI extends EmptyGUI {
    @Setter @Getter
    private ItemStack toModify;

    private SignInputButton amountSelectInput = new SignInputButton(new ItemBuilder(Material.OAK_SIGN).setName("§aCustom Amount: ").toItemStack(), "", "^^^^^^^^^^^^^^^", "Enter custom", "amount here");

    public AmountSelectGUI(ItemStack toModify) {
        super("Select Amount", 3);
        this.toModify = toModify;
    }

    @Override
    public Inventory render(Player p) {
        // toModify top selection
        String amount = this.amountSelectInput.getResult();
        if (amount.equals("")) {
            this.setGUIElement(new EmptyElement(), 4, 0);
        } else if (amount.matches("-?\\d+") && (0 < Integer.parseInt(amount)) && (Integer.parseInt(amount) < 65)) {
            this.setGUIElement(new AmountButton(toModify, Integer.parseInt(amount)), 4, 0);
        } else {
            this.setGUIElement(new AmountButton(toModify, -1), 4, 0);
        }

        // toModify normal selection row
        this.setGUIElement(new AmountButton(toModify,  1), 2, 1);
        this.setGUIElement(new AmountButton(toModify,  5), 3, 1);
        this.setGUIElement(new AmountButton(toModify, 10), 4, 1);
        this.setGUIElement(new AmountButton(toModify, 32), 5, 1);
        this.setGUIElement(new AmountButton(toModify, 64), 6, 1);


        // Back arrow
        if (GuiManager.historyManager.hasHistory(p)) {
            this.setGUIElement(new CloseBackElement(p), 3, 2);
        } else {
            this.setGUIElement(new EmptyElement(), 3, 2);
        }

        // Edit item
        this.setGUIElement(new GUIOpenButton(new ItemEditGUI(this.toModify.clone()), new ItemBuilder(Material.ANVIL).setName("§aEdit item:").addLoreLine("").addLoreLine("§eClick to edit").toItemStack()), 4, 2);

        // Custom amount
        this.setGUIElement(this.amountSelectInput, 5, 2);
        return super.render(p);
    }
}
