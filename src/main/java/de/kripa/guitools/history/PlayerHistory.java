package de.kripa.guitools.history;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerHistory {
    @Getter private final Player player;
    @Getter(AccessLevel.PROTECTED) private List<PlayerHistoryEntry> history = new ArrayList<>();

    /**
     * Copies another PlayerHistory object
     * @param playerHistory The object to copy
     */
    public PlayerHistory(PlayerHistory playerHistory) {
        this.player = playerHistory.getPlayer();
        this.history = playerHistory.getHistory();
    }

    /**
     * Initializes a PlayerHistory object with a given player and optional PlayerHistoryEntries
     * @param player The Player to assign the history to
     * @param history Optional already existing PlayerHistoryEntries
     */
    public PlayerHistory(Player player, PlayerHistoryEntry... history) {
        this.player = player;
        this.history.addAll(List.of(history));
    }

    /**
     * Adds a single or multiple PlayerHistoryEntries
     *
     * @param playerHistoryEntries The PlayerHistoryEntries to add
     * @return this
     */
    public PlayerHistory addEntry(PlayerHistoryEntry... playerHistoryEntries) {
        this.history.addAll(List.of(playerHistoryEntries));
        return this;
    }

    /**
     * Removes an Entry from History
     * @param index The index to remove from; When it is negative it gets removed from the back
     * @return The removed PlayerHistoryEntry
     */
    public PlayerHistoryEntry removeEntry(int index) {
        if (index < 0) {
            index = this.history.size() + index; // When negative, remove from back
        }
        return this.history.remove(index);
    }

    /**
     * Gets an Entry from History
     * @param index The index to get from; When it is negative it gets removed from the back
     * @return The PlayerHistoryEntry at position index
     */
    public PlayerHistoryEntry getEntry(int index) {
        if (index < 0) {
            index = this.history.size() + index; // When negative, remove from back
        }
        return this.history.get(index);
    }

    /**
     * Clears the entire history
     * @return The entire deleted history
     */
    public PlayerHistoryEntry[] clearHistory() {
        PlayerHistoryEntry[] deleted = history.toArray(PlayerHistoryEntry[]::new);
        history.clear();
        return deleted;
    }
}
