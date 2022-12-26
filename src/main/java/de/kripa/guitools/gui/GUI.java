package de.kripa.guitools.gui;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.history.PlayerHistoryEntry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface GUI extends Cloneable {

    /**
     * <strong>Do not use this! Use {@link #scheduleOpenGUI(Player)} instead.<br/>
     * For internal use only. </strong>
     * <br/>
     * Opens the GUI for a Player
     * @param p The Player to open the GUI for
     */
    @Deprecated
    default void openGUI(Player p) {
        GuiManager.historyManager.appendHistory(new PlayerHistoryEntry(p, this));
        p.openInventory(render(p));
    }

    default void scheduleOpenGUI(Player p) {
        GuiManager.historyManager.setPreserveHistory(p, true);
        this.update();
        this.openGUI(p);
        GuiManager.historyManager.setPreserveHistory(p, false);
    }

    Object clone();

    /**
     * Converts this object to an org.bukkit.inventory.Inventory
     * @return The Inventory
     */
    Inventory render(Player p);

    /**
     * Updates the Inventory
     */
    default void update() {

    };

    String getTitle();
    void setTitle(String title);

    default String getMeta() {
        return "";
    }

    /**
     * Sets the GUIElement at the given coordinate
     * @param guiElement The GUIElement to set
     * @param x X-Coordinate
     * @param y Y-Coordinate
     */
    default void setGUIElement(GUIElement guiElement, int x, int y) {
        this.setGUIElement(guiElement, convertXYtoIndex(x, y));
    }

    /**
     * Sets the GUIElement at the given slot
     * @param guiElement The GUIElement to set
     * @param slot slot
     */
    void setGUIElement(GUIElement guiElement, int slot);

    /**
     * Gets the GUIElement at the given coordinate
     * @param x X-Coordinate
     * @param y Y-Coordinate
     * @return The GUIElement at the given coordinate
     */
    default GUIElement getGUIElement(int x, int y) {
        return this.getGUIElement(convertXYtoIndex(x, y));
    }

    /**
     * Gets the GUIElement at the given slot
     * @param slot slot
     * @return The GUIElement at the given slot
     */
    GUIElement getGUIElement(int slot);

    default void checkSlotValidity(int slot, int invSize) {
        if (slot >= invSize) {
            throw new IndexOutOfBoundsException("slot is over " + invSize);
        }
    }

    default int convertXYtoIndex(int x, int y) {
        return y * 9 + x;
    }
}
