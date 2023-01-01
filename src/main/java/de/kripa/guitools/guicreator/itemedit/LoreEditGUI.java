package de.kripa.guitools.guicreator.itemedit;

import de.kripa.guitools.GuiManager;
import de.kripa.guitools.GuiTools;
import de.kripa.guitools.anvilgui.AnvilGUI;
import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.guicreator.CloseBackElement;
import de.kripa.guitools.signgui.SignGUI;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIButton;
import de.kripa.guitools.std.element.button.GUIOpenButton;
import de.kripa.guitools.std.element.button.RotatingSelectButton;
import de.kripa.guitools.std.gui.EmptyGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.kripa.guitools.GuiManager.historyManager;
import static de.kripa.guitools.GuiManager.signManager;
import static de.kripa.guitools.GuiTools.PREFIX;

public class LoreEditGUI extends EmptyGUI {
    private class LoreLineSelectButton extends RotatingSelectButton {
        public LoreLineSelectButton() {
            super(clearLore(toEdit.clone()), getLore(toEdit));
        }

        public void updateOptions() {
            this.setOptions(getLore(toEdit));
        }

        private static String[] getLore(ItemStack itemStack) {
            if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null) {
                return new String[]{""};
            }
            if (!itemStack.getItemMeta().hasLore() || itemStack.getItemMeta().getLore() == null) {
                return new String[]{""};
            }

            return itemStack.getItemMeta().getLore().toArray(String[]::new);
        }

        private static ItemStack clearLore(ItemStack itemStack) {
            if (!itemStack.hasItemMeta() || itemStack.getItemMeta() == null) {
                return itemStack;
            }
            if (!itemStack.getItemMeta().hasLore() || itemStack.getItemMeta().getLore() == null) {
                return itemStack;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Collections.emptyList());
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
    }

    private class InputGUISelectButton extends RotatingSelectButton implements Listener {
        private final List<Player> anvilGUIOpen = new ArrayList<>();
        private final List<Player> chatGUIOpen = new ArrayList<>();

        public InputGUISelectButton() {
            super(new ItemBuilder(Material.OAK_SIGN).setName("§aInput GUI:").toItemStack(),
                    "Chat Input",
                    "Sign GUI",
                    "Anvil GUI"
                    );
            Bukkit.getPluginManager().registerEvents(this, GuiTools.plugin);
        }

        @Override
        @SuppressWarnings("deprecated")
        protected void finalize() throws Throwable {
            super.finalize();
            HandlerList.unregisterAll(this);
        }

