package de.kripa.guitools.std.element;

import de.kripa.guitools.gui.GUIElement;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public interface GUIButton extends GUIElement {
    default void playDing(Player p) {
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 1.0F, 2.0F);
    }
}
