package de.kripa.guitools.std.element.button;

import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIButton;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Data
public class RotatingSelectButton implements GUIButton {
    private ItemStack icon;
    @Setter @Getter
    private String[] options;
    @Setter @Getter
    private int selectedOption;

    public RotatingSelectButton(ItemStack icon, String... options) {
        this(icon, 0, options);
    }

    public RotatingSelectButton(ItemStack icon, int selectedOption, String... options) {
        if (options.length <= 0) {
            throw new IllegalArgumentException("Length of options should be more than 0");
        }

        this.icon = icon;
        this.options = options;
        this.selectedOption = selectedOption;
    }

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        this.playDing(e.getPlayer());
        this.selectedOption = (this.selectedOption + (e.isLeftClick() ? 1 : 2)) % options.length;
        return false;
    }

    @Override
    public ItemStack getIcon() {
        ItemBuilder iconBuilder = new ItemBuilder(this.icon.clone());
        for (int i = 0; i < this.options.length; i++) {
            String prefix = (i == this.selectedOption) ? "§b▶ " : "§8";
            iconBuilder.addLoreLine(prefix + this.options[i]);
        }
        iconBuilder.addLoreLine("");
        iconBuilder.addLoreLine("§bRight click for previous");
        iconBuilder.addLoreLine("§eLeft click for next");
        return iconBuilder.toItemStack();
    }
}
