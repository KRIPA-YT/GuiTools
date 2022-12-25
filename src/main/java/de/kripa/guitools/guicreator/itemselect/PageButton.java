package de.kripa.guitools.guicreator.itemselect;

import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.GUIButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PageButton implements GUIButton {
    @Setter private ItemStack icon;
    @Setter @Getter
    private int page, rightClickPage;
    @Setter @Getter
    private boolean increment;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        this.playDing(e.getPlayer());
        if (!e.isLeftClick()) {
            this.page = this.rightClickPage;
            return false;
        }

        if (increment) {
            this.page++;
        } else {
            this.page--;
        }
        return false;
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder iconBuilder = new ItemBuilder(icon.clone());

        if (this.increment) {
            iconBuilder
                .addLoreLine("§8§o" + (this.page + 2))
                .addLoreLine("")
                .addLoreLine("§bRight click to go to end")
                .addLoreLine("§eLeft click for next page");
        } else {
            iconBuilder
                .addLoreLine("§8§o" + this.page)
                .addLoreLine("")
                .addLoreLine("§bRight click to go to start")
                .addLoreLine("§eLeft click for previous page");
        }
        return iconBuilder.toItemStack();
    }
}
