package net.vaultcraft.vchub.commands;

import net.vaultcraft.vcutils.command.ICommand;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CraftFest on 10/9/2014.
 */

public class VCHelp extends ICommand {

    public static Inventory help = Bukkit.createInventory(null, 27, "VaultCraft - Help Menu");

    public VCHelp(String name, Group permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void processCommand(Player player, String[] strings) {
        help.setItem(2, new ItemStack(VCItems.WEBSITE));
        help.setItem(4, new ItemStack(VCItems.WELCOME));
        help.setItem(6, new ItemStack(VCItems.SHOP));
        help.setItem(18, new ItemStack(VCItems.GAME_SELECTOR_TUTORIAL));
        help.setItem(19, new ItemStack(VCItems.STORE_TUTORIAL));
        help.setItem(20, new ItemStack(VCItems.SPEED_BOOST_TUTORIAL));
        help.setItem(21, new ItemStack(VCItems.NO_ACTIVE_PERK_TUTORIAL));
        help.setItem(22, new ItemStack(VCItems.PICK_PERK_TUTORIAL));
        help.setItem(23, new ItemStack(VCItems.VAULT_COINS_TUTORIAL));
        help.setItem(24, new ItemStack(VCItems.PEARL_OF_TELEPORTATION_TUTORIAL));
        help.setItem(25, new ItemStack(VCItems.MAP_BY_TUTORIAL));
        help.setItem(26, new ItemStack(VCItems.USER_SETTINGS_TUTORIAL));
        player.openInventory(help);
    }

}
