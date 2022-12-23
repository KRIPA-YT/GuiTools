package de.kripa.guitools.history;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    @Getter(AccessLevel.PROTECTED) private List<PlayerHistory> playerHistoryList = new ArrayList<>();
    @Getter(AccessLevel.PROTECTED) private List<Player> preserveHistory = new ArrayList<>();

    /**
     * Copies another HistoryManager object
     * @param historyManager The object to copy
     */
    public HistoryManager(HistoryManager historyManager) {
        this.playerHistoryList = historyManager.getPlayerHistoryList();
        this.preserveHistory = historyManager.getPreserveHistory();
    }

    /**
     * Initializes a HistoryManager object
     * @param playerHistories Optional existing PlayerHistories
     */
    public HistoryManager(PlayerHistory... playerHistories) {
        this.playerHistoryList.addAll(List.of(playerHistories));
    }

    /**
     * Registers new Player
     * @param playerHistory The PlayerHistory object the Player is assigned to
     */
    public void registerNewPlayer(PlayerHistory playerHistory) {
        this.playerHistoryList.add(playerHistory);
    }

    /**
     * Appends a PlayerHistoryEntry to a Player
     * @param playerHistoryEntry The PlayerHistoryEntry to append
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
     * Gets the last PlayerHistoryEntry
     * @param player The Player to get from
     * @return Their last PlayerHistoryEntry
     */
    public PlayerHistoryEntry getLastHistoryEntry(Player player) {
        return this.getPlayerHistory(player).getEntry(-1);
    }

    /**
     * Checks if the given Player has PlayerHistory assigned
     * @param player The Player to check
     * @return If they have PlayerHistory assigned
     */
    public boolean hasHistory(Player player) {
        return (this.getPlayerHistory(player) != null) || (this.getPlayerHistory(player).getHistory().size() == 0);
    }

    /**
     * Gets the PlayerHistory for a given Player; If it doesn't exist it returns null
     * @param player The Player to get from
     * @return The PlayerHistory
     */
    public PlayerHistory getPlayerHistory(Player player) {
        try {
            return this.playerHistoryList.stream().filter(pH -> pH.getPlayer() == player).toList().get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Set if PlayerHistory should be preserved on InventoryClose
     * @param player The Player to set to
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
     * Get if PlayerHistory should be preserved
     * @param player The Player to get from
     * @return The preserve flag
     */
    public boolean getPreserveHistory(Player player) {
        return this.preserveHistory.contains(player);
    }

    public PlayerHistoryEntry[] clearHistory(Player player) {
        return this.getPlayerHistory(player).clearHistory();
    }
}
