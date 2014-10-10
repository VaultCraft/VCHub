package net.vaultcraft.vchub;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Created by Connor on 7/23/14. Designed for the VCHub project.
 */

public class VCItems {

    //hub items

    public static final ItemStack GAME_SELECTOR = build(Material.COMPASS, "&d&lGame Selector");
    public static final ItemStack STORE = build(Material.DIAMOND, "&b&lServer Store");
    public static final ItemStack SPEED_BOOST = build(Material.POTION, (byte)8226, "&a&lSpeed Boost");
    public static final ItemStack NO_ACTIVE_PERK = build(Material.BUCKET, "&c&l&nNo active perk!", "&5Unlock perks by donating today!", "&5Use &e\"/buy\"&5 for more information.");
    public static final ItemStack PICK_PERK = build(Material.BEACON, "&b&lSelect Perk");
    public static final ItemStack VAULT_COINS = build(Material.EMERALD, "&6&lTotal tokens: &f{0}", "&5Buy more tokens today.", "Use &e\"/buy\"&5 for more information.");
    public static final ItemStack PEARL_OF_TELEPORTATION = build(Material.ENDER_PEARL, "&5&l&nPearl of teleportation");
    public static final ItemStack MAP_BY = build(Material.PAPER, "&5&lMap by: &7&n4thewar");
    public static final ItemStack USER_SETTINGS = build(Material.REDSTONE_TORCH_ON, "&c&lUser settings...");

    //hub help menu items

    public static final ItemStack WELCOME = build(Material.WATCH, "&dWelcome to the &5&lVaultCraft Hub", "Help Menu&d. Hover over the", "items to read the tips!");
    public static final ItemStack WEBSITE = build(Material.MAP, "&dWebsite: https://vaultcraft.net");
    public static final ItemStack SHOP = build(Material.MAP, "&dStore: http://store.vaultcraft.net");
    public static final ItemStack GAME_SELECTOR_TUTORIAL = build(Material.COMPASS, "&d&lGame Selector", "&dThis is the &d&lGame Selector&d.", "It is used to teleport you", "to our different servers!");
    public static final ItemStack STORE_TUTORIAL  = build(Material.DIAMOND, "&b&lServer Store", "&dThis is the &b&lServer Store&d. It", "opens our shop menu where you", "can buy our various products!");
    public static final ItemStack SPEED_BOOST_TUTORIAL  = build(Material.POTION, (byte)8226, "&a&lSpeed Boost", "&dThis is the &a&lSpeed Boost&d. It", "makes you run faster around", "the hub!");
    public static final ItemStack NO_ACTIVE_PERK_TUTORIAL  = build(Material.BUCKET, "&c&l&nNo active perk!", "&dThis is the perk slot. If", "you have a perk active, it", "will show up here.");
    public static final ItemStack PICK_PERK_TUTORIAL  = build(Material.BEACON, "&b&lSelect Perk", "&dThis is the perk menu. It", "opens a GUI filled with our", "hub perks. Purchase perks at", "&6store.vaultcraft.net&d!");
    public static final ItemStack VAULT_COINS_TUTORIAL  = build(Material.EMERALD, "&6&lTotal tokens:", "&dThis item shows your total", "amount of tokens. Purchase tokens", "at &6store.vaultcraft.net&d!");
    public static final ItemStack PEARL_OF_TELEPORTATION_TUTORIAL  = build(Material.ENDER_PEARL, "&5&l&nPearl of teleportation", "&dThis item is the &5&l&nPearl of", "teleportation&d. Throw it and", "see what happens!");
    public static final ItemStack MAP_BY_TUTORIAL  = build(Material.PAPER, "&5&lMap by: &7&n4thewar", "&dThis item displays our builder,", "&64thewar. &dSay hello if you see", "him in game!");
    public static final ItemStack USER_SETTINGS_TUTORIAL  = build(Material.REDSTONE_TORCH_ON, "&c&lUser settings...", "&dThis is the &c&lUser settings", "&dmenu. It opens your personal", "settings that you can edit!");

    public static ItemStack build(Material type, String displayName, String... lore) {
        return build(type, (byte)0, displayName, lore);
    }

    public static ItemStack build(Material type, byte data, String displayName, String... lore) {
        ItemStack stack = new ItemStack(type, 1, (short)1, data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if (lore.length > 0) {
            List<String> l = Lists.newArrayList();
            for (String s : lore) {
                l.add(ChatColor.translateAlternateColorCodes('&', s));
            }
            meta.setLore(l);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
