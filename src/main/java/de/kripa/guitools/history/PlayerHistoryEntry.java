package de.kripa.guitools.history;

import de.kripa.guitools.gui.GUI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class PlayerHistoryEntry {
    @Setter @Getter private Player player;
    @Setter @Getter private GUI gui;
}
