package de.kripa.guitools;

import de.kripa.guitools.anvilgui.AnvilGUI;
import de.kripa.guitools.guicreator.itemselect.ItemSelectGUI;
import de.kripa.guitools.history.PlayerHistoryEntry;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Objects;

public final class GuiTools extends JavaPlugin implements Listener {
    public static GuiTools plugin;
    public static GuiManager guiManager;

    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&', "&5[&dGuiTools&5]: &f");

    @Override
    public void onEnable() {
        plugin = this;
        Objects.requireNonNull(getCommand("guibuilder")).setExecutor(new ItemSelectGUI(0, "", ItemSelectGUI.ITEM_ID));
        Objects.requireNonNull(getCommand("test")).setExecutor(this);
        guiManager = new GuiManager(this.getServer().getPluginManager());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(PREFIX + "You have to be a Player to use this command!");
            return true;
        }

        if (args.length < 1) {
            appendHistory(p, args);
            return true;
        }
        String subCmd = args[0];
        System.arraycopy(args, 1, args, 0, args.length - 1);
        switch (subCmd) {
            case "history":
                appendHistory(p, args);
                break;
            case "anviltest":
                anvilTest(p, args);
                break;
            default:
                p.sendMessage(PREFIX + "No such subcommand exists.");
        }
        return true;
    }

    private void appendHistory(Player p, String[] args) {
        EmptyGUI emptyGUI = new EmptyGUI("Empty GUI", 6);
        GuiManager.historyManager.appendHistory(new PlayerHistoryEntry(p, emptyGUI));
        p.sendMessage(PREFIX + "History appended");
        p.sendMessage(PREFIX + "hasCurrentGUI(" + p.getName() + ") = " + GuiManager.historyManager.hasCurrentGUI(p));
    }

    private void anvilTest(Player p, String[] args) {
        AnvilGUI.Builder anvilGUIBuilder = new AnvilGUI.Builder();
        anvilGUIBuilder.onComplete((completion) -> {
            if(completion.getText().equalsIgnoreCase("you")) {
                completion.getPlayer().sendMessage("You have magical powers!");
                return List.of(AnvilGUI.ResponseAction.close());
            } else {
                return List.of(AnvilGUI.ResponseAction.replaceInputText("Try again"));
            }
        });
        anvilGUIBuilder.text("What is the meaning of life?");
        anvilGUIBuilder.plugin(this);
        anvilGUIBuilder.open(p);
    }
}
