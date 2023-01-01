package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.gui.GUIElement;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.guicreator.CloseBackElement;
import de.kripa.guitools.signgui.SignCompleteEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.AirElement;
import de.kripa.guitools.std.element.EmptyElement;
import de.kripa.guitools.std.element.button.GUIButton;
import de.kripa.guitools.std.element.button.GUIOpenButton;
import de.kripa.guitools.std.element.button.RotatingSelectButton;
import de.kripa.guitools.std.element.button.SignInputButton;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Stream;

import static de.kripa.guitools.GuiTools.PREFIX;

public class EnchantmentSelectGUI extends EmptyGUI {
    private enum DisplayMode {COMPATIBLE, WEAPON, TOOL, ARMOR, ALL_LOW, ALL_MAX}

    @AllArgsConstructor
    private class ModeButton implements GUIButton {
        @Getter private String title;
        @Getter private Material iconMaterial;
        @Getter private DisplayMode targetDisplayMode;

        @Override
        public boolean onClick(GUIElementClickEvent e) {
            currentDisplayMode = this.targetDisplayMode;
            this.playDing(e.getPlayer());
            return false;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(this.iconMaterial).setName(this.title)
                    .addLoreLine("§8Category")
                    .addLoreLine("")
                    .addLoreLine("§eClick to view")
                    .addLoreLine("§eenchantments!")
                    .setItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    .toItemStack();
        }
    }

    private static class EnchantmentSortSelector extends RotatingSelectButton {
        @Getter
        private boolean grouped = false;

        public EnchantmentSortSelector() {
            super(new ItemBuilder(Material.HOPPER).setName("§aSort by:").toItemStack(), "Enchant ID", "Alphabet", "Alphabet Reverse");
        }

        @Override
        public boolean onClick(GUIElementClickEvent e) {
            if (!e.isShiftClick()) {
                return super.onClick(e);
            }
            this.playDing(e.getPlayer());
            this.grouped = !this.grouped;
            return false;
        }

        @Override
        public ItemStack getIcon() {
            String[] originalOptions = this.getOptions();
            if (this.grouped) {
                this.setOptions(Arrays.stream(this.getOptions()).map(option -> option = "Grouped " + option).toArray(String[]::new));
            }
            ItemStack icon = super.getIcon();
            ItemMeta iconMeta = icon.getItemMeta();
            if (this.grouped) {
                iconMeta.setLore(iconMeta.getLore().stream().map(loreLine -> loreLine.replace("§b▶", "§5▶")).toList());
            }
            icon.setItemMeta(iconMeta);
            icon = new ItemBuilder(icon).addLoreLine("§5Shift click to toggle").addLoreLine("§5grouping of enchants").toItemStack();
            this.setOptions(originalOptions);

            return icon;
        }
    }

    @AllArgsConstructor
    private class EnchantmentButton implements GUIButton {
        @Getter private Enchantment enchantment;

