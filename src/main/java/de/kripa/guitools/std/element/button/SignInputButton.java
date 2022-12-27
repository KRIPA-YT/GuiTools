package de.kripa.guitools.std.element.button;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.GuiTools;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.signgui.SignCompleteEvent;
import de.kripa.guitools.signgui.SignGUI;
import de.kripa.guitools.std.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignInputButton implements GUIButton {
    protected ItemStack icon;
    @Getter private final String[] lines;
    @Setter @Getter private boolean removeHistory;

    public SignInputButton(ItemStack icon, String... lines) {
        this(icon, true, lines);
    }

    public SignInputButton(ItemStack icon, boolean removeHistory, String... lines) {
        if (lines.length > 4) {
            throw new IllegalArgumentException("lines must have a length of 4 or less");
        }

        List<String> linesList = new ArrayList<>(Arrays.asList(lines));
        while (linesList.size() < 4) {
            linesList.add(0, "");
        }
        this.icon = icon;
        this.removeHistory = removeHistory;
        this.lines = linesList.toArray(String[]::new);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onClick(GUIElementClickEvent e) {
        Player p = e.getPlayer();
        this.playDing(p);
        if (!e.isLeftClick()) {
            this.setResult("");
            return false;
        }

        SignGUI signGUI = new SignGUI(GuiManager.signManager, event -> onSignComplete(event, p, true));
        signGUI.withLines(this.lines);
        GuiManager.historyManager.setPreserveHistory(p, true);
        signGUI.open(e.getPlayer());
        return false;
    }

    protected void onSignComplete(SignCompleteEvent event, Player p, boolean playDing) {
        if (playDing) {
            this.playDing(p);
        }
        this.setResult(event.getLines()[0]);
        Bukkit.getScheduler().runTask(GuiTools.plugin, () -> {
            GuiManager.historyManager.getLastHistoryEntry(p).getGui().update();
            if (removeHistory) {
                p.openInventory(GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p));
            } else {
                GuiManager.historyManager.getLastHistoryEntry(p).getGui().openGUI(p);
            }
            GuiManager.historyManager.setPreserveHistory(p, false);
        });
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(this.icon.clone())
                .addLoreLine(this.getResult().equals("") ? "§8§oempty" : "§8" + this.getResult())
                .addLoreLine("")
                .addLoreLine("§bRight click to reset")
                .addLoreLine("§eLeft click to search")
                .toItemStack();
    }

    public void setResult(String result) {
        this.lines[0] = result;
    }

    public String getResult() {
        return this.lines[0];
    }
}
