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
     * Copies another {@link PlayerHistory} object
     * @param playerHistory The object to copy
     */
    public PlayerHistory(PlayerHistory playerHistory) {
        this.player = playerHistory.getPlayer();
        this.history = playerHistory.getHistory();
    }

    /**
     * Initializes a {@link PlayerHistory} object with a given player and optional PlayerHistoryEntries
     * @param player The {@link Player} to assign the history to
     * @param history Optional already existing {@link PlayerHistoryEntry}s
     */
    public PlayerHistory(Player player, PlayerHistoryEntry... history) {
        this.player = player;
        this.history.addAll(List.of(history));
    }

    /**
     * Adds a single or multiple {@link PlayerHistoryEntry}s
     *
     * @param playerHistoryEntries The {@link PlayerHistoryEntry}s to add
     * @return this
     */
    public PlayerHistory addEntry(PlayerHistoryEntry... playerHistoryEntries) {
        this.history.addAll(List.of(playerHistoryEntries));
        return this;
    }

    /**
     * Removes a {@link PlayerHistoryEntry} from History
     * @param index The index to remove from; When it is negative it gets removed from the back
     * @return The removed {@link PlayerHistoryEntry}
     */
    public PlayerHistoryEntry removeEntry(int index) {
        if (index < 0) {
            index = this.history.size() + index; // When negative, remove from back
        }
        return this.history.remove(index);
    }

    /**
     * Gets a {@link PlayerHistoryEntry} from History
     * @param index The index to get from; When it is negative it gets removed from the back
     * @return The {@link PlayerHistoryEntry} at position index
     */
    public PlayerHistoryEntry getEntry(int index) {
        if (index < 0) {
            index = this.history.size() + index; // When negative, remove from back
        }
        return this.history.get(index);
    }

    /**
     * Clears the entire history
     * @return The entire deleted history as a {@link PlayerHistoryEntry} list
     */
    public PlayerHistoryEntry[] clearHistory() {
        PlayerHistoryEntry[] deleted = history.toArray(PlayerHistoryEntry[]::new);
        history.clear();
        return deleted;
    }

    /**
     * Gets the size of the {@link PlayerHistory}
     * @return The size
     */
    public int size() {
        return history.size();
    }
}
