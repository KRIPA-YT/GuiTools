package de.kripa.guitools;

import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.guicreator.itemselect.ItemSelectGUI;
import de.kripa.guitools.history.HistoryManager;
import de.kripa.guitools.history.PlayerHistoryEntry;
import de.kripa.guitools.signgui.SignManager;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftInventoryCustom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public final class GuiTools extends JavaPlugin implements Listener {

    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&5[&dGuiTools&5]: &f");
    public static SignManager signManager;
    public static GuiTools plugin;
    public static HistoryManager historyManager;

    @Override
    public void onEnable() {
        plugin = this;
        historyManager = new HistoryManager();

        Objects.requireNonNull(getCommand("guibuilder")).setExecutor(new ItemSelectGUI(0, "", ItemSelectGUI.ITEM_ID));
        Objects.requireNonNull(getCommand("test")).setExecutor(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        signManager = new SignManager(this);
        signManager.init(pm);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!historyManager.hasCurrentGUI(p)) {
            return;
        }

        GUI currentGUI = historyManager.getLastHistoryEntry(p).getGui();
        if (!e.getView().getTitle().equals(currentGUI.getTitle() + currentGUI.getMeta())) {
            return;
        }

        if (e.getRawSlot() > e.getView().getTopInventory().getSize()) {
            e.setCancelled(true);
            return;
        }

        int slotClicked = e.getSlot();
        if (slotClicked == -999) {
            return;
        }

        boolean cancelled = !currentGUI.getGUIElement(slotClicked).onClick(new GUIElementClickEvent(p, e.isLeftClick(), e.getAction()));
        e.setCancelled(cancelled);
        if (cancelled) {
            e.setCurrentItem(null);
        }
        currentGUI = historyManager.getLastHistoryEntry(p).getGui();

        currentGUI.update();

        boolean invTitleChanged = !(e.getView().getTitle().equals(currentGUI.getTitle() + currentGUI.getMeta()));

        if (!invTitleChanged) {
            Objects.requireNonNull(e.getClickedInventory()).setContents(currentGUI.render(p).getContents());
            p.updateInventory();
            return;
        }

        boolean titleChanged = !e.getView().getTitle().startsWith(currentGUI.getTitle());
        boolean metaChanged = !titleChanged;
        GUI finalCurrentGUI = currentGUI;
        Bukkit.getScheduler().runTask(this, () -> {
            historyManager.setPreserveHistory(p, true);
            if (metaChanged) {
                historyManager.removePlayerHistoryEntry(p, -1);
            }
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


    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(PREFIX + "You have to be a Player to use this command!");
            return true;
        }

        EmptyGUI emptyGUI = new EmptyGUI("Empty GUI", 6);
        historyManager.appendHistory(new PlayerHistoryEntry(p, emptyGUI));
        p.sendMessage(PREFIX + "History appended");
        p.sendMessage(PREFIX + "hasCurrentGUI(" + p.getName() + ") = " + historyManager.hasCurrentGUI(p));
        return true;
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