        @Override
        public boolean onClick(GUIElementClickEvent e) {
            if (e.isShiftClick()) {
                return super.onClick(e);
            }
            Player p = e.getPlayer();
            this.playDing(p);
            historyManager.setPreserveHistory(p, true);
            switch (this.getSelectedOption()) {
                case 0 -> {
                    p.sendMessage(PREFIX + "Please type lore line in the chat!");
                    p.closeInventory();
                    chatGUIOpen.add(p);
                }
                case 1 ->
                    new SignGUI(signManager, event -> {
                        StringBuilder loreLineBuilder = new StringBuilder();
                        Arrays.stream(event.getLines()).map(line -> line = ChatColor.translateAlternateColorCodes('&', line)).forEach(loreLineBuilder::append);
                        this.setLoreLines(loreLineSelectButton.getSelectedOption(), loreLineBuilder.toString());
                        Bukkit.getScheduler().runTask(GuiTools.plugin, () -> {
                            historyManager.setPreserveHistory(p, false);
                            p.openInventory(GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p));
                        });
                    })
                            .withLines("", "", "", "")
                            .open(p);
                case 2 -> {
                    anvilGUIOpen.add(e.getPlayer());
                    new AnvilGUI.Builder()
                            .plugin(GuiTools.plugin)
                            .title("Set lore line")
                            .itemLeft(new ItemBuilder(toEdit.clone())
                                    .setName(new ItemBuilder(toEdit).getLoreLine(loreLineSelectButton.getSelectedOption()).equals("") ? " " : new ItemBuilder(toEdit).getLoreLine(loreLineSelectButton.getSelectedOption()).replace('§', '&'))
                                    .setLore(Collections.emptyList())
                                    .toItemStack())
                            .onComplete(completion -> {
                                this.setLoreLines(loreLineSelectButton.getSelectedOption(), ChatColor.translateAlternateColorCodes('&', completion.getText()));
                                p.openInventory(GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p));
                                historyManager.setPreserveHistory(p, false);
                                return Collections.emptyList();
                            })
                            .onClose(closeListener -> {
                                p.openInventory(GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p));
                                historyManager.setPreserveHistory(p, false);
                            })
                            .open(e.getPlayer());
                }
                default -> throw new IllegalStateException("Unexpected value: " + this.getSelectedOption());
            }
            return false;
        }

        @Override
        public ItemStack getIcon() {
            this.setIcon(new ItemBuilder(switch (this.getSelectedOption()) {
                case 0 -> Material.COMMAND_BLOCK;
                case 1 -> Material.OAK_SIGN;
                case 2 -> Material.ANVIL;
                default -> throw new IllegalStateException("Unexpected value: " + this.getSelectedOption());
            }).setName("§aInput mode:").toItemStack());
            ItemBuilder iconBuilder = new ItemBuilder(super.getIcon());
            iconBuilder.removeLoreLine(iconBuilder.getLore().size() - 1);
            iconBuilder.removeLoreLine(iconBuilder.getLore().size() - 1);
            iconBuilder.addLoreLine("§bShift + Right click for previous");
            iconBuilder.addLoreLine("§eShift + Left click for next");
            return iconBuilder.toItemStack();
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChat(AsyncPlayerChatEvent e) {
            Player p = e.getPlayer();
            if (!chatGUIOpen.contains(p)) {
                return;
            }
            Bukkit.getScheduler().runTask(GuiTools.plugin, () -> {
                this.setLoreLines(loreLineSelectButton.getSelectedOption(), ChatColor.translateAlternateColorCodes('&', e.getMessage()));
                Inventory toOpen = GuiManager.historyManager.getLastHistoryEntry(p).getGui().render(p);
                p.openInventory(toOpen);
                historyManager.setPreserveHistory(p, false);
            });
            e.setCancelled(true);
            chatGUIOpen.remove(p);
        }

        @EventHandler
        public void onAnvilPrepare(PrepareAnvilEvent e) {
            if (e.getViewers().size() < 1) {
                return;
            }
            Player p = (Player) e.getViewers().get(0);
            if (!anvilGUIOpen.contains(p)) {
                return;
            }
            try {
                e.getInventory().setItem(2,
                        new ItemBuilder(Objects.requireNonNull(e.getInventory().getItem(2)).clone())
                                .setName(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(Objects.requireNonNull(e.getInventory().getItem(2)).getItemMeta()).getDisplayName()))
                                .toItemStack());
            } catch (NullPointerException ignored) {

            }
            anvilGUIOpen.remove(p);
        }

        private void setLoreLines(int pos, String input) {
            Bukkit.broadcastMessage(PREFIX + "input = " + input);
            ItemBuilder toEditBuilder = new ItemBuilder(toEdit);
            if (inputTypeSelectButton.getSelectedOption() == 0) {
                Bukkit.broadcastMessage(PREFIX + "early exit");
                toEditBuilder.setLoreLine(pos, "§r§8" + input);
                Bukkit.broadcastMessage(toEditBuilder.getLoreLine(pos));
                toEdit = toEditBuilder.toItemStack();
                return;
            }
            toEditBuilder.removeLoreLine(pos);
            toEditBuilder.addLoreLines(pos, insertLineBreaks(input).toArray(String[]::new));
            toEditBuilder.getLore().forEach(Bukkit::broadcastMessage);
            toEdit = toEditBuilder.toItemStack();
        }

        private List<String> insertLineBreaks(String input) {
            String[] words = input.split("( |(?<=\\\\n)|(?=\\\\n))");
            List<String> lines = new ArrayList<>();
            String colorCode = "§r§7";
            StringBuilder currentLine = new StringBuilder(colorCode);
            for (String word : words) {
                Matcher colorCodeMatcher = Pattern.compile("([§&][a-f\\dklmnor])").matcher(word);
                while (colorCodeMatcher.find()) {
                    colorCode += colorCodeMatcher.group(0);
                }
                currentLine.append(word).append(" ");
                if (currentLine.length() > colorCode.length() + 40 || word.contains("\\n")) {
                    StringBuilder line = currentLine.deleteCharAt(currentLine.length() - 1);
                    if (word.contains("\\n")) {
                        line.delete(line.length() - "\\n".length(), line.length());
                    }
                    lines.add(line.toString());
                    currentLine = new StringBuilder(colorCode);
                }
            }
            StringBuilder line = currentLine.deleteCharAt(currentLine.length() - 1);
            lines.add(line.toString());
            return lines;
        }
    }

    private static class InputTypeSelectButton extends RotatingSelectButton {
        public InputTypeSelectButton() {
            super(new ItemBuilder(Material.WRITABLE_BOOK).setName("§aInput type:").toItemStack(), "Single line", "Auto-linebreak");
        }

        @Override
        public ItemStack getIcon() {
            this.setIcon(new ItemBuilder(switch (this.getSelectedOption()) {
                case 0 -> Material.WRITABLE_BOOK;
                case 1 -> Material.COMPARATOR;
                default -> throw new IllegalStateException("Unexpected value: " + this.getSelectedOption());
            }).setName("§aInput type:").toItemStack());
            return super.getIcon();
        }
    }

    private class AppendEmptyLineButton implements GUIButton {
        @Override
        public boolean onClick(GUIElementClickEvent e) {
            this.playDing(e.getPlayer());
            if (new ItemBuilder(toEdit).getLore().size() == 0) {
                toEdit = new ItemBuilder(toEdit).addLoreLine("").toItemStack();
            }
            toEdit = new ItemBuilder(toEdit).addLoreLine("").toItemStack();
            return false;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.GREEN_CONCRETE)
                    .setName("§aAdd empty lore line")
                    .addLoreLine("")
                    .addLoreLine("§eClick to add!")
                    .toItemStack();
        }
    }

    private class RemoveEmptyLineButton implements GUIButton {
        @Override
        public boolean onClick(GUIElementClickEvent e) {
            Player p = e.getPlayer();
            if (new ItemBuilder(toEdit).getLore().stream().filter(String::isBlank).toList().size() <= 0) {
                p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 0.1F);
                p.sendMessage(PREFIX + "Last line of item lore is not empty!");
                return false;
            }

            this.playDing(p);
            toEdit = new ItemBuilder(toEdit).removeLoreLine(new ItemBuilder(toEdit).getLore().size() - 1).toItemStack();
            return false;
        }

        @Override
        public ItemStack getIcon() {
            return new ItemBuilder(Material.RED_CONCRETE)
                    .setName("§cRemove empty lore line")
                    .addLoreLine("")
                    .addLoreLine("§eClick to remove!")
                    .toItemStack();
        }
    }

    @Setter @Getter
    private ItemStack toEdit;
    private final LoreLineSelectButton loreLineSelectButton;
    private final InputGUISelectButton inputGUISelectButton;
    private final InputTypeSelectButton inputTypeSelectButton;

    public LoreEditGUI(ItemStack toEdit) {
        super("Edit lore", 3);
        this.toEdit = toEdit;
        this.loreLineSelectButton = new LoreLineSelectButton();
        this.inputGUISelectButton = new InputGUISelectButton();
        this.inputTypeSelectButton = new InputTypeSelectButton();
    }

    @Override
    public Inventory render(Player p) {
        loreLineSelectButton.updateOptions();

        this.setGUIElement(new GUIOpenButton(new ItemEditGUI(this.toEdit.clone()), this.toEdit), 4, 0);
        this.setGUIElement(new CloseBackElement(p), 4, 2);

        this.setGUIElement(this.loreLineSelectButton, 1, 1);
        this.setGUIElement(this.inputGUISelectButton, 3, 1);
        this.setGUIElement(this.inputTypeSelectButton, 5, 1);
        this.setGUIElement(new AppendEmptyLineButton(), 7, 0);
        this.setGUIElement(new RemoveEmptyLineButton(), 7, 2);
        return super.render(p);
    }
}
