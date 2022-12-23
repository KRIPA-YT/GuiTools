package de.kripa.guitools;

import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.history.HistoryManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GuiTools extends JavaPlugin implements Listener {

    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&5[&dGuiTools&5]: &f");
    public static GuiTools plugin;
    public static HistoryManager historyManager;

    @Override
    public void onEnable() {
        plugin = this;
        historyManager = new HistoryManager();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!historyManager.hasHistory(p)) {
            return;
        }

        GUI currentGUI = historyManager.getLastHistoryEntry(p).getGui();
        int slotClicked = e.getSlot();

        boolean cancelled = !currentGUI.getGUIElement(slotClicked).onClick(p, e.isLeftClick(), e.getAction());
        e.setCancelled(cancelled);
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (historyManager.getPreserveHistory(p)) {
            return;
        }

        historyManager.clearHistory(p);
    }
}
