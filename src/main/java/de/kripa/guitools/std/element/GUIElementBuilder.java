package de.kripa.guitools.std.element;

import de.kripa.guitools.gui.GUIElementClickEvent;
import de.kripa.guitools.std.ItemBuilder;
import de.kripa.guitools.std.element.button.GUIButton;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GUIElementBuilder extends ItemBuilder implements GUIButton {
    public interface GUIElementClickHandler {
        boolean onClick(GUIElementClickEvent event, GUIElementBuilder elementBuilder);
    }

    @Setter @Getter
    private GUIElementClickHandler guiElementClickHandler;

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m The material to create the GUIBuilder with.
     */
    public GUIElementBuilder(Material m, @NonNull GUIElementClickHandler handler) {
        super(m);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder over an existing Itemstack.
     *
     * @param is The Itemstack to create the GUIBuilder over.
     */
    public GUIElementBuilder(ItemStack is, @NonNull GUIElementClickHandler handler) {
        super(is);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m      The material of the item.
     * @param amount The amount of the item.
     */
    public GUIElementBuilder(Material m, int amount, @NonNull GUIElementClickHandler handler) {
        super(m, amount);
        this.guiElementClickHandler = handler;
    }

    /**
     * Create a new GUIBuilder from scratch.
     *
     * @param m          The material of the item.
     * @param amount     The amount of the item.
     * @param durability The durability of the item.
     */
    public GUIElementBuilder(Material m, int amount, byte durability, @NonNull GUIElementClickHandler handler) {
        super(m, amount, durability);
        this.guiElementClickHandler = handler;
    }

    /**
     * Clone the GUIElementBuilder into a new one.
     * @return The cloned instance.
     */
    public GUIElementBuilder clone(){
        return new GUIElementBuilder(itemStack, this.guiElementClickHandler);
    }
    /**
     * Change the durability of the item.
     * @param dur The durability to set it to.
     */
    public GUIElementBuilder setDurability(short dur){
        itemStack.setDurability(dur);
        return this;
    }
    /**
     * Set the displayname of the item.
     * @param name The name to change it to.
     */
    public GUIElementBuilder setName(String name){
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(name);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Add an unsafe enchantment.
     * @param ench The enchantment to add.
     * @param level The level to put the enchant on.
     */
    public GUIElementBuilder addUnsafeEnchantment(Enchantment ench, int level){
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            itemStack.addUnsafeEnchantment(ench, level);
            return this;
        }

        EnchantmentStorageMeta im = (EnchantmentStorageMeta) itemStack.getItemMeta();
        im.addStoredEnchant(ench, level, true);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Remove a certain enchant from the item.
     * @param ench The enchantment to remove
     */
    public GUIElementBuilder removeEnchantment(Enchantment ench){
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            itemStack.removeEnchantment(ench);
            return this;
        }
        EnchantmentStorageMeta im = (EnchantmentStorageMeta) itemStack.getItemMeta();
        im.removeStoredEnchant(ench);
        return this;
    }
    /**
     * Set the skull owner for the item. Works on skulls only.
     * @param owner The name of the skull's owner.
     */
    public GUIElementBuilder setSkullOwner(String owner){
        try{
            SkullMeta im = (SkullMeta) itemStack.getItemMeta();
            im.setOwner(owner);
            itemStack.setItemMeta(im);
        }catch(ClassCastException expected){}
        return this;
    }
    /**
     * Add an enchant to the item.
     * @param ench The enchant to add
     * @param level The level
     */
    public GUIElementBuilder addEnchant(Enchantment ench, int level){
        if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            ItemMeta im = itemStack.getItemMeta();
            im.addEnchant(ench, level, true);
            itemStack.setItemMeta(im);
            return this;
        }

        EnchantmentStorageMeta im = (EnchantmentStorageMeta) itemStack.getItemMeta();
        im.addStoredEnchant(ench, level, true);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Add multiple enchants at once.
     * @param enchantments The enchants to add.
     */
    public GUIElementBuilder addEnchantments(Map<Enchantment, Integer> enchantments){
        itemStack.addEnchantments(enchantments);
        return this;
    }
    /**
     * Sets infinity durability on the item by setting the durability to Short.MAX_VALUE.
     */
    public GUIElementBuilder setInfinityDurability(){
        itemStack.setDurability(Short.MAX_VALUE);
        return this;
    }
    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public GUIElementBuilder setLore(String... lore){
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(Arrays.asList(lore));
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Re-sets the lore.
     * @param lore The lore to set it to.
     */
    public GUIElementBuilder setLore(List<String> lore) {
        ItemMeta im = itemStack.getItemMeta();
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Remove a lore line.
     * @param line The lore to remove.
     */
    public GUIElementBuilder removeLoreLine(String line){
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if(!lore.contains(line))return this;
        lore.remove(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Remove a lore line.
     * @param index The index of the lore line to remove.
     */
    public GUIElementBuilder removeLoreLine(int index){
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if(index<0||index>lore.size())return this;
        lore.remove(index);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Add a lore line.
     * @param line The lore line to add.
     */
    public GUIElementBuilder addLoreLine(String line){
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(im.hasLore())lore = new ArrayList<>(im.getLore());
        lore.add(line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Add a lore line.
     * @param line The lore line to add.
     * @param pos The index of where to put it.
     */
    public GUIElementBuilder addLoreLine(String line, int pos){
        ItemMeta im = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        itemStack.setItemMeta(im);
        return this;
    }
    /**
     * Sets the armor color of a leather armor piece. Works only on leather armor pieces.
     * @param color The color to set it to.
     */
    public GUIElementBuilder setLeatherArmorColor(Color color){
        try{
            LeatherArmorMeta im = (LeatherArmorMeta) itemStack.getItemMeta();
            im.setColor(color);
            itemStack.setItemMeta(im);
        }catch(ClassCastException expected){}
        return this;
    }
    /**
     * Retrieves the itemstack from the GUIElementBuilder.
     * @return The itemstack created/modified by the GUIElementBuilder instance.
     */
    public ItemStack toItemStack(){
        return itemStack;
    }

    @Override
    public boolean onClick(GUIElementClickEvent e) {
        return this.guiElementClickHandler.onClick(e, this);
    }

    @Override
    public ItemStack getIcon() {
        return this.toItemStack();
    }
}
