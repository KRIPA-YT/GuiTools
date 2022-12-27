package de.kripa.guitools.gui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

@Data
@AllArgsConstructor
public class GUIElementClickEvent {
    @Getter private Player player;
    @Getter private boolean leftClick;
    @Getter private boolean shiftClick;
    @Getter private InventoryAction action;
}
