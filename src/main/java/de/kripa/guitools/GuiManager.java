package de.kripa.guitools;

import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.guicreator.itemedit.NameEditButton;
import de.kripa.guitools.history.HistoryManager;
import de.kripa.guitools.signgui.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class GuiManager implements Listener {
    public static SignManager signManager;
    public static HistoryManager historyManager;

    public GuiManager(PluginManager pm) {
        historyManager = new HistoryManager();
        signManager = new SignManager(GuiTools.plugin);
        signManager.init(pm);
        pm.registerEvents(this, GuiTools.plugin);
        pm.registerEvents(new NameEditButton(), GuiTools.plugin);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInvClick(InventoryClickEvent e) throws CloneNotSupportedException {
        Player p = (Player) e.getWhoClicked();

        int slotClicked = e.getSlot();
        if (slotClicked == -999) {
            return;
        }

        if (!historyManager.hasCurrentGUI(p)) {
            //p.sendMessage(PREFIX + "Doesn't have currentGUI");
            return;
        }

        if (e.getView().getTopInventory().getType() != InventoryType.CHEST) {
            return;
        }

        if (e.getRawSlot() > e.getView().getTopInventory().getSize()) {
            //p.sendMessage(PREFIX + "Not upper inventory");
            e.setCancelled(true);
            return;
        }

        GUI currentGUI = (GUI) historyManager.getLastHistoryEntry(p).getGui().clone();
        if (!e.getView().getTitle().equals(currentGUI.getTitle() + currentGUI.getMeta())) {
            //p.sendMessage(PREFIX + "Titles don't match");
            return;
        }

        boolean cancelled = !currentGUI.getGUIElement(slotClicked).onClick(new GUIElementClickEvent(p, e.isLeftClick(), e.getAction()));
        e.setCancelled(cancelled);
        if (cancelled) {
            e.setCurrentItem(null);
        }
        currentGUI = (GUI) historyManager.getLastHistoryEntry(p).getGui().clone();
        currentGUI.update();

        boolean invTitleChanged = !(e.getView().getTitle().equals(currentGUI.getTitle() + currentGUI.getMeta()));
        if (!invTitleChanged) {
            Objects.requireNonNull(e.getClickedInventory()).setContents(currentGUI.render(p).getContents());
            p.updateInventory();
            return;
        }
        GUI finalCurrentGUI = currentGUI;
        Bukkit.getScheduler().runTask(GuiTools.plugin, () -> {
            historyManager.setPreserveHistory(p, true);
            historyManager.removePlayerHistoryEntry(p, -1);
            finalCurrentGUI.openGUI(p);
            historyManager.setPreserveHistory(p, false);
        });
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (historyManager.getPreserveHistory(p)) {
            return;
        }
        historyManager.clearHistory(p);
    }

    private String getTitleOfInventory(Inventory inv) {
        try {
            return (String) this.getInventoryTitleField().get(getInventoryField().get(inv));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Field getInventoryTitleField() throws NoSuchFieldException {
        Class<?> craftInventoryCustomMinecraftInventory = Arrays.stream(CraftInventoryCustom.class.getDeclaredClasses()).filter(aClass -> aClass.getName().contains("MinecraftInventory")).toList().get(0);
        Field invTitleField = craftInventoryCustomMinecraftInventory.getDeclaredField("title");
        invTitleField.setAccessible(true);
        return invTitleField;
    }

    private Field getInventoryField() throws NoSuchFieldException {
        Class<?> craftInventoryClass = CraftInventory.class;
        Field craftInventoryInventoryField = craftInventoryClass.getDeclaredField("inventory");
        craftInventoryInventoryField.setAccessible(true);
        return craftInventoryInventoryField;
    }
}
