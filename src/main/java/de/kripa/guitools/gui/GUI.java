package de.kripa.guitools.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class GUI {
    public void openGUI(Player p) {
        p.openInventory(toInventory());
    }

    /** Converts this object to an org.bukkit.inventory.Inventory
     * @return The Inventory
     */
    protected abstract Inventory toInventory();

    /**
     * Sets the GUIElement at the given coordinate
     * @param guiElement The GUIElement to set
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    public void setGUIElement(GUIElement guiElement, int x, int y) {
        this.setGUIElement(guiElement, convertXYtoIndex(x, y));
    }

    /**
     * Sets the GUIElement at the given slot
     * @param guiElement The GUIElement to set
     * @param slot slot
     */
    public abstract void setGUIElement(GUIElement guiElement, int slot);

    /**
     * Gets the GUIElement at the given coordinate
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return The GUIElement at the given coordinate
     */
    public GUIElement getGUIElement(int x, int y) {
        return this.getGUIElement(convertXYtoIndex(x, y));
    }

    /**
     * Gets the GUIElement at the given slot
     * @param slot slot
     * @return The GUIElement at the given slot
     */
    public abstract GUIElement getGUIElement(int slot);

    protected void checkSlotValidity(int slot, int invSize) {
        if (slot >= invSize) {
            throw new IndexOutOfBoundsException("slot is over " + invSize);
        }
    }

    protected int convertXYtoIndex(int x, int y) {
        return y * 9 + x;
    }
}
