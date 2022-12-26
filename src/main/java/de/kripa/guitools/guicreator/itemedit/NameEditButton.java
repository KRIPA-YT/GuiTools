package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.GuiTools;
import de.kripa.guitools.anvilgui.AnvilGUI;
import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIButton;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static de.kripa.guitools.GuiManager.historyManager;

@NoArgsConstructor
@RequiredArgsConstructor
public class NameEditButton implements GUIButton, Listener {
    @NonNull
    private ItemStack toEdit;

    private static final List<Player> anvilGUIOpen = new ArrayList<>();

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        Player p = e.getPlayer();
        if (!e.isLeftClick()) {
            ItemMeta toEditMeta = toEdit.getItemMeta();
            assert toEditMeta != null;
            toEditMeta.setDisplayName(null);
            this.toEdit.setItemMeta(toEditMeta);
            return false;
        }
        Bukkit.getScheduler().runTask(GuiTools.plugin, () -> {
            historyManager.setPreserveHistory(p, true);
            anvilGUIOpen.add(p);
            new AnvilGUI.Builder()
                    .plugin(GuiTools.plugin)
                    .itemLeft(this.toEdit)
                    .title("Edit item name")
                    .onLeftInputClick((player) -> {
                        this.playDing(player);
                        handleClose(player);
                    }).onRightInputClick((player) -> {
                        this.playDing(player);
                        handleClose(player);
                    }).onClose(this::handleClose)
                    .onComplete((completion -> {
                        this.playDing(completion.getPlayer());
                        new ItemEditGUI(completion.getOutputItem()).scheduleOpenGUI(completion.getPlayer());
                        return Collections.emptyList();
                    }))
                    .open(p);
        });
        return false;
    }

    private void handleClose(Player p) {
        GUI currentGUI = (GUI) historyManager.getLastHistoryEntry(p).getGui().clone();
        currentGUI.update();
        p.openInventory(GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p));
        historyManager.setPreserveHistory(p, false);
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.ANVIL).setName("§aEdit Name").addLoreLine("").addLoreLine("§bRight click to reset").addLoreLine("§eLeft click to edit").toItemStack();
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent e) {
        if (e.getViewers().size() < 1) {
            return;
        }
        Player p = (Player) e.getViewers().get(0);
        if (!anvilGUIOpen.contains(p)) {
            return;
        }
        try {
            e.getInventory().setItem(2,
                    new ItemBuilder(Objects.requireNonNull(e.getInventory().getItem(2)).clone())
                    .setName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(2)).getItemMeta()).getDisplayName()))
                    .toItemStack());
        } catch (NullPointerException ignored) {

        }
        anvilGUIOpen.remove(p);
    }
}
