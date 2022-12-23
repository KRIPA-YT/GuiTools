package de.kripa.guitools.std;

import de.kripa.guitools.gui.GUIElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public class GUIBuilder extends ItemBuilder {
    public interface GUIElementClickHandler {
        boolean onClick(Player player, boolean isLeftClick, InventoryAction action);
    }

    @Setter @Getter
    private GUIElementClickHandler guiElementClickHandler;

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m The material to create the GUIBuilder with.
     */
    public GUIBuilder(Material m, @NonNull GUIElementClickHandler handler) {
        super(m);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder over an existing Itemstack.
     *
     * @param is The Itemstack to create the GUIBuilder over.
     */
    public GUIBuilder(ItemStack is, @NonNull GUIElementClickHandler handler) {
        super(is);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public GUIBuilder(Material m, int amount, @NonNull GUIElementClickHandler handler) {
        super(m, amount);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m          The material of the item.
     * @param amount     The amount of the item.
     * @param durability The durability of the item.
     */
    public GUIBuilder(Material m, int amount, byte durability, @NonNull GUIElementClickHandler handler) {
        super(m, amount, durability);
        this.guiElementClickHandler = handler;
    }

    public GUIElement toGUIElement() {
        return new GUIElement() {
            @Override
            public boolean onClick(Player player, boolean isLeftClick, InventoryAction action) {
                return guiElementClickHandler.onClick(player, isLeftClick, action);
            }

            @Override
            public ItemStack getIcon() {
                return toItemStack();
            }
        };
    }
}
