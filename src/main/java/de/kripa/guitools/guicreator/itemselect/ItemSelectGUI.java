package de.kripa.guitools.guicreator.itemselect;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.guicreator.amountselect.AmountSelectGUI;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.*;
import de.kripa.guitools.std.element.button.GUIOpenButton;
import de.kripa.guitools.std.element.button.RotatingSelectButton;
import de.kripa.guitools.std.element.button.SignInputButton;
import de.kripa.guitools.std.gui.EmptyGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static de.kripa.guitools.GuiTools.PREFIX;

public class ItemSelectGUI extends EmptyGUI implements CommandExecutor {
    private RotatingSelectButton sortSelect;
    private SignInputButton search;
    private PageButton prevPageButton, nextPageButton;

    private int lastPage;

    private final Material[] BLACKLIST = {Material.AIR};

    public static final int ITEM_ID = 0;
    public static final int ALPHABET = 1;
    public static final int ALPHABET_REVERSE = 2;

    public ItemSelectGUI(int page, String sortString, int search) {
        super(ChatColor.RESET + String.format("Select Item: [%d/%d]", page + 1, 0), 6);

        this.lastPage = page;
        this.nextPageButton = new PageButton(new ItemBuilder(Material.ARROW).setName("§aNext Page:").toItemStack(), page, 0, true);
        this.prevPageButton = new PageButton(new ItemBuilder(Material.ARROW).setName("§aPrevious Page:").toItemStack(), page, 0, false);
        this.sortSelect = new RotatingSelectButton(new ItemBuilder(Material.HOPPER).setName("§aSort by:").toItemStack(), search, "Item ID", "Alphabet", "Alphabet Reverse");
        this.search = new SignInputButton(new ItemBuilder(Material.OAK_SIGN).setName("§aSearch:").toItemStack(),
                sortString,
                "^^^^^^^^^^^^^^^",
                "Enter your",
                "search here");
    }

    @Override
    public void update() {
        // Synchronize current page
        int maxPage = (int) Math.ceil(this.itemSize(this.search.getResult()) / (4.0 * 7)) - 1;
        this.lastPage = Math.max(Math.min(maxPage, this.lastPage), 0);
        this.nextPageButton.setPage(Math.max(Math.min(maxPage, this.nextPageButton.getPage()), 0));
        this.prevPageButton.setPage(Math.max(Math.min(maxPage, this.prevPageButton.getPage()), 0));
        if (this.nextPageButton.getPage() != this.lastPage) {
            this.lastPage = this.nextPageButton.getPage();
            this.prevPageButton.setPage(this.nextPageButton.getPage());
        } else if (this.prevPageButton.getPage() != this.lastPage) {
            this.lastPage = this.prevPageButton.getPage();
            this.nextPageButton.setPage(this.prevPageButton.getPage());
        }

        this.prevPageButton.setRightClickPage(0);
        this.nextPageButton.setRightClickPage(maxPage);

        this.title = ChatColor.RESET + "Select Item ";
        this.meta = String.format("[%d/%d]: %s", this.lastPage + 1, maxPage + 1, this.search.getResult());
    }

    @Override
    public Inventory render(Player p) {

        // Search and back arrow
        if (GuiManager.historyManager.hasHistory(p)) {
            this.setGUIElement(this.search, 3, 5);
            this.setGUIElement(new BackElement(new ItemBuilder(Material.ARROW).setName("§aBack").toItemStack()), 4, 5);
        } else {
            this.setGUIElement(new EmptyElement(), 3, 5);
            this.setGUIElement(this.search, 4, 5);
        }

        // Sort Selector
        Material[] items = this.fetchItems(this.lastPage, this.search.getResult(), this.sortSelect.getSelectedOption());
        this.setGUIElement(this.sortSelect, 5, 5);
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 4; y++) {
                if (y * 7 + x < items.length) {
                    this.setGUIElement(new GUIOpenButton(new AmountSelectGUI(new ItemStack(items[y * 7 + x])), new ItemStack(items[y * 7 + x])), x + 1, y + 1);
                } else {
                    this.setGUIElement(new AirElement(), x + 1, y + 1);
                }
            }
        }

        // Page Manipulator
        int maxPage = this.nextPageButton.getRightClickPage();
        if (this.prevPageButton.getPage() > 0) {
            this.setGUIElement(this.prevPageButton, 0, 5);
        } else {
            this.setGUIElement(new EmptyElement(), 0, 5);
        }
        if (this.nextPageButton.getPage() < maxPage) {
            this.setGUIElement(this.nextPageButton, 8, 5);
        } else {
            this.setGUIElement(new EmptyElement(), 8, 5);
        }

        return super.render(p);
    }

    public int getPage() {
        return this.lastPage;
    }

    public String getSearch() {
        return this.search.getResult();
    }

    public int getSort() {
        return this.sortSelect.getSelectedOption();
    }

    private Material[] fetchItems(int page, String sortString, int sort) {
        List<Material> materials = Arrays.stream(Material.values()).filter(material -> material.isItem()
                && !Arrays.asList(BLACKLIST).contains(material)
                && material.getKey().getKey().toUpperCase().contains(sortString.toUpperCase().replace(' ', '_'))).toList();

        if (sort == ALPHABET) {
            materials = sortEnum(materials);
        } else if (sort == ALPHABET_REVERSE) {
            materials = sortEnum(materials);
            Collections.reverse(materials);
        }
        List<Material> toDisp = materials.subList(page * 4 * 7, Math.min((page + 1) * 4 * 7, materials.size()));
        return toDisp.toArray(Material[]::new);
    }

    private int itemSize(String sortString) {
        return Arrays.stream(Material.values()).filter(material -> material.isItem()
                && !Arrays.asList(BLACKLIST).contains(material)
                && material.getKey().getKey().toUpperCase().contains(sortString.toUpperCase().replace(' ', '_'))).toList().size();
    }

    private <T> List<T> sortEnum(List<T> e) {
        SortedMap<String, T> map = new TreeMap<>();
        for (T t: e) {
            map.put(t.toString(), t);
        }
        return new ArrayList<>(map.values());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + "You have to be a Player to use this command!");
            return true;
        }

        Player p = (Player) sender;
        new ItemSelectGUI(this.getPage(), this.getSearch(), this.getSort()).scheduleOpenGUI(p);
        return true;
    }
}
