package de.kripa.guitools.history;

import de.kripa.guitools.gui.GUI;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    @Getter(AccessLevel.PROTECTED) private List<PlayerHistory> playerHistoryList = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED) private List<Player> preserveHistory = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED) private List<Player> blockAddHistory = new ArrayList<>();

    /**
     * Copies another {@link HistoryManager} object
     * @param historyManager The object to copy
     */
    public HistoryManager(HistoryManager historyManager) {
        this.playerHistoryList = historyManager.getPlayerHistoryList();
        this.preserveHistory = historyManager.getPreserveHistory();
    }

    /**
     * Initializes a {@link HistoryManager} object
     * @param playerHistories Optional existing {@link PlayerHistory}s
     */
    public HistoryManager(PlayerHistory... playerHistories) {
        this.playerHistoryList.addAll(List.of(playerHistories));
    }

    /**
     * Registers new {@link Player}
     * @param playerHistory The {@link PlayerHistory} object the {@link Player} is assigned to
     */
    public void registerNewPlayer(PlayerHistory playerHistory) {
        this.playerHistoryList.add(playerHistory);
    }

    /**
     * Appends a {@link PlayerHistoryEntry} to a Player
     * @param playerHistoryEntry The {@link PlayerHistoryEntry} to append
     */
    public void appendHistory(PlayerHistoryEntry playerHistoryEntry) {
        Player player = playerHistoryEntry.getPlayer();
        if (!this.playerHistoryList.contains(player)) {
            this.registerNewPlayer(new PlayerHistory(player));
        }

        PlayerHistory playerHistory = this.getPlayerHistory(player);
        this.playerHistoryList.set(this.playerHistoryList.indexOf(playerHistory), playerHistory.addEntry(playerHistoryEntry));
    }

    /**
     * Gets the last {@link PlayerHistoryEntry}
     * @param player The {@link Player} to get from
     * @return Their last {@link PlayerHistoryEntry}
     */
    public PlayerHistoryEntry getLastHistoryEntry(Player player) {
        return this.getPlayerHistory(player).getEntry(-1);
    }

    /**
     * Checks if the given {@link Player} has {@link PlayerHistory} assigned
     * @param player The {@link Player} to check
     * @return If they have {@link PlayerHistory} assigned
     */
    public boolean hasHistory(Player player) {
        return (this.getPlayerHistory(player) != null) && (this.getPlayerHistory(player).getHistory().size() > 1);
    }

    /**
     * Checks if the given {@link Player} has {@link GUI} currently open
     * @param player The {@link Player} to check
     * @return If they have {@link {@link GUI} currently open
     */
    public boolean hasCurrentGUI(Player player) {
        return (this.getPlayerHistory(player) != null) && (this.getPlayerHistory(player).getHistory().size() > 0);
    }

    /**
     * Gets the {@link PlayerHistory} for a given Player; If it doesn't exist it returns null
     * @param player The {@link Player} to get from
     * @return The {@link PlayerHistory}
     */
    public PlayerHistory getPlayerHistory(Player player) {
        try {
            return this.playerHistoryList.stream().filter(pH -> pH.getPlayer() == player).toList().get(0);
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Remove {@link PlayerHistoryEntry} on the given index
     * @param player The {@link Player} to remove from
     * @param index The index
     * @return The removed {@link PlayerHistoryEntry}
     */
    public PlayerHistoryEntry removePlayerHistoryEntry(Player player, int index) {
        return this.getPlayerHistory(player).removeEntry(index);
    }

    /**
     * Set if {@link PlayerHistory} should be preserved on InventoryClose
     * @param player The {@link Player} to set to
     * @param preserve The preserve flag
     */
    public void setPreserveHistory(Player player, boolean preserve) {
        if (!this.preserveHistory.contains(player) && preserve) {
            this.preserveHistory.add(player);
        }
        if (this.preserveHistory.contains(player) && !preserve) {
            this.preserveHistory.remove(player);
        }
    }

    /**
     * Get if {@link PlayerHistory} should be preserved
     * @param player The {@link Player} to get from
     * @return The preserve flag
     */
    public boolean getPreserveHistory(Player player) {
        return this.preserveHistory.contains(player);
    }

    /**
     * Clears the {@link PlayerHistory} of a Player
     * @param player The {@link Player} to clear
     * @return The entire cleared {@link PlayerHistory}
     */
    public PlayerHistoryEntry[] clearHistory(Player player) {
        if (!this.hasCurrentGUI(player)) {
            return new PlayerHistoryEntry[0];
        }
        return this.getPlayerHistory(player).clearHistory();
    }
}
