package de.kripa.guitools.std.gui;

import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.std.element.AirElement;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;


@Data
public class SimpleGUI implements GUI {
    @NonNull @Setter @Getter protected String title;
    @NonNull @Setter @Getter protected GUIElement[] content;

    @NonNull @Getter protected String meta = "";

    public SimpleGUI(@NonNull String title, int rows) {
        this(title, generateEmptyElementArray(rows * 9));
    }

    public SimpleGUI(@NonNull String title, @NonNull GUIElement[] content) {
        if (content.length % 9 != 0) {
            throw new IllegalArgumentException("content has to have a length divisible by 9");
        }
        if (content.length > 6*9) {
            throw new IndexOutOfBoundsException("size of content be smaller than 54");
        }

        this.title = title;
        this.content = content;
    }

    @Override
    public Inventory render(Player p) {
        Inventory inv = Bukkit.createInventory(null, this.content.length, this.title + this.meta);
        inv.setContents(Arrays.stream(this.content).map(GUIElement::getIcon).toArray(ItemStack[]::new));
        return inv;
    }

    @Override
    public void setGUIElement(GUIElement guiElement, int slot) {
        this.content[slot] = guiElement;
    }

    @Override
    public GUIElement getGUIElement(int slot) {
        return this.content[slot];
    }

    private static GUIElement[] generateEmptyElementArray(int length) {
        GUIElement[] result = new GUIElement[length];
        for (int i = 0; i < length; i++) {
            result[i] = new AirElement();
        }
        return result;
    }

    public GUI clone() {
        try {
            return (GUI) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
