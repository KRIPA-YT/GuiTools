package de.kripa.guitools.std.element.button;

import de.kripa.guitools.gui.GUI;
import de.kripa.guitools.gui.GUIElementClickEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class GUIOpenButton implements GUIButton {
    @Setter
    @Getter
    private GUI toOpen;
    @Getter
    private ItemStack icon;

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        this.playDing(e.getPlayer());
        toOpen.scheduleOpenGUI(e.getPlayer());
        return false;
    }
}