        @Override
        public boolean onClick(GUIElementClickEvent e) {
            this.playDing(e.getPlayer());
            if (currentDisplayMode == DisplayMode.ALL_MAX) {
                toggleEnchantment(enchantment, enchantment.getMaxLevel());
                return false;
            }

            if (this.enchantment.getMaxLevel() != 1) {
                new EnchantmentLevelSelectGUI(this.enchantment).scheduleOpenGUI(e.getPlayer());
                return false;
            }

            toggleEnchantment(this.enchantment, 1);
            return false;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.ENCHANTED_BOOK)
                    .setName(getEnchantmentName(this.enchantment))
                    .addEnchant(this.enchantment, currentDisplayMode == DisplayMode.ALL_MAX ? this.enchantment.getMaxLevel() : 1)
                    .setLore(new ItemBuilder(toEdit).containsEnchantment(enchantment) ? List.of("", "§cClick to remove", "§cenchantment") : Collections.emptyList())
                    .toItemStack();
        }
    }

    @RequiredArgsConstructor
    private class RowButton implements GUIButton {
        @Getter
        private boolean increment;
        private boolean empty = false;

        public RowButton(boolean increment) {
            this.increment = increment;
        }

        @Override
        public boolean onClick(GUIElementClickEvent e) {
            if (!empty) {
                return false;
            }
            if (!e.isLeftClick()) {
                row = this.increment ? maxRow() + 1 : 0;
                return false;
            }

            row += this.increment ? 1 : -1;
            return false;
        }

        @Override
        public ItemStack getIcon() {
            this.empty = false;
            if (this.increment && (row >= maxRow())) {
                return new EmptyElement(displayModeColorMappings.get(currentDisplayMode)).getIcon();
            }
            if (!this.increment && (row <= 0)) {
                return new EmptyElement(displayModeColorMappings.get(currentDisplayMode)).getIcon();
            }
            this.empty = true;
            return new ItemBuilder(Material.ARROW)
                    .setName("§a" + (this.increment ? "Down" : "Up"))
                    .addLoreLine("")
                    .addLoreLine("§bRight click for end")
                    .addLoreLine("§eLeft click for " + (this.increment ? "down" : "up")).toItemStack();
        }
    }

    private class EnchantmentLevelSelectGUI extends EmptyGUI {
        @AllArgsConstructor
        private class ApplyEnchantButton implements GUIButton {
            @Getter private ItemStack toEdit;
            @Getter private Enchantment ench;
            @Getter private int level;


            @Override
            public boolean onClick(GUIElementClickEvent e) {
                this.playDing(e.getPlayer());
                toggleEnchantment(this.ench, this.level);
                return false;
            }

            @Override
            public ItemStack getIcon() {
                return new ItemBuilder(Material.ENCHANTED_BOOK)
                        .setName((new ItemBuilder(toEdit).containsEnchantment(this.ench) ? "§cRemove " : "§aApply ") + getRawEnchantmentName(this.ench))
                        .setLore(new ItemBuilder(toEdit).containsEnchantment(this.ench) ? List.of("", "§cClick to remove", "§cenchantment") : Collections.emptyList())
                        .addEnchant(ench, level)
                        .toItemStack();
            }
        }

        private class CustomLevelButton extends SignInputButton {
            public CustomLevelButton() {
                super(new ItemBuilder(Material.OAK_SIGN).setName("§aCustom level:").toItemStack(), "", "^^^^^^^^^^^^^^^", "Enter custom", "level here");
            }

            @Override
            protected void onSignComplete(SignCompleteEvent event, Player p, boolean playDing) {
                super.onSignComplete(event, p, false);
                if (this.getResult().matches("-?\\d+") && (0 < Integer.parseInt(this.getResult())) && (Integer.parseInt(this.getResult()) < 256)) {
                    ItemBuilder toEditBuilder = new ItemBuilder(toEdit);
                    if (toEditBuilder.containsEnchantment(enchant)) {
                        toEditBuilder.removeEnchantment(enchant);
                    }
                    toEditBuilder.addEnchant(enchant, Integer.parseInt(this.getResult()));
                    this.playDing(p);
                } else {
                    p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.1F);
                    p.sendMessage(PREFIX + "Please enter a valid integer between 1 and 255!");
                    this.setResult("");
                }
            }
        }

        @AllArgsConstructor
        private class EnchantmentGroupSelectButton extends EmptyElement {
            @Getter private Enchantment targetEnch;

            @Override
            public boolean onClick(GUIElementClickEvent e) {
                enchant = targetEnch;
                return super.onClick(e);
            }

            @Override
            public ItemStack getIcon() {
                if (!sort.grouped) {
                    return super.getIcon();
                }

                return new ItemBuilder(Material.ENCHANTED_BOOK)
                        .setName("§6" + getRawEnchantmentName(targetEnch))
                        .addEnchant(targetEnch, 1)
                        .addLoreLine("")
                        .addLoreLine("§eClick to change")
                        .addLoreLine("§eenchantment")
                        .toItemStack();
            }
        }
        @Getter
        private Enchantment enchant;
        private CustomLevelButton levelInput;

        public EnchantmentLevelSelectGUI(Enchantment enchant) {
            super("Select Enchantment Level", 3);
            this.enchant = enchant;
            this.levelInput = new CustomLevelButton();
        }

        @Override
        public Inventory render(Player p) {
            // Navigation
            this.setGUIElement(new GUIOpenButton(new ItemEditGUI(toEdit.clone()), toEdit), 4, 0);
            this.setGUIElement(new CloseBackElement(p), 3, 2);
            this.setGUIElement(this.levelInput, 4, 2);

            switch (enchant.getMaxLevel()) {
                case 2 -> {
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 1), 3, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 2), 5, 1);
                }
                case 3 -> {
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 1), 2, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 2), 4, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 3), 6, 1);
                }
                case 4 -> {
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 1), 2, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 2), 3, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 3), 5, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 4), 6, 1);
                }
                case 5 -> {
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 1), 2, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 2), 3, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 3), 4, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 4), 5, 1);
                    this.setGUIElement(new ApplyEnchantButton(toEdit.clone(), enchant, 5), 6, 1);

                }

            }

            if (!sort.isGrouped()) {
                return super.render(p);
            }

            if (Stream.of(Enchantment.DAMAGE_ALL, Enchantment.DAMAGE_UNDEAD, Enchantment.DAMAGE_ARTHROPODS).filter(enchant::equals).toList().size() > 0) {
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.DAMAGE_ALL), 0, 0);
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.DAMAGE_UNDEAD), 0, 1);
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.DAMAGE_ARTHROPODS), 0, 2);
            } else if (Stream.of(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.PROTECTION_EXPLOSIONS, Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE).filter(enchant::equals).toList().size() > 0) {
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.PROTECTION_ENVIRONMENTAL), 0, 0);
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.PROTECTION_EXPLOSIONS), 8, 0);
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.PROTECTION_FIRE), 0, 2);
                this.setGUIElement(new EnchantmentGroupSelectButton(Enchantment.PROTECTION_PROJECTILE), 8, 2);
            }
            return super.render(p);
        }
    }

    @Setter @Getter
    private ItemStack toEdit;
    @Getter
    private DisplayMode currentDisplayMode;
    private SignInputButton search;
    private EnchantmentSortSelector sort;

    private int row;

    public static final int ITEM_ID = 0;
    public static final int ALPHABET = 1;
    public static final int ALPHABET_REVERSE = 2;

    private static final Map<DisplayMode, String> displayModeColorMappings = new HashMap<>();
    private static final Map<DisplayMode, ChatColor> displayModeChatColorMappings = new HashMap<>();
    private static final List<Enchantment> groupFilter = List.of(
            Enchantment.PROTECTION_FIRE, Enchantment.PROTECTION_PROJECTILE, Enchantment.PROTECTION_EXPLOSIONS,
            Enchantment.DAMAGE_ARTHROPODS, Enchantment.DAMAGE_UNDEAD
            );

    static {
        displayModeColorMappings.put(DisplayMode.COMPATIBLE, "LIGHT_GRAY");
        displayModeChatColorMappings.put(DisplayMode.COMPATIBLE, ChatColor.GRAY);
        displayModeColorMappings.put(DisplayMode.WEAPON,     "RED");
        displayModeChatColorMappings.put(DisplayMode.WEAPON, ChatColor.RED);
        displayModeColorMappings.put(DisplayMode.TOOL,       "BLUE");
        displayModeChatColorMappings.put(DisplayMode.TOOL, ChatColor.BLUE);
        displayModeColorMappings.put(DisplayMode.ARMOR,      "CYAN");
        displayModeChatColorMappings.put(DisplayMode.ARMOR, ChatColor.AQUA);
        displayModeColorMappings.put(DisplayMode.ALL_LOW,    "PURPLE");
        displayModeChatColorMappings.put(DisplayMode.ALL_LOW, ChatColor.LIGHT_PURPLE);
        displayModeColorMappings.put(DisplayMode.ALL_MAX,    "ORANGE");
        displayModeChatColorMappings.put(DisplayMode.ALL_MAX, ChatColor.GOLD);
    }

    public EnchantmentSelectGUI(ItemStack toEdit) {
        this(toEdit, "", 0);
    }

    public EnchantmentSelectGUI(ItemStack toEdit, String searchString, int row) {
        super("Select enchantments", 6);
        this.toEdit = toEdit;
        this.currentDisplayMode = DisplayMode.COMPATIBLE;
        this.search = new SignInputButton(new ItemBuilder(Material.OAK_SIGN).setName("§aSearch:").toItemStack(), searchString, "^^^^^^^^^^^^^^^", "Enter your", "search here");
        this.sort = new EnchantmentSortSelector();
        this.row = row;
    }

    @Override
    public Inventory render(Player p) {
        // Sync row
        this.row = Math.min(this.row, maxRow());

        // Set background color
        Arrays.stream(this.getContent()).filter(content -> (content instanceof EmptyElement)).forEach(emptyElement ->
            ((EmptyElement) emptyElement).setColor(displayModeColorMappings.get(this.currentDisplayMode))
        );

        // Navigation buttons
        this.setGUIElement(new ModeButton("§7Compatible", Material.NETHER_STAR, DisplayMode.COMPATIBLE), 0, 0);
        this.setGUIElement(new ModeButton("§cWeapons", Material.DIAMOND_SWORD, DisplayMode.WEAPON), 0, 1);
        this.setGUIElement(new ModeButton("§9Tools", Material.DIAMOND_PICKAXE, DisplayMode.TOOL), 0, 2);
        this.setGUIElement(new ModeButton("§bArmor", Material.DIAMOND_CHESTPLATE, DisplayMode.ARMOR), 0, 3);
        this.setGUIElement(new ModeButton("§dAll enchants", Material.ENCHANTED_BOOK, DisplayMode.ALL_LOW), 0, 4);
        this.setGUIElement(new ModeButton("§6Max enchants", Material.ENCHANTED_GOLDEN_APPLE, DisplayMode.ALL_MAX), 0, 5);

        this.setGUIElement(new RowButton(false), 8, 0);
        this.setGUIElement(new RowButton(true), 8, 5);

        this.setGUIElement(new GUIOpenButton(new ItemEditGUI(this.toEdit), this.toEdit), 4, 0);

        this.setGUIElement(new CloseBackElement(p), 3, 5);
        this.setGUIElement(this.search, 4, 5);
        this.setGUIElement(this.sort, 5, 5);

        GUIElement[] fetchedItems = fetchItems();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 6; x++) {
                this.setGUIElement(fetchedItems[y * 6 + x], x + 2, y + 1);
            }
        }

        return super.render(p);
    }

    private GUIElement[] fetchItems() {
        List<Enchantment> enchantments = Arrays.stream(Enchantment.values())
                .filter(ench -> !groupFilter.contains(ench) || !this.sort.isGrouped())
                .filter(this::enchantmentMatchesDisplayMode)
                .filter(enchantment -> enchantment.getKey().getKey().toUpperCase().contains(this.search.getResult().toUpperCase()))
                .toList();

        if (this.sort.getSelectedOption() == ALPHABET) {
            enchantments = sortEnum(enchantments);
        } else if (this.sort.getSelectedOption() == ALPHABET_REVERSE) {
            enchantments = sortEnum(enchantments);
            Collections.reverse(enchantments);
        }

        GUIElement[] toDisp = new GUIElement[6*4];
        for (int i = 0; i < 6*4; i++) {
            if (i >= enchantments.size() - (this.row * 6)) {
                toDisp[i] = new AirElement();
                continue;
            }
            toDisp[i] = new EnchantmentButton(enchantments.get(i + (this.row * 6)));
        }

        return toDisp;
    }

    private int maxRow() {
        return (int) Math.max(Math.ceil(Arrays.stream(Enchantment.values())
                .filter(ench -> !groupFilter.contains(ench) || !this.sort.isGrouped())
                .filter(this::enchantmentMatchesDisplayMode)
                .filter(enchantment -> enchantment.getKey().getKey().toUpperCase().contains(this.search.getResult().toUpperCase()))
                .toList().size() / 6.0) - 4, 0);
    }

    private List<Enchantment> sortEnum(List<Enchantment> enchantments) {
        SortedMap<String, Enchantment> map = new TreeMap<>();
        for (Enchantment enchantment: enchantments) {
            map.put(enchantment.getKey().getKey(), enchantment);
        }
        return new ArrayList<>(map.values());
    }

    private boolean enchantmentMatchesDisplayMode(Enchantment ench) {
        switch (this.currentDisplayMode) {
            case COMPATIBLE -> {
                if (this.toEdit.getType() == Material.ENCHANTED_BOOK) {
                    return true;
                }
                return ench.canEnchantItem(this.toEdit);
            }
            case WEAPON -> {
                return Stream.of(Material.DIAMOND_SWORD, Material.BOW, Material.CROSSBOW, Material.TRIDENT)
                        .map(ItemStack::new)
                        .filter(ench::canEnchantItem)
                        .toList()
                        .size() > 0;
            }
            case TOOL -> {
                return ench.canEnchantItem(new ItemStack(Material.DIAMOND_PICKAXE));
            }
            case ARMOR -> {
                return Stream.of(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS)
                        .map(ItemStack::new)
                        .filter(ench::canEnchantItem)
                        .toList()
                        .size() > 0;
            }
            case ALL_LOW, ALL_MAX -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private String getEnchantmentName(Enchantment enchantment) {
        return "§o" + displayModeChatColorMappings.get(currentDisplayMode) + WordUtils.capitalizeFully(this.getRawEnchantmentName(enchantment));
    }

    private String getRawEnchantmentName(Enchantment enchantment) {
        return WordUtils.capitalizeFully(switch (enchantment.getKey().getKey().toUpperCase()) {
            case "VANISHING_CURSE" -> "Curse of Vanishing";
            case "BINDING_CURSE" -> "Curse of Binding";
            case "SWEEPING" -> "Sweeping Edge";
            default -> enchantment.getKey().getKey().replace('_', ' ');
        });
    }

    private void toggleEnchantment(Enchantment enchantment, int level) {
        ItemBuilder toEditBuilder = new ItemBuilder(toEdit);
        if (toEditBuilder.containsEnchantment(enchantment)) {
            toEditBuilder.removeEnchantment(enchantment);
            toEdit = toEditBuilder.toItemStack();
            return;
        }
        toEditBuilder.addUnsafeEnchantment(enchantment, level);
        toEdit = toEditBuilder.toItemStack();
    }
}
