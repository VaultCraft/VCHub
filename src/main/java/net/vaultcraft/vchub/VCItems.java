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

    public static final ItemStack GAME_SELECTOR = build(Material.COMPASS, "&d&lGame selector");
    public static final ItemStack HUB_SELECTOR = build(Material.WATCH, "&e&lLobby selector");
    public static final ItemStack SPEED_BOOST = build(Material.POTION, (byte)8226, "&a&lSpeed Boost");
    public static final ItemStack NO_ACTIVE_PERK = build(Material.BUCKET, "&c&l&nNo active perk!", "&5Unlock perks by donating today!", "&5Use &e\"/buy\"&5 for more information.");
    public static final ItemStack PICK_PERK = build(Material.BEACON, "&b&lSelect Perk");
    public static final ItemStack VAULT_COINS = build(Material.EMERALD, "&6&lTotal tokens: &f{0}", "&5Buy more tokens today.", "Use &e\"/buy\"&5 for more information.");
    public static final ItemStack PEARL_OF_TELEPORTATION = build(Material.ENDER_PEARL, "&5&l&nPearl of teleportation");
    public static final ItemStack MAP_BY = build(Material.PAPER, "&5&lMap by: &7&n4thewar");
    public static final ItemStack USER_SETTINGS = build(Material.REDSTONE_TORCH_ON, "&c&lUser settings...");

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
